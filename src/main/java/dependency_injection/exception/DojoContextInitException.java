package dependency_injection.exception;

public class DojoContextInitException extends RuntimeException {
    public DojoContextInitException(final String message) {
        super(message);
    }

    public DojoContextInitException() {
        super();
    }
}
