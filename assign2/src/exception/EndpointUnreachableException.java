package exception;

public class EndpointUnreachableException extends RuntimeException {
    public EndpointUnreachableException() {
        super();
    }

    public EndpointUnreachableException(String message) {
        super(message);
    }
}
