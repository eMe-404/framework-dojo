package dependency_injection;

import com.thoughtworks.fusheng.integration.junit5.FuShengTest;

@FuShengTest
public class DojoContainer {
    void initApplication() {
        DojoContextUtils.initApplication();
    }

    DojoContainer retrieveActiveContainer() {
        return DojoContextUtils.retrieveDojoContainer();
    }
}
