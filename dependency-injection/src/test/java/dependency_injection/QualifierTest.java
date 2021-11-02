package dependency_injection;

import com.thoughtworks.fusheng.integration.junit5.FuShengTest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@FuShengTest
public class QualifierTest {
    public Object retrievePaymentProcessor(String injectionBean) {
        DojoContextHelper.initApplication();
        DojoContainer dojoContainer = DojoContextHelper.retrieveDojoContainer();
        final Object retrievedBean = dojoContainer.retrieveBean(injectionBean);
        try {
            final Method processorMethod = retrievedBean.getClass().getMethod("retrieveProcessorName");
            return processorMethod.invoke(retrievedBean);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return "Not recognized bean name";
    }
}