package dependency_injection.utils;

import dependency_injection.exception.DojoContextInitException;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;
import javax.inject.Qualifier;

public interface QualifierUtility {
    static String retrieveImplementationNameByQualifier(Annotation[] currentParameterAnnotations) {
        Optional<Annotation> namedQualifier = Arrays.stream(currentParameterAnnotations)
                .filter(annotation -> annotation.annotationType().equals(Named.class))
                .findAny();

        Optional<Named> namedAnnotation = namedQualifier
                .map(Named.class::cast);
        if (namedAnnotation.isPresent()) {
            return namedAnnotation.get().value();
        }

        return retrieveCustomQualifiedName(currentParameterAnnotations);
    }

    static String retrieveCustomQualifiedName(final Annotation[] annotations) {
        return Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(Qualifier.class))
                .findAny()
                .map(annotation -> annotation.annotationType().getSimpleName())
                .orElseThrow(DojoContextInitException::new);
    }

}
