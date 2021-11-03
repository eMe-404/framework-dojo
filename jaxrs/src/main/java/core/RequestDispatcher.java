package core;

import models.MatchedResource;
import models.RootResourceClassMatchingResult;
import utils.URIHelper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RequestDispatcher {
    public MatchedResource matchRequestHandler(HttpServletRequest request, List<Class<?>> rootResourceClasses, ServletContext servletContext) {
        RootResourceClassMatchingResult candidateResourceClasses = findCandidateResourceClasses(request.getRequestURI(), rootResourceClasses);
        final Set<Method> matchedCandidateMethods = matchResourceMethods(candidateResourceClasses.getNotMatchedCapturingGroup(), candidateResourceClasses.getMatchedRootClasses(), servletContext);
        Method requestHandler = matchedCandidateMethods.stream().filter(this::checkRequestDesignator).findAny().orElseThrow(NotSupportedException::new);

        return MatchedResource.builder()
                .matchedResourceMethod(requestHandler)
                .matchedResourceClass(requestHandler.getDeclaringClass())
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

    public Set<Method> matchResourceMethods(String capturingGroup, Set<Class<?>> rootResourceClasses, ServletContext servletContext) {
        Set<Method> allResourceMethods = rootResourceClasses.stream()
                .flatMap(clazz -> Arrays.stream(clazz.getDeclaredMethods()))
                .collect(Collectors.toSet());

        if (Objects.isNull(capturingGroup) || capturingGroup.equals("/")) {
            return allResourceMethods.stream()
                    .filter(method -> !method.isAnnotationPresent(Path.class) && checkRequestDesignator(method))
                    .collect(Collectors.toSet());
        }

        Optional<Method> optionalMatchedSubResourceMethod = allResourceMethods.stream()
                .filter(this::checkRequestDesignator)
                .filter(method -> {
                    String declaredPath;
                    if (method.isAnnotationPresent(Path.class)) {
                        declaredPath = method.getAnnotation(Path.class).value();
                    } else {
                        declaredPath = method.getDeclaringClass().getAnnotation(Path.class).value();
                    }
                    return capturingGroup.matches(URIHelper.normalizePath(declaredPath));
                })
                .max(Comparator.comparingInt((Method method) -> sortByLiteralCharacters(capturingGroup, method.getAnnotation(Path.class).value())));

        if (optionalMatchedSubResourceMethod.isPresent()) {
            return Collections.singleton(optionalMatchedSubResourceMethod.get());
        }

        Optional<Method> optionalSubLocator = allResourceMethods.stream()
                .filter(method -> method.getDeclaredAnnotations().length == 1 && method.isAnnotationPresent(Path.class))
                .filter(method -> capturingGroup.matches(URIHelper.normalizePath(method.getAnnotation(Path.class).value())))
                .findAny();
        //TODO: should only found one candidate locator, if more found should throw exception

        if (optionalSubLocator.isEmpty()) {
            throw new NotFoundException();
        }

        Method resourceLocatorMethod = optionalSubLocator.get();
        String pathTemplate = URIHelper.normalizePath(resourceLocatorMethod.getAnnotation(Path.class).value());
        Matcher pathMatcher = Pattern.compile(pathTemplate).matcher(capturingGroup);

        if (pathMatcher.matches()) {
            servletContext.setAttribute("capturingGroup", pathMatcher.group(1));
            return matchResourceMethods(pathMatcher.group(pathMatcher.groupCount()), Set.of(resourceLocatorMethod.getReturnType()), servletContext);
        }

        return Collections.emptySet();
    }

    private boolean checkRequestDesignator(Method method) {
        return Arrays.stream(method.getAnnotations())
                .anyMatch(annotation -> annotation.annotationType().isAnnotationPresent(HttpMethod.class));
    }
}
