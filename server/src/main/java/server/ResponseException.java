package server;

public class ResponseException extends RuntimeException {
    public ResponseException(int code, String message) {
        super(message);
    }
}
