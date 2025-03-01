package dataaccess;
import model.AuthData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public interface AuthDAO {
    AuthData createAuth(String username);
    AuthData getAuth(String authToken);
    void deleteAuth(AuthData authData);
    void clearData();
}
