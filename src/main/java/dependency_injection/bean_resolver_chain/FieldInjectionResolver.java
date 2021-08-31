package dependency_injection.bean_resolver_chain;

import dependency_injection.DojoContainer;
import dependency_injection.exception.DojoContextInitException;
import dependency_injection.utils.QualifierUtility;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;

public class FieldInjectionResolver extends BeanResolver {
    public FieldInjectionResolver(final DojoContainer container) {
        this.currentContainer = container;
        this.containerBeanFactory = container.getResolvedBeansFactory();
    }

    @Override
    public void resolveBean(final Class<?> beanClass) {
        final Field[] classFields = beanClass.getDeclaredFields();
        final boolean hasFieldAnnotated = Arrays.stream(classFields)
                .anyMatch(field -> field.isAnnotationPresent(Inject.class));
        final List<Field> injectionPointFields = Arrays.stream(classFields)
                .filter(field -> field.isAnnotationPresent(Inject.class))
                .collect(Collectors.toList());

        checkFieldInjectionEligibility(injectionPointFields);

        if (hasFieldAnnotated) {
            resolveBeanFieldInjection(beanClass, injectionPointFields);
        }
        nextResolver.resolveBean(beanClass);
    }

    private void checkFieldInjectionEligibility(final List<Field> injectionPointFields) {
        final Map<? extends Class<?>, Long> fieldsByType =
                injectionPointFields.stream().collect(Collectors.groupingBy(Field::getType, Collectors.counting()));
        final boolean moreInjectionForSameType = fieldsByType.values().stream().anyMatch(aLong -> aLong > 1);
        if (moreInjectionForSameType) {
            throw new DojoContextInitException("more then one filed of same Type annotated with @Inject annotation");
        }
    }

    private void resolveBeanFieldInjection(final Class<?> aClass, final List<Field> injectionPointFields) {
        injectionPointFields.forEach(field -> resolve(aClass, field));
    }

    private void resolve(Class<?> aClass, Field field) {
        try {
            final Object resolvedBean = retrieveDependencyBean(field);
            final Object classInstance = setFieldValue(aClass, field, resolvedBean);
            containerBeanFactory.put(aClass.getSimpleName(), classInstance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private Object setFieldValue(Class<?> aClass, Field field, Object resolvedBean)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final Object classInstance;
        if (!containerBeanFactory.containsKey(aClass.getSimpleName())) {
            classInstance = aClass.getDeclaredConstructor().newInstance();
        } else {
            classInstance = containerBeanFactory.get(aClass.getSimpleName());
        }
        final boolean canAccess = field.canAccess(classInstance);
        field.setAccessible(true);
        field.set(classInstance, resolvedBean);
        field.setAccessible(canAccess);
        return classInstance;
    }

    private Object retrieveDependencyBean(Field field) {
        Class<?> fieldType = field.getType();

        String filedTypeSimpleName = fieldType.getSimpleName();

        if (!containerBeanFactory.containsKey(filedTypeSimpleName)) {
            currentContainer.register(fieldType);
        }

        if (fieldType.isInterface()) {
            filedTypeSimpleName = Optional.ofNullable(field.getAnnotation(Named.class))
                    .map(Named::value)
                    .orElseGet(() -> QualifierUtility.retrieveCustomQualifiedName(field.getAnnotations()));
        }
        return containerBeanFactory.get(filedTypeSimpleName);
    }
}
