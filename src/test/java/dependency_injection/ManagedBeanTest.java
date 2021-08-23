package dependency_injection;


import com.thoughtworks.fusheng.integration.junit5.FuShengTest;

@FuShengTest
public class ManagedBeanTest {
    public void initApplication() {
        DojoContextUtils.initApplication();
    }

    public String printMyGreeterWelcomeMessage() {
        DojoContextUtils.initApplication();
        DojoContainer dojoContainer = DojoContextUtils.retrieveDojoContainer();
        return dojoContainer.initMessage();
    }
}
