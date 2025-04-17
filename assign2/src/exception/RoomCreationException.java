package exception;

public class RoomCreationException extends RuntimeException {
    public RoomCreationException() {};

    public RoomCreationException(String message) {
        super(message);
    }
}
