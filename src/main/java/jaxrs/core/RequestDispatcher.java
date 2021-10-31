package jaxrs.core;

import jaxrs.model.MatchedResource;
import jaxrs.model.RootResourceClassMatchingResult;
import jaxrs.utils.URIHelper;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RequestDispatcher {
    public MatchedResource findMatchedSourceMethod(String requestUriPath, List<Class<?>> rootResourceClasses) {

        return MatchedResource.builder()
                .matchedResourceMethod(null)
                .matchedResourceClass(null)
                .build();
    }

    public RootResourceClassMatchingResult findCandidateResourceClasses(String requestUriPath, List<Class<?>> rootResourceClasses) {
        Map<Class<?>, String> classesWithPath = rootResourceClasses.stream()
                .collect(Collectors.toMap(clazz -> clazz, clazz -> clazz.getAnnotation(Path.class).value()));

        Set<Class<?>> matchedClasses = classesWithPath.keySet().stream()
                .filter(clazz -> requestUriPath.matches(URIHelper.normalizePath(classesWithPath.get(clazz))))
                .sorted(Comparator.comparing(clazz -> sortByLiteralCharacters(requestUriPath, classesWithPath.get(clazz))))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Class<?> chosenClass = matchedClasses.stream().findFirst().orElseThrow(BadRequestException::new);
        Matcher selectedRegexMatcher = Pattern.compile(URIHelper.normalizePath(classesWithPath.get(chosenClass))).matcher(requestUriPath);
        String notMatchedCapturingGroup = null;
        if (selectedRegexMatcher.find()) {
            notMatchedCapturingGroup = selectedRegexMatcher.group(1);
        }

        return RootResourceClassMatchingResult.builder()
                .notMatchedCapturingGroup(notMatchedCapturingGroup)
                .matchedRootClasses(matchedClasses)
                .build();
    }

    private int sortByLiteralCharacters(String requestUriPath, String resourcePath) {
        Pattern pattern = Pattern.compile(URIHelper.normalizePath(resourcePath));
        Matcher matcher = pattern.matcher(requestUriPath);
        if (matcher.find()) {
            int matchingGroupStartPosition = matcher.start(1);
            return matchingGroupStartPosition > 0 ? resourcePath.substring(0, matchingGroupStartPosition).length()
                    : resourcePath.length();
        }
        return 0;
    }

    public Set<Method> matchResourceMethods(String capturingGroup, List<Class<?>> rootResourceClasses) {
        Set<Method> allResourceMethods = rootResourceClasses.stream()
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods()))
                .collect(Collectors.toSet());

        if (Objects.isNull(capturingGroup) || capturingGroup.equals("/")) {
            return allResourceMethods;
        }

        Method matchedSubResourceMethod = allResourceMethods.stream()
                .filter(method -> method.isAnnotationPresent(Path.class)
                        && Arrays.stream(method.getAnnotations())
                        .anyMatch(annotation -> annotation.annotationType().isAnnotationPresent(HttpMethod.class)))
                .filter(method -> capturingGroup.matches(URIHelper.normalizePath(method.getAnnotation(Path.class).value())))
                .max(Comparator.comparingInt((Method method) -> sortByLiteralCharacters(capturingGroup, method.getAnnotation(Path.class).value())))
                .orElseThrow(NotFoundException::new);

        return Collections.singleton(matchedSubResourceMethod);
    }
}
