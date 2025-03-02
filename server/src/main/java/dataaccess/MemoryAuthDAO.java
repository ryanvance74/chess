package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    HashMap<String, AuthData> authDataHashMap = new HashMap<>();

    public AuthData createAuth(String username) {
        String newAuthToken = UUID.randomUUID().toString();
        AuthData newAuthData = new AuthData(newAuthToken,username);
        authDataHashMap.put(newAuthToken, newAuthData);
        return newAuthData;
    }
    public AuthData getAuth(String authToken) {
        return authDataHashMap.get(authToken);
    }

    public void deleteAuth(AuthData authData) {
        authDataHashMap.remove(authData.authToken());
    }

    public void clearData() {
        authDataHashMap.clear();
    }

    public boolean empty() {
        return authDataHashMap.isEmpty();
    }
}
