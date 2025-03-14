package dataaccess.Exceptions;

public class DuplicateUserException extends DataAccessException {
    public DuplicateUserException(String message) {
        super(message);
    }
}
