package dependency_injection.bean_resolver_chain;

import dependency_injection.DojoContainer;
import dependency_injection.exception.DojoContextInitException;
import dependency_injection.utils.QualifierUtility;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.inject.Inject;

public class ConstructorInjectionResolver extends BeanResolver {
    public ConstructorInjectionResolver(final DojoContainer container) {
        this.currentContainer = container;
        this.containerBeanFactory = container.getResolvedBeansFactory();
    }

    @Override
    public void resolveBean(final Class<?> beanClass) {
        final Constructor<?>[] beanConstructors = beanClass.getConstructors();
        final List<Constructor<?>> injectionAnnotatedConstructors = checkConstructionInjectionEligibility(beanClass, beanConstructors);
        final boolean hasConstructorAnnotated = Arrays.stream(beanConstructors)
                .anyMatch(constructor -> constructor.isAnnotationPresent(Inject.class));
        if (hasConstructorAnnotated) {
            final Constructor<?> injectionPointConstructor = injectionAnnotatedConstructors.get(0);
            resolveBeanWithSelectedConstructor(beanClass, injectionPointConstructor);
        }

        nextResolver.resolveBean(beanClass);
    }

    private List<Constructor<?>> checkConstructionInjectionEligibility(final Class<?> beanClass, final Constructor<?>[] beanConstructors) {
        final List<Constructor<?>> injectionAnnotatedConstructors =
                Arrays.stream(beanConstructors)
                        .filter(constructor1 -> constructor1.isAnnotationPresent(Inject.class))
                        .collect(Collectors.toList());

        if (injectionAnnotatedConstructors.size() > 1) {
            throw new DojoContextInitException(
                    "more then one constructor annotated with @Inject annotation on class:" + beanClass.getName());
        }
        return injectionAnnotatedConstructors;
    }

    private void resolveBeanWithSelectedConstructor(final Class<?> aClass, final Constructor<?> injectionPointConstructor) {
        try {
            final Object newInstance;
            if (injectionPointConstructor.getParameterCount() > 0) {
                newInstance = instantiateWithMultiArgument(injectionPointConstructor);
            } else {
                newInstance = injectionPointConstructor.newInstance();
            }
            containerBeanFactory.put(aClass.getSimpleName(), newInstance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new DojoContextInitException("failed to instantiate with constructor injection");
        }
    }

    private Object instantiateWithMultiArgument(final Constructor<?> injectionPointConstructor)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        List<Object> constructorArguments = new LinkedList<>();
        Class<?>[] parameterTypes = injectionPointConstructor.getParameterTypes();
        Annotation[][] parameterAnnotations = injectionPointConstructor.getParameterAnnotations();

        IntStream.range(0, parameterTypes.length).forEachOrdered(index -> {
            Class<?> currentParameterClass = parameterTypes[index];
            String simpleName = currentParameterClass.getSimpleName();

            if (!containerBeanFactory.containsKey(simpleName)) {
                currentContainer.register(currentParameterClass);
            }

            if (currentParameterClass.isInterface()) {
                Annotation[] currentParameterAnnotations = parameterAnnotations[index];
                simpleName = QualifierUtility.retrieveImplementationNameByQualifier(currentParameterAnnotations);
            }

            final Object resolvedBean = containerBeanFactory.get(simpleName);
            constructorArguments.add(resolvedBean);
        });

        return injectionPointConstructor.newInstance(constructorArguments.toArray());
    }

}