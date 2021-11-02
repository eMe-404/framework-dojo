package dependency_injection.bean.payment;

import dependency_injection.qualifier.Asynchronous;

@Asynchronous
public class AsynchronousPaymentProcessor implements PaymentProcessor {
}
