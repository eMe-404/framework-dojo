package dependency_injection;

import dependency_injection.annotation.DojoComponent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import org.reflections.Reflections;

public class DojoContainer {
    public static final String DEFAULT_PREFIX = "";
    private final HashMap<String, Object> beanMap;

    public DojoContainer() {
        beanMap = new HashMap<>();
        registerBeans();
    }

    private void registerBeans() {
        final Reflections reflections = new Reflections(DEFAULT_PREFIX);
        final Set<Class<?>> managedBeans = reflections.getTypesAnnotatedWith(DojoComponent.class);
        managedBeans.forEach(aClass -> {
            final Constructor<?>[] beanConstructors = aClass.getConstructors();
            final Optional<Constructor<?>> defaultConstructor =
                    Arrays.stream(beanConstructors).filter(constructor -> constructor.getParameterCount() == 0).findFirst();
            defaultConstructor.ifPresent(constructor -> {
                try {
                    final Object newInstance = constructor.newInstance();
                    beanMap.put(aClass.getSimpleName(), newInstance);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public String initMessage() {
        return "container initialized successfully";
    }

    public Object retrieveBean(final String beanName) {
        return beanMap.get(beanName);
    }
}
