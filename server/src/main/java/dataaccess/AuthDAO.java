package dataaccess;
import dataaccess.exceptions.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(AuthData authData) throws DataAccessException;
    void clearData() throws DataAccessException;
    boolean empty() throws DataAccessException;
}
