package dataaccess;

import model.AuthData;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    Collection<AuthData> authDataHashSet = HashSet.newHashSet(1000);

    public AuthData createAuth(String username) {
        String newAuthToken = UUID.randomUUID().toString();
        AuthData newAuthData = new AuthData(newAuthToken,username);
        authDataHashSet.add(newAuthData);
        return newAuthData;
    }
    public AuthData getAuth(String authToken) {
        // TODO
        return null;
    }

    public void deleteAuth(AuthData authData) {
        authDataHashSet.remove(authData);
    }

    public void clearData() {
        authDataHashSet.clear();
    }

    public boolean empty() {
        return authDataHashSet.isEmpty();
    }
}
