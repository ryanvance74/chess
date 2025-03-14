package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.*;
import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import passoff.model.*;
import server.Server;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Collection;




public class DatabaseInternalUnitTests {
    private static SQLUserDAO userDao;
    private static SQLGameDAO gameDao;
    private static SQLAuthDAO authDao;
    private static UserService userService;
    private static GameService gameService;

    @BeforeEach
    public void setup() {
        try {

            userDao = new SQLUserDAO();
            gameDao = new SQLGameDAO();
            authDao = new SQLAuthDAO();
            userService = new UserService(userDao, authDao);
            gameService = new GameService(gameDao, authDao);
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
        Assertions.assertDoesNotThrow(() -> ClearService.clearDatabase(authDao, userDao, gameDao));
        Assertions.assertDoesNotThrow(() -> {
            Assertions.assertTrue(authDao.empty() && gameDao.empty() && userDao.empty());
        });

    }

    @Test
    public void goodRegister() {
        Assertions.assertDoesNotThrow(() -> userService.register(new RegisterRequest("testUser_jdk", "epic_password_529", "vim@nano.edu")));
        Assertions.assertDoesNotThrow(() -> userService.register(new RegisterRequest("myUsername1234", "myPassword5678", "test@email.byu.edu")));

    }

    @Test
    public void badRegister() {
        Assertions.assertThrows(BadRequestException.class, () -> {
            userService.register(new RegisterRequest("myUsername1234", null, "test@email.byu.edu"));
        });
        Assertions.assertThrows(DuplicateUserException.class, () -> {
            userService.register(new RegisterRequest("myUsername1234", "epic_password_529", "test@email.byu.edu"));
            userService.register(new RegisterRequest("myUsername1234", "epic_password_529", "new@email.byu.edu"));
        });

    }

    @Test
    public void goodLogin() {
        Assertions.assertDoesNotThrow(() -> userService.register(new RegisterRequest("testUser_jdk", "epic_password_529", "vim@nano.edu")));
        Assertions.assertDoesNotThrow(() -> userService.register(new RegisterRequest("myUsername1234", "myPassword5678", "test@email.byu.edu")));
        Assertions.assertDoesNotThrow(() -> userService.login(new LoginRequest("myUsername1234", "myPassword5678")));
        Assertions.assertDoesNotThrow(() -> userService.login(new LoginRequest("testUser_jdk", "epic_password_529")));

    }

    @Test
    public void badLogin() {
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> {
            userService.login(new LoginRequest("myUsername1234", "epic_password_529"));
            userService.login(new LoginRequest("myUsername1234", "epic_password_529"));
        });

    }

    @Test
    public void goodLogout() {
        Assertions.assertDoesNotThrow(() -> userService.register(new RegisterRequest("testUser_jdk", "epic_password_529", "vim@nano.edu")));
        Assertions.assertDoesNotThrow(() -> userService.register(new RegisterRequest("myUsername1234", "myPassword5678", "test@email.byu.edu")));
        Assertions.assertDoesNotThrow(() -> userService.login(new LoginRequest("myUsername1234", "myPassword5678")));

        Assertions.assertDoesNotThrow(() -> {
                    LoginResult j = userService.login(new LoginRequest("testUser_jdk", "epic_password_529"));
                    userService.logout(new LogoutRequest(j.authToken()));
                }

        );

    }

    @Test
    public void badLogout() {
        Assertions.assertDoesNotThrow(() -> userService.register(new RegisterRequest("testUser_jdk", "epic_password_529", "vim@nano.edu")));
        Assertions.assertDoesNotThrow(() -> userService.register(new RegisterRequest("myUsername1234", "myPassword5678", "test@email.byu.edu")));
        Assertions.assertDoesNotThrow(() -> userService.login(new LoginRequest("myUsername1234", "myPassword5678")));
        Assertions.assertDoesNotThrow(() -> {}
        );
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> {
                    LoginResult j = userService.login(new LoginRequest("testUser_jdk1", "epic_password_529"));
                    userService.logout(new LogoutRequest(j.authToken()+"1"));
                }

        );
    }

    @Test
    public void goodListGames() {
        Assertions.assertDoesNotThrow(() -> {

        gameDao.createGame("game2435");
        gameDao.createGame("spiel21");
        AuthData auth = authDao.createAuth("testuser74");
        Collection<GameData> array = new ArrayList<>();

        Assertions.assertTrue(() -> {
            try {
                ListGamesResult result = gameService.listGames(auth.authToken());
                boolean found1 = false;
                boolean found2 = false;
                for (ListGameResultSingle game : result.games()) {
                    if (game.gameName().equals("game2435")) {
                        found1 = true;
                    }
                    if (game.gameName().equals("spiel21")) {
                        found2 = true;
                    }
                }
                return found1 && found2;
            } catch (Exception e) {
                return false;
            }
        });

        });
    }

    @Test
    public void badListGames() {
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> gameService.listGames("234"));

    }

    @Test
    public void goodCreateGame() {
        Assertions.assertDoesNotThrow(() -> {
            AuthData authData = authDao.createAuth("user234");
            Assertions.assertTrue(() -> {
                try {
                    CreateGameResult result = gameService.createGame(new CreateGameRequest(authData.authToken(), "game234"));
                    return true;
                } catch (Exception e) {
                    return false;
                }

            });
        });

    }

    @Test
    public void badCreateGame() {
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> gameService.createGame(new CreateGameRequest("234", "test_game")));
    }

    @Test
    public void goodUpdateGame() {
        Assertions.assertDoesNotThrow(() -> {
            AuthData authData = authDao.createAuth("user234");
            Assertions.assertTrue(() -> {
                try {
                    CreateGameResult result = gameService.createGame(new CreateGameRequest(authData.authToken(), "game234"));
                    gameService.updateGame(new UpdateGameRequest(authData.authToken(), ChessGame.TeamColor.WHITE, result.gameID()));
                    return true;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return false;
                }
        });



        });
    }

    @Test
    public void badUpdateGame() {
        Assertions.assertThrows(UnauthorizedRequestException.class, () -> {
                    gameService.updateGame(new UpdateGameRequest("234", ChessGame.TeamColor.WHITE,234));
                }
        );
    }
}
