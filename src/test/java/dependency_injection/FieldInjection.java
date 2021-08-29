package dependency_injection;

import com.thoughtworks.fusheng.integration.junit5.FuShengTest;
import dependency_injection.bean.Bar;
import dependency_injection.bean.Baz;
import dependency_injection.bean.MoreInjectionPointBean;
import dependency_injection.exception.DojoContextInitException;

@FuShengTest
public class FieldInjection {
    public String retrieveGreeterBless(String clientBeanName) {
        DojoContextUtils.initApplication();

        DojoContainer dojoContainer = DojoContextUtils.retrieveDojoContainer();
        if (clientBeanName.equals("Bar")) {
            final Bar bar = (Bar) dojoContainer.retrieveBean(clientBeanName);
            return bar.retrieveGreeterBless();
        }
        return "Not recognized bean name";
    }

    public String retrieveCombinedMessage(String clientBeanName) {
        DojoContextUtils.initApplication();

        DojoContainer dojoContainer = DojoContextUtils.retrieveDojoContainer();
        if (clientBeanName.equals("Bar")) {
            final Bar bar = (Bar) dojoContainer.retrieveBean(clientBeanName);
            return bar.retrieveCombinedMessage();
        }
        return "Not recognized bean name";
    }


}