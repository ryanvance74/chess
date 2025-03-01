package service;
import dataaccess.*;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import passoff.model.*;
import server.Server;
import org.junit.jupiter.api.Assertions;

public class InternalUnitTests {

    @DisplayName("Clear Database")
    public void goodClear() {
        ClearService clearService = new ClearService();
        MemoryUserDAO userDao = new MemoryUserDAO();
        MemoryGameDAO gameDao = new MemoryGameDAO();
        MemoryAuthDAO authDao = new MemoryAuthDAO();

        userDao.createUser("myUsername1234", "myPassword5678", "test@email.byu.edu");
        userDao.createUser("testUser_jdk", "epic_password_529", "vim@nano.edu");

        gameDao.createGame("Deutsches Spiel");
        gameDao.createGame("Schweizer Spiel ");
        gameDao.createGame("Österreichisches Spiel");

        authDao.createAuth("small_user");
        authDao.createAuth("FC_Bayern279");

        Assertions.assertDoesNotThrow(() -> clearService.clearDatabase(authDao, userDao, gameDao));
        Assertions.assertTrue(authDao.);
    }
}
