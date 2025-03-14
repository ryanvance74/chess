package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DuplicateUserException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import service.*;
import org.junit.jupiter.api.*;




public class DatabaseInternalUnitTests {
    private static SQLUserDAO userDao;
    private static SQLGameDAO gameDao;
    private static SQLAuthDAO authDao;
    private static UserService userService;

    @BeforeEach
    public void setup() {
        try {

            userDao = new SQLUserDAO();
            gameDao = new SQLGameDAO();
            authDao = new SQLAuthDAO();
        } catch (Exception e) {
            System.out.println("FAILED ON SETUP");
            System.exit(1);
        }


    }
    @AfterEach
    public void takeDown() {
        try {
            authDao.clearData();
            gameDao.clearData();
            userDao.clearData();
        } catch (Exception e) {
            System.out.println("FAILED ON TAKEDOWN");
            System.exit(1);
        }
    }




    @DisplayName("Clear Database")
    @Test
    public void goodClear() {


        Assertions.assertDoesNotThrow(() -> userDao.createUser("myUsername1234", "myPassword5678", "test@email.byu.edu"));
        Assertions.assertDoesNotThrow(() -> userDao.createUser("testUser_jdk", "epic_password_529", "vim@nano.edu"));

        Assertions.assertDoesNotThrow(() -> {
            gameDao.createGame("Deutsches Spiel");
            gameDao.createGame("Schweizer Spiel ");
            gameDao.createGame("Österreichisches Spiel");

            authDao.createAuth("small_user");
            authDao.createAuth("FC_Bayern279");}
        );
        Assertions.assertDoesNotThrow(() -> {
            userDao.clearData();
            authDao.clearData();
            gameDao.clearData();
        });
        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertTrue(authDao.empty() && gameDao.empty() && userDao.empty());
        });

    }

    @Test
    public void goodCreateUser() {
        Assertions.assertDoesNotThrow(() -> userDao.createUser("myUsername1234", "myPassword5678", "test@email.byu.edu"));
        Assertions.assertDoesNotThrow(() -> userDao.createUser("testUser_jdk", "epic_password_529", "vim@nano.edu"));

    }

    @Test
    public void badCreateUser() {
        Assertions.assertThrows(DuplicateUserException.class, () -> {
            userDao.createUser("myUsername1234", "epic_password_529", "test@email.byu.edu");
            userDao.createUser("myUsername1234", "epic_password_529", "new@email.byu.edu");
        });

    }

    @Test
    public void goodGetUser() {
        Assertions.assertDoesNotThrow(() -> userDao.createUser("myUsername1234", "myPassword5678", "test@email.byu.edu"));
        Assertions.assertDoesNotThrow(() -> {
            userDao.getUser("myUsername1234");
        });
    }

    @Test
    public void badGetUser() {
        Assertions.assertDoesNotThrow(() -> userDao.createUser("myUsername1234", "myPassword5678", "test@email.byu.edu"));
        Assertions.assertDoesNotThrow(() -> {
            userDao.getUser("invalidUsername1234");
        });
    }

    @Test
    public void goodHashPassword() {
        String hashedPassword = BCrypt.hashpw("password123456", BCrypt.gensalt());
        Assertions.assertTrue(userDao.verifyUser("password123456", hashedPassword));
    }

    @Test
    public void badHashPassword() {
        String hashedPassword = BCrypt.hashpw("password123456", BCrypt.gensalt());
        Assertions.assertFalse(userDao.verifyUser("differentPassword", hashedPassword));
    }

    @Test
    public void goodEmpty() {
        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertTrue(userDao.empty());
        });
    }

    @Test
    public void badEmpty() {
        Assertions.assertDoesNotThrow(() -> {
            userDao.createUser("testUser_jdk", "epic_password_529", "vim@nano.edu");
        });

        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertFalse(userDao.empty());
        });
    }

    @Test
    public void goodCreateAuth() {
        Assertions.assertDoesNotThrow(() -> {
            AuthData auth = authDao.createAuth("testuser74");
        });
    }

    @Test
    public void badCreateAuth() {
        Assertions.assertThrows(Exception.class, () -> authDao.createAuth(null));

    }

    @Test
    public void goodGetAuth() {
        Assertions.assertDoesNotThrow(() -> {
            AuthData authData = authDao.createAuth("user234");
            Assertions.assertEquals(authData, authDao.getAuth(authData.authToken()));
        });

    }

    @Test
    public void goodDeleteAuth() {
        Assertions.assertDoesNotThrow(() -> {
            AuthData authData = authDao.createAuth("user234");
            authDao.deleteAuth(authData);
            Assertions.assertTrue(authDao.empty());
        });
    }

    @Test
    public void anotherGoodDeleteAuth() {

        Assertions.assertDoesNotThrow(() ->
            {authDao.deleteAuth(new AuthData("auth_token1234567", "user1234"));
        });
    }

    @Test
    public void goodAuthEmpty() {
        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertTrue(authDao.empty());
        });
    }

    @Test
    public void badAuthEmpty() {
        Assertions.assertDoesNotThrow(() -> {
            authDao.createAuth("user234");
            Assertions.assertFalse(authDao.empty());
        });
    }

    @Test
    public void goodCreateGame() {
        Assertions.assertDoesNotThrow(() -> {
            gameDao.createGame("game1234");
        });
    }

    @Test
    public void badCreateGame() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDao.createGame("""
            game1234555;laksjdfhgjfkdls;ahgjfdkla;hgjfjdks
            la;ghfjdksla;ghfjdksla;ghfjdksla;ghfjdksla;ghfj
            ksal;ghfjdksla;ghfjdksla;ghfjdksla;ghfjdksla;gh
            as;ldkfjghfjdksla;ghfjdksla;ghfjdksla;hgjfdksl;a
            ghfjdksal;ghfjdksla;ghfjdksla;ghfjdksal;ghfjdksal;
            ghfjdksal;ghfjdksal;ghfjdksla;jfhgjdfksl;aghfjdksl;a
            ghfjdksal;ghfjdksal;ghfjdksal;ghfjdksla;ghfjdksal;
            ghfjdksal;ghfjdksl;aghfjdks;alghjfdksal;ghfjdksla;
            ghfjdksal;ghfjdksla;ghfjkdsa;lghfjdksal;ghfjdksal;
            ghfjdksal;ghfjdksal;ghfjdksal;ghfjdksal;ghfjdksl;
            hgfjdksa;lghfjdksal;ghfjdksal;ghfjdksal;ghfjdksla;
            ghfjdksal;ghfjdksal;ghjfdksl;aghfjdksal;ghfjdksal;
            ghfjdksal;ghfjdksla;ghfjdksla;ghfjdksla;ghfjdksa;
            """);
        });
    }

    @Test
    public void goodListGames() {
        Assertions.assertDoesNotThrow(() -> {
            gameDao.createGame("game1234");
            gameDao.createGame("game1235");
            Assertions.assertEquals(2, gameDao.listGames().size());
        });
    }

    @Test
    public void anotherGoodListGames() {

        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertEquals(0, gameDao.listGames().size());
        });
    }

    @Test
    public void goodUpdateGame() {
        Assertions.assertDoesNotThrow(() -> {
            GameData gameData = gameDao.createGame("game1234");
            gameDao.updateGame(gameData.gameID(), "testuser74", ChessGame.TeamColor.WHITE.name());
        });
    }

    @Test
    public void badUpdateGame() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            GameData gameData = gameDao.createGame("game1234");
            gameDao.updateGame(gameData.gameID()+1, "testuser74", ChessGame.TeamColor.WHITE.name());
        });
    }

    @Test
    public void goodGameEmpty() {
        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertTrue(gameDao.empty());
        });
    }

    @Test
    public void badGameEmpty() {

        Assertions.assertDoesNotThrow(() -> {
            gameDao.createGame("game1234");
            Assertions.assertFalse(gameDao.empty());
        });
    }
}
