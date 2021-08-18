package dependency_injection;


import com.thoughtworks.fusheng.integration.junit5.FuShengTest;

@FuShengTest
public class DojoContainerTest {
    public void initApplication() {
        DojoContextUtils.initApplication();
    }

    public DojoContainer retrieveActiveContainer() {
        DojoContextUtils.initApplication();
        return DojoContextUtils.retrieveDojoContainer();
    }
}
