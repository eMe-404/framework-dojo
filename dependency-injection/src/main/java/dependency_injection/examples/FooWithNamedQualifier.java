package dependency_injection.examples;


import dependency_injection.annotation.DojoComponent;
import dependency_injection.examples.payment.PaymentProcessor;

import javax.inject.Inject;
import javax.inject.Named;

@DojoComponent
public class FooWithNamedQualifier {

    @Inject
    @Named(value = "SynchronousPaymentProcessor")
    PaymentProcessor paymentProcessor;

    public String retrieveProcessorName() {
        return this.paymentProcessor
                .getClass()
                .getSimpleName();
    }
}
