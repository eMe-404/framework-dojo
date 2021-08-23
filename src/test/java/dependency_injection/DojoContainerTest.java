package dependency_injection;


import com.thoughtworks.fusheng.integration.junit5.FuShengTest;

@FuShengTest
public class DojoContainerTest {
    public void initApplication() {
        DojoContextUtils.initApplication();
    }

    public String retrieveActiveContainer() {
        DojoContextUtils.initApplication();
        DojoContainer dojoContainer = DojoContextUtils.retrieveDojoContainer();
        return dojoContainer.initMessage();
    }
}
