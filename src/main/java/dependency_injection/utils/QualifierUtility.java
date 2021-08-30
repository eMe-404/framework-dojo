package dependency_injection.utils;

import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

public interface QualifierUtility {
    static String retrieveNamedAnnotationValue(Annotation[] currentParameterAnnotations) {
        Optional<Annotation> namedQualifier = Arrays.stream(currentParameterAnnotations)
                .filter(annotation -> annotation.annotationType().equals(Named.class))
                .findAny();
        Named namedAnnotation = namedQualifier
                .map(annotation -> (Named) annotation)
                .orElseThrow(RuntimeException::new);
        return namedAnnotation.value();
    }
}
