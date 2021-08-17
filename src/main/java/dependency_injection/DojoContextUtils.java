package dependency_injection;

public class DojoContextUtils {
    private static DojoContainer dojoContainer;

    private DojoContextUtils() {
    }

    public static void initApplication() {
        dojoContainer = new DojoContainer();
    }

    public static DojoContainer retrieveDojoContainer() {
        return dojoContainer;
    }
}
