package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {
    Collection<UserData> userDataHashSet = HashSet.newHashSet(1000);

    public void clearData() {
        userDataHashSet.clear();
    }

    public UserData createUser(String username, String password, String email) {
        UserData newUserData = new UserData(username, password, email);
        userDataHashSet.add(newUserData);
        return newUserData;
    }

    public UserData getUser(String username) {
        // TODO
        return null;
    }
}
