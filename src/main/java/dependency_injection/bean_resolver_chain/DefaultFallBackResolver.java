package dependency_injection.bean_resolver_chain;

import dependency_injection.DojoContainer;
import java.lang.reflect.InvocationTargetException;

public class DefaultFallBackResolver extends BeanResolver {
    public DefaultFallBackResolver(final DojoContainer container) {
        this.currentContainer = container;
        this.containerBeanFactory = container.getResolvedBeansFactory();
    }

    @Override
    public void resolveBean(final Class<?> beanClass) {
        try {
            if (!containerBeanFactory.containsKey(beanClass.getSimpleName())) {
                final Object newInstance = beanClass.getDeclaredConstructor().newInstance();
                containerBeanFactory.put(beanClass.getSimpleName(), newInstance);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
