package dependency_injection.bean_resolver_chain;

import dependency_injection.DojoContainer;
import java.util.Map;

public abstract class BeanResolver {
    protected BeanResolver nextResolver;
    protected DojoContainer currentContainer;
    protected Map<String, Object> containerBeanFactory;

    public static BeanResolver configDefaultBeanResolverChain(DojoContainer container) {
        final BeanResolver constructorResolver = new ConstructorInjectionResolver(container);
        final BeanResolver filedResolver = new FieldInjectionResolver(container);
        final BeanResolver defaultFallBackResolver = new DefaultFallBackResolver(container);
        constructorResolver.setNextResolver(filedResolver);
        filedResolver.setNextResolver(defaultFallBackResolver);
        return constructorResolver;
    }

    public abstract void resolveBean(Class<?> beanClass);

    public BeanResolver getNextResolver() {
        return nextResolver;
    }

    public void setNextResolver(BeanResolver nextBeanResolver) {
        this.nextResolver = nextBeanResolver;
    }
}
