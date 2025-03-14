package dataaccess.exceptions;

public class UnauthorizedRequestException extends DataAccessException {
    public UnauthorizedRequestException(String message) {
        super(message);
    }
}
