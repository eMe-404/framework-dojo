package models;

import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Method;

@Builder
@Getter
public class MatchedMethod {
    private Method method;
    private String finalCapturingGroup;
}
