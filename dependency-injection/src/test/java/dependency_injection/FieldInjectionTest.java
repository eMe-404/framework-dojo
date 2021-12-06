package dependency_injection;

import com.thoughtworks.fusheng.integration.junit5.FuShengTest;
import dependency_injection.examples.Bar;

@FuShengTest
public class FieldInjectionTest {
    public String retrieveGreeterBless(String clientBeanName) {
        DojoContextHelper.initApplication();

        DojoContainer dojoContainer = DojoContextHelper.retrieveDojoContainer();
        if (clientBeanName.equals("Bar")) {
            final Bar bar = (Bar) dojoContainer.retrieveBean(clientBeanName);
            return bar.retrieveGreeterBless();
        }
        return "Not recognized bean name";
    }

    public String retrieveCombinedMessage(String clientBeanName) {
        DojoContextHelper.initApplication();

        DojoContainer dojoContainer = DojoContextHelper.retrieveDojoContainer();
        if (clientBeanName.equals("Bar")) {
            final Bar bar = (Bar) dojoContainer.retrieveBean(clientBeanName);
            return bar.retrieveCombinedMessage();
        }
        return "Not recognized bean name";
    }


}