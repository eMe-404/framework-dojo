package dependency_injection.bean;


import dependency_injection.annotation.DojoComponent;
import dependency_injection.bean.payment.PaymentProcessor;
import dependency_injection.qualifier.Synchronous;
import javax.inject.Inject;

@DojoComponent
public class Foo {
    public static final String WELCOME_MESSAGE = "hello from Foo";
    private final PaymentProcessor paymentProcessor;

    @Inject
    public Foo(@Synchronous final PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    public String sayHi() {
        return WELCOME_MESSAGE;
    }

    public String retrieveProcessorName() {
        return paymentProcessor.getClass().getSimpleName();
    }
}
