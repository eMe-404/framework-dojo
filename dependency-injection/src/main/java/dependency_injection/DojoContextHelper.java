package dependency_injection;

import java.util.Objects;

public class DojoContextHelper {
    private static DojoContainer dojoContainer;

    private DojoContextHelper() {
    }

    public static void initApplication() {
        if (Objects.isNull(retrieveDojoContainer())) {
            dojoContainer = DojoContainer.initWithManagedBeans();
        }
    }

    public static DojoContainer retrieveDojoContainer() {
        return dojoContainer;
    }

    public static DojoContainer newContainer() {
        return DojoContainer.initWithDefaultSetup();
    }
}
