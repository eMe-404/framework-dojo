package jaxrs.model;

import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Method;

@Getter
@Builder
public class MatchedResource {
    private Class<?> matchedResourceClass;
    private Method matchedResourceMethod;
}