package dependency_injection.bean.payment;

import dependency_injection.qualifier.Synchronous;

@Synchronous
public class SynchronousPaymentProcessor implements PaymentProcessor {
}
