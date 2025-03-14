package dataaccess;
import dataaccess.Exceptions.DataAccessException;
import dataaccess.Exceptions.DuplicateUserException;
import model.UserData;

public interface UserDAO {
    UserData createUser(String username, String password, String email) throws DataAccessException, DuplicateUserException;
    UserData getUser(String username) throws DataAccessException;
    void clearData() throws DataAccessException;
    boolean verifyUser(String inputPassword, String hashedPassword);
}
