package dependency_injection;

import dependency_injection.annotation.DojoComponent;
import dependency_injection.exception.DojoContextInitException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.reflections.Reflections;

public class DojoContainer {
    public static final String DEFAULT_PREFIX = "";
    public static final String DEFAULT_INIT_MESSAGE = "container initialized successfully";
    private final HashMap<String, Object> resolvedBeansFactory = new HashMap<>();

    public static DojoContainer initWithManagedBeans() {
        final DojoContainer dojoContainer = new DojoContainer();
        dojoContainer.registerBeans();
        return dojoContainer;
    }

    private void registerBeans() {
        final Reflections reflections = new Reflections(DEFAULT_PREFIX);
        final Set<Class<?>> managedBeans = reflections.getTypesAnnotatedWith(DojoComponent.class);
        managedBeans.forEach(this::resolveBean);
    }

    private void resolveBean(final Class<?> aClass) {
        if (resolvedBeansFactory.containsKey(aClass.getSimpleName())) {
            return;
        }

        final Constructor<?>[] beanConstructors = aClass.getConstructors();
        final Constructor<?> injectionPointConstructor = selectInjectionPointConstructor(aClass, beanConstructors);

        resolveBeanWithSelectedConstructor(aClass, injectionPointConstructor);
    }

    private void resolveBeanWithSelectedConstructor(final Class<?> aClass, final Constructor<?> injectionPointConstructor) {
        try {
            final Object newInstance;
            if (injectionPointConstructor.getParameterCount() > 0) {
                newInstance = instantiateWithMultiArgument(injectionPointConstructor);
            } else {
                newInstance = injectionPointConstructor.newInstance();
            }
            resolvedBeansFactory.put(aClass.getSimpleName(), newInstance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Object instantiateWithMultiArgument(final Constructor<?> injectionPointConstructor)
            throws InstantiationException, IllegalAccessException, InvocationTargetException {
        List<Object> constructorArguments = new LinkedList<>();
        for (Class<?> parameterType : injectionPointConstructor.getParameterTypes()) {
            if (!resolvedBeansFactory.containsKey(parameterType.getSimpleName())) {
                resolveBean(parameterType);
            }
            final Object resolvedBean = resolvedBeansFactory.get(parameterType.getSimpleName());
            constructorArguments.add(resolvedBean);
        }
        return injectionPointConstructor.newInstance(constructorArguments.toArray());
    }

    private Constructor<?> selectInjectionPointConstructor(final Class<?> aClass, final Constructor<?>[] beanConstructors) {
        final List<Constructor<?>> injectionAnnotatedConstructors =
                Arrays.stream(beanConstructors)
                        .filter(constructor -> constructor.isAnnotationPresent(Inject.class))
                        .collect(Collectors.toList());

        if (injectionAnnotatedConstructors.size() > 1) {
            throw new DojoContextInitException(
                    "more then one constructor annotated with @Inject annotation on class:" + aClass.getName());
        }

        if (injectionAnnotatedConstructors.isEmpty()) {
            final Optional<Constructor<?>> defaultConstructor =
                    Arrays.stream(beanConstructors).filter(constructor -> constructor.getParameterCount() == 0).findFirst();
            return defaultConstructor.
                    orElseThrow(() -> new DojoContextInitException("no eligible constructor for bean resolution"));
        }

        return injectionAnnotatedConstructors.get(0);
    }

    public String initMessage() {
        return DEFAULT_INIT_MESSAGE;
    }

    public Object retrieveBean(final String beanName) {
        return resolvedBeansFactory.get(beanName);
    }
}
