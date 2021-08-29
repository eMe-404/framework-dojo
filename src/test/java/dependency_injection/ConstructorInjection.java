package dependency_injection;

import static org.assertj.core.api.Assertions.assertThat;

import com.thoughtworks.fusheng.integration.junit5.FuShengTest;
import dependency_injection.bean.Baz;

@FuShengTest
public class ConstructorInjection {
    public String retrieveGreeterBless(String clientBeanName) {
        DojoContextUtils.initApplication();
        DojoContainer dojoContainer = DojoContextUtils.retrieveDojoContainer();
        if (clientBeanName.equals("Baz")) {
            final Baz baz = (Baz) dojoContainer.retrieveBean(clientBeanName);
            return baz.retrieveGreeterBless();
        }
        return "Not recognized bean name";
    }
}