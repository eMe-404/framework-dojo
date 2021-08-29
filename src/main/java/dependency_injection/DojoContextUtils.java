package dependency_injection;

import java.util.Objects;

public class DojoContextUtils {
    private static DojoContainer dojoContainer;

    private DojoContextUtils() {
    }

    public static void initApplication() {
        if (Objects.isNull(retrieveDojoContainer())) {
            dojoContainer = new DojoContainer();
        }
    }

    public static DojoContainer retrieveDojoContainer() {
        return dojoContainer;
    }
}
