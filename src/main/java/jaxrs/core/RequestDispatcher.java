package jaxrs.core;

import jaxrs.model.MatchedResource;
import jaxrs.model.RootResourceClassMatchingResult;
import jaxrs.utils.URIHelper;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import java.util.*;
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
                .collect(Collectors.toSet());

        if (matchedClasses.isEmpty()) {
            throw new NotFoundException();
        }

        return RootResourceClassMatchingResult.builder()
                .notMatchedCapturingGroup(null)
                .matchedRootClasses(matchedClasses)
                .build();
    }
}
