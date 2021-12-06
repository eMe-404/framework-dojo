package dependency_injection.examples;

import dependency_injection.annotation.DojoComponent;
import dependency_injection.examples.payment.PaymentProcessor;
import dependency_injection.qualifier.Asynchronous;
import javax.inject.Inject;

@DojoComponent
public class Baz {
    private final MyGreeter myGreeter;

    @Inject
    @Asynchronous
    private PaymentProcessor paymentProcessor;

    @Inject
    public Baz(final MyGreeter myGreeter) {
        this.myGreeter = myGreeter;
    }

    public String retrieveGreeterBless() {
        return myGreeter.sayHi();
    }

    public String retrieveProcessorName() {
        return this.paymentProcessor.getClass().getSimpleName();
    }
}
