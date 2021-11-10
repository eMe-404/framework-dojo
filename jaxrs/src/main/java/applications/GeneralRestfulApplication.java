package applications;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import org.reflections.Reflections;

public class GeneralRestfulApplication extends Application {
    private final Set<Class<?>> autoRegisteredClasses;

    public GeneralRestfulApplication() {
        this.autoRegisteredClasses = new HashSet<>();
    }

    public void register(Class<?> clazz) {
        autoRegisteredClasses.add(clazz);
    }

    public void register(Class<?>... classes) {
        autoRegisteredClasses.addAll(List.of(classes));
    }

    @Override
    public Set<Class<?>> getClasses() {
        return autoRegisteredClasses;
    }

    public void scanPackage() {
        //TODO: don't use reflection, try to use native class loader
        final Reflections reflections = new Reflections();
        final Set<Class<?>> scannedClasses = reflections.getTypesAnnotatedWith(Path.class);
        this.register(scannedClasses.toArray(Class[]::new));
    }
}
