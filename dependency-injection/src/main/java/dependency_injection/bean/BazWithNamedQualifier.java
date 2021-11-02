package dependency_injection.bean;


import dependency_injection.annotation.DojoComponent;
import dependency_injection.bean.payment.PaymentProcessor;

import javax.inject.Inject;
import javax.inject.Named;

@DojoComponent
public class BazWithNamedQualifier {

    final PaymentProcessor paymentProcessor;

    @Inject
    public BazWithNamedQualifier(@Named(value = "AsynchronousPaymentProcessor") PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    public String retrieveProcessorName() {
        return this.paymentProcessor
                .getClass()
                .getSimpleName();
    }
}
