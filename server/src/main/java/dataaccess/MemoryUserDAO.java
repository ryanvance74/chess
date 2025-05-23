package dataaccess;

import dataaccess.exceptions.DuplicateUserException;
import model.UserData;

import java.util.HashMap;

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

    public boolean verifyUser(String inputPassword, String hashedPassword) {
        return inputPassword.equals(hashedPassword);
    }
}
