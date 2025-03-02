package dataaccess;

import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MemoryUserDAO implements UserDAO {

    HashMap<String, UserData> userDataHashMap = new HashMap<>();

    public void clearData() {
        userDataHashMap.clear();
    }

    public UserData createUser(String username, String password, String email) throws DuplicateUserException {
        UserData newUserData = new UserData(username, password, email);
        if (userDataHashMap.containsKey(username)) {
            throw new DuplicateUserException("Error: already taken");
        } else {
            userDataHashMap.put(username, newUserData);

        }

        return newUserData;
    }

    public UserData getUser(String username) {
        return userDataHashMap.get(username);
    }

    public boolean empty() {
        return userDataHashMap.isEmpty();
    }
}
