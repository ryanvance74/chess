package dataaccess.Exceptions;

public class UnauthorizedRequestException extends DataAccessException {
    public UnauthorizedRequestException(String message) {
        super(message);
    }
}
