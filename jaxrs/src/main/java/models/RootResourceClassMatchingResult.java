package models;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class RootResourceClassMatchingResult {
    private String notMatchedCapturingGroup;
    private Set<Class<?>> matchedRootClasses;
}
