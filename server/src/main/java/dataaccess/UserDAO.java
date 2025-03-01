package dataaccess;
import model.AuthData;
import model.UserData;

import java.util.Collection;
import java.util.HashSet;

public interface UserDAO {
    UserData createUser(String username, String password, String email);
    UserData getUser(String username);
    void clearData();
}
