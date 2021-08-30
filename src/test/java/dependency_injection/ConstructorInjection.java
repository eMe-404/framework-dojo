package dependency_injection;

import static org.assertj.core.api.Assertions.assertThat;

import com.thoughtworks.fusheng.integration.junit5.FuShengTest;
import dependency_injection.bean.Baz;
import dependency_injection.bean.MoreInjectionPointBean;
import dependency_injection.exception.DojoContextInitException;

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

    public String moreInjectAnnotationCase() {
        final DojoContainer plainContainer = DojoContextUtils.newContainer();
        final MoreInjectionPointBean moreInjectAnnotatedBean = new MoreInjectionPointBean();
        try {
            plainContainer.register(moreInjectAnnotatedBean);
        } catch (DojoContextInitException exp) {
            return exp.getClass().getSimpleName();
        }
        return "No Exception";
    }
}