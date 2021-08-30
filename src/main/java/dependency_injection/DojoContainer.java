package dependency_injection;

import dependency_injection.bean_resolver_chain.BeanResolver;
import dependency_injection.annotation.DojoComponent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.reflections.Reflections;

public class DojoContainer {
    public static final String DEFAULT_PREFIX = "";
    public static final String DEFAULT_INIT_MESSAGE = "container initialized successfully";
    private final HashMap<String, Object> resolvedBeansFactory = new HashMap<>();
    private BeanResolver beanResolver;

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
        final Reflections reflections = new Reflections(DEFAULT_PREFIX);
        final Set<Class<?>> managedBeans = reflections.getTypesAnnotatedWith(DojoComponent.class);
        managedBeans.forEach(this::resolveBean);
    }

    private void resolveBean(final Class<?> aClass) {
        if (resolvedBeansFactory.containsKey(aClass.getSimpleName())) {
            return;
        }

        beanResolver.resolveBean(aClass);
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
