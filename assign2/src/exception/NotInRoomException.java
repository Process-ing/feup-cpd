package exception;

public class NotInRoomException extends RuntimeException {
    public NotInRoomException() {}

    public NotInRoomException(String message) {
        super(message);
    }
}
