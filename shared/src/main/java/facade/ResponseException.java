package facade;

public class ResponseException extends RuntimeException {
    public ResponseException(int code, String message) {
        super(message);
    }
}
