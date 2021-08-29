package dependency_injection;

import dependency_injection.annotation.DojoComponent;
import dependency_injection.exception.DojoContextInitException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
        final boolean hasConstructorAnnotated = Arrays.stream(beanConstructors)
                .anyMatch(constructor -> constructor.isAnnotationPresent(Inject.class));
        final Field[] classFields = aClass.getDeclaredFields();
        final boolean hasFieldAnnotated = Arrays.stream(classFields)
                .anyMatch(field -> field.isAnnotationPresent(Inject.class));
        if (hasConstructorAnnotated) {
            final Constructor<?> injectionPointConstructor = selectInjectionPointConstructor(aClass, beanConstructors);
            resolveBeanWithSelectedConstructor(aClass, injectionPointConstructor);
        } else if (hasFieldAnnotated) {
            final List<Field> injectionPointFields =
                    Arrays.stream(classFields).filter(field -> field.isAnnotationPresent(Inject.class)).collect(Collectors.toList());
            checkFieldInjectionEligibility(injectionPointFields);
            resolveBeanFieldInjection(aClass, injectionPointFields);
        } else {
            resolveDefaultConstructorCase(aClass);
        }
    }

    private void resolveDefaultConstructorCase(final Class<?> aClass) {
        try {
            resolvedBeansFactory.put(aClass.getSimpleName(), aClass.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void resolveBeanFieldInjection(final Class<?> aClass, final List<Field> injectionPointFields) {
        injectionPointFields.forEach(field -> {
            try {
                final String filedTypeSimpleName = field.getType().getSimpleName();
                if (!resolvedBeansFactory.containsKey(filedTypeSimpleName)) {
                    resolveBean(field.getType());
                }
                final Object resolvedBean = resolvedBeansFactory.get(filedTypeSimpleName);
                final Object classInstance;
                if (!resolvedBeansFactory.containsKey(aClass.getSimpleName())) {
                    classInstance = aClass.getDeclaredConstructor().newInstance();
                } else {
                    classInstance = resolvedBeansFactory.get(aClass.getSimpleName());
                }
                final boolean canAccess = field.canAccess(classInstance);
                field.setAccessible(true);
                field.set(classInstance, resolvedBean);
                field.setAccessible(canAccess);
                resolvedBeansFactory.put(aClass.getSimpleName(), classInstance);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });
    }

    private void checkFieldInjectionEligibility(final List<Field> injectionPointFields) {
        final Map<? extends Class<?>, Long> fieldsByType =
                injectionPointFields.stream().collect(Collectors.groupingBy(Field::getType, Collectors.counting()));
        final boolean moreInjectionForSameType = fieldsByType.values().stream().anyMatch(aLong -> aLong > 1);
        if (moreInjectionForSameType) {
            throw new DojoContextInitException("more then one filed of same Type annotated with @Inject annotation");
        }
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

        return injectionAnnotatedConstructors.get(0);
    }

    public String initMessage() {
        return DEFAULT_INIT_MESSAGE;
    }

    public Object retrieveBean(final String beanName) {
        return resolvedBeansFactory.get(beanName);
    }

    public void register(final Object manualAddedBean) {
        resolveBean(manualAddedBean.getClass());
    }
}
