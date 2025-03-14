package dataaccess;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DuplicateUserException;
import model.UserData;

public interface UserDAO {
    UserData createUser(String username, String password, String email) throws DataAccessException, DuplicateUserException;
    UserData getUser(String username) throws DataAccessException;
    void clearData() throws DataAccessException;
    boolean verifyUser(String inputPassword, String hashedPassword);
}
