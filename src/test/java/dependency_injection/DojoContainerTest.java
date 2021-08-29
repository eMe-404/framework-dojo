package dependency_injection;


import com.thoughtworks.fusheng.integration.junit5.FuShengTest;

@FuShengTest
public class DojoContainerTest {

    public String retrieveActiveContainer() {
        DojoContextUtils.initApplication();
        DojoContainer dojoContainer = DojoContextUtils.retrieveDojoContainer();
        return dojoContainer.initMessage();
    }

    public String retrieveBean(String beanName) {
        DojoContextUtils.initApplication();
        DojoContainer dojoContainer = DojoContextUtils.retrieveDojoContainer();
        final Object obj = dojoContainer.retrieveBean(beanName);
        return obj.getClass().getSimpleName();
    }


}
