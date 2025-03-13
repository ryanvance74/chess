package dataaccess;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.Collection;
import java.util.HashSet;

public interface UserDAO {
    UserData createUser(String username, String password, String email) throws DataAccessException, DuplicateUserException;
    UserData getUser(String username) throws DataAccessException;
    void clearData() throws DataAccessException;
}
