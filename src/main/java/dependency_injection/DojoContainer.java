package dependency_injection;

import dependency_injection.bean_resolver_chain.BeanResolver;
import dependency_injection.annotation.DojoComponent;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Qualifier;
import org.reflections.Reflections;

public class DojoContainer {
    public static final String DEFAULT_PREFIX = "";
    public static final String DEFAULT_INIT_MESSAGE = "container initialized successfully";
    private final HashMap<String, Object> resolvedBeansFactory = new HashMap<>();
    private BeanResolver beanResolver;
    private Reflections reflections = new Reflections(DEFAULT_PREFIX);

    public static DojoContainer initWithManagedBeans() {
        final DojoContainer dojoContainer = new DojoContainer();
        dojoContainer.beanResolver = BeanResolver.configDefaultBeanResolverChain(dojoContainer);
        dojoContainer.registerBeans();
        return dojoContainer;
    }

    public static DojoContainer initWithDefaultSetup() {
        final DojoContainer dojoContainer = new DojoContainer();
        dojoContainer.beanResolver = BeanResolver.configDefaultBeanResolverChain(dojoContainer);
        return dojoContainer;
    }

    public Map<String, Object> getResolvedBeansFactory() {
        return resolvedBeansFactory;
    }

    private void registerBeans() {
        final Set<Class<?>> managedBeans = reflections.getTypesAnnotatedWith(DojoComponent.class);
        managedBeans.forEach(this::resolveBean);
    }

    private void resolveBean(final Class<?> aClass) {
        if (resolvedBeansFactory.containsKey(aClass.getSimpleName())) {
            return;
        }

        if (aClass.isInterface()) {
            resolveImplementation(aClass);
            return;
        }

        beanResolver.resolveBean(aClass);
    }

    private void resolveImplementation(final Class<?> aClass) {
        Set<Class<?>> implementations = Collections.unmodifiableSet(this.reflections.getSubTypesOf(aClass));
        implementations.forEach(implementClass -> {
            if (resolvedBeansFactory.containsKey(implementClass.getSimpleName())) {
                return;
            }

            beanResolver.resolveBean(implementClass);
            tryToAddCustomQualifiedBeanReference(implementClass);
        });
    }

    private void tryToAddCustomQualifiedBeanReference(final Class<?> implementClass) {
        final Optional<? extends Class<? extends Annotation>> optionalCustomQualifierName = Arrays.stream(implementClass.getAnnotations())
                .map(Annotation::annotationType)
                .filter(annotationType -> annotationType.isAnnotationPresent(Qualifier.class))
                .findAny();

        if (optionalCustomQualifierName.isPresent()) {
            final Class<? extends Annotation> customQualifier = optionalCustomQualifierName.get();
            if (resolvedBeansFactory.containsKey(customQualifier.getSimpleName())) {
                return;
            }
            final Object implementationManagedInstance = resolvedBeansFactory.get(implementClass.getSimpleName());
            resolvedBeansFactory.put(customQualifier.getSimpleName(), implementationManagedInstance);
        }
    }

    public String initMessage() {
        return DEFAULT_INIT_MESSAGE;
    }

    public Object retrieveBean(final String beanName) {
        return resolvedBeansFactory.get(beanName);
    }

    public void register(final Class<?> aClass) {
        resolveBean(aClass);
    }

}
