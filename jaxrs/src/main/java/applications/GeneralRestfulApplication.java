package applications;

import java.util.*;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import dependency_injection.DojoContainer;
import dependency_injection.DojoContextHelper;
import org.reflections.Reflections;

public class GeneralRestfulApplication extends Application {
    private final Set<Class<?>> autoRegisteredResourceClasses;
    private final Map<String, Object> autoInjectedClasses;

    public GeneralRestfulApplication() {
        this.autoRegisteredResourceClasses = new HashSet<>();

        DojoContextHelper.initApplication();
        DojoContainer dojoContainer = DojoContextHelper.retrieveDojoContainer();
        this.autoInjectedClasses = dojoContainer.getResolvedBeansFactory();
    }

    public void register(Class<?> clazz) {
        autoRegisteredResourceClasses.add(clazz);
    }

    public void register(Class<?>... classes) {
        autoRegisteredResourceClasses.addAll(List.of(classes));
    }

    @Override
    public Set<Class<?>> getClasses() {
        return autoRegisteredResourceClasses;
    }

    public void scanPackage() {
        //TODO: don't use reflection, try to use native class loader
        final Reflections reflections = new Reflections();
        final Set<Class<?>> scannedClasses = reflections.getTypesAnnotatedWith(Path.class);
        this.register(scannedClasses.toArray(Class[]::new));
    }

    public Object retrieveInstanceByName(String classSimpleName) {
        return autoInjectedClasses.get(classSimpleName);
    }
}
