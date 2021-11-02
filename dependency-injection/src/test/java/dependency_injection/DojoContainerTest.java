package dependency_injection;


import com.thoughtworks.fusheng.integration.junit5.FuShengTest;

@FuShengTest
public class DojoContainerTest {

    public String retrieveActiveContainer() {
        DojoContextHelper.initApplication();
        DojoContainer dojoContainer = DojoContextHelper.retrieveDojoContainer();
        return dojoContainer.initMessage();
    }

    public String retrieveBean(String beanName) {
        DojoContextHelper.initApplication();
        DojoContainer dojoContainer = DojoContextHelper.retrieveDojoContainer();
        final Object obj = dojoContainer.retrieveBean(beanName);
        return obj.getClass().getSimpleName();
    }


}
