package client;

import chess.ChessGame;
import dataaccess.exceptions.BadRequestException;
import dataaccess.exceptions.DuplicateUserException;
import dataaccess.exceptions.UnauthorizedRequestException;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;
import service.*;

import java.util.ArrayList;
import java.util.Collection;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }
//    public static void init() {
//        server = new Server();
//        var port = server.run(0);
//        System.out.println("Started test HTTP server on " + port);
//    }
    @AfterEach
    void clearDb() {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    void clearGood() {
        Assertions.assertDoesNotThrow(() -> facade.clear());
    }

    @Test
    void registerGood() {
        var authData = facade.register(new RegisterRequest("username1234", "password", "p1@email.com"));
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerBad() {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register(new RegisterRequest("myUsername1234", null, "test@email.byu.edu"));
        });
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register(new RegisterRequest("myUsername1234", "epic_password_529", "test@email.byu.edu"));
            facade.register(new RegisterRequest("myUsername1234", "epic_password_529", "new@email.byu.edu"));
        });
    }

    @Test
    public void goodLogin() {
        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("testUser_jdk", "epic_password_529", "vim@nano.edu")));
        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("myUsername1234", "myPassword5678", "test@email.byu.edu")));
        Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("myUsername1234", "myPassword5678")));
        Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("testUser_jdk", "epic_password_529")));

    }

    @Test
    public void badLogin() {
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.login(new LoginRequest("myUsername1234", "epic_password_529"));
            facade.login(new LoginRequest("myUsername1234", "epic_password_529"));
        });

    }

    @Test
    public void goodLogout() {
        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("testUser_jdk", "epic_password_529", "vim@nano.edu")));
        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("myUsername12345", "myPassword5678", "test@email.byu.edu")));
        Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("myUsername12345", "myPassword5678")));

        Assertions.assertDoesNotThrow(() -> {
                    LoginResult j = facade.login(new LoginRequest("testUser_jdk", "epic_password_529"));
            facade.logout(new LogoutRequest(j.authToken()));
                }

        );

    }

    @Test
    public void badLogout() {
        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("testUser_jdk", "epic_password_529", "vim@nano.edu")));
        Assertions.assertDoesNotThrow(() -> facade.register(new RegisterRequest("myUsername1234", "myPassword5678", "test@email.byu.edu")));
        Assertions.assertDoesNotThrow(() -> facade.login(new LoginRequest("myUsername1234", "myPassword5678")));
        Assertions.assertDoesNotThrow(() -> {}
        );
        Assertions.assertThrows(ResponseException.class, () -> {
                    LoginResult j = facade.login(new LoginRequest("testUser_jdk1", "epic_password_529"));
            facade.logout(new LogoutRequest(j.authToken()+"1"));
                }

        );
    }

//    @Test
//    public void goodListGames() {
//        facade.createGame("game2435");
//        facade.createGame("spiel21");
//        AuthData auth = facade.createAuth("testuser74");
//        Collection<GameData> array = new ArrayList<>();
//
//        Assertions.assertTrue(() -> {
//            try {
//                ListGamesResult result = gameService.listGames(auth.authToken());
//                boolean found1 = false;
//                boolean found2 = false;
//                for (ListGameResultSingle game : result.games()) {
//                    if (game.gameName().equals("game2435")) {
//                        found1 = true;
//                    }
//                    if (game.gameName().equals("spiel21")) {
//                        found2 = true;
//                    }
//                }
//                return found1 && found2;
//            } catch (Exception e) {
//                return false;
//            }
//
//
//        });
//
//    }
//
//    @Test
//    public void badListGames() {
//        Assertions.assertThrows(UnauthorizedRequestException.class, () -> gameService.listGames("234"));
//
//    }
//
//    @Test
//    public void goodCreateGame() {
//        AuthData authData = authDao.createAuth("user234");
//        Assertions.assertTrue(() -> {
//            try {
//                CreateGameResult result = gameService.createGame(new CreateGameRequest(authData.authToken(), "game234"));
//                return true;
//            } catch (Exception e) {
//                return false;
//            }
//
//
//        });
//    }
//
//    @Test
//    public void badCreateGame() {
//        Assertions.assertThrows(UnauthorizedRequestException.class, () -> gameService.createGame(new CreateGameRequest("234", "test_game")));
//    }
//
//    @Test
//    public void goodUpdateGame() {
//        AuthData authData = authDao.createAuth("user234");
//        Assertions.assertTrue(() -> {
//            try {
//                CreateGameResult result = gameService.createGame(new CreateGameRequest(authData.authToken(), "game234"));
//                gameService.updateGame(new UpdateGameRequest(authData.authToken(), ChessGame.TeamColor.WHITE, result.gameID()));
//                return true;
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//                return false;
//            }
//
//
//        });
//    }
//
//    @Test
//    public void badUpdateGame() {
//        Assertions.assertThrows(UnauthorizedRequestException.class, () -> {
//                    gameService.updateGame(new UpdateGameRequest("234", ChessGame.TeamColor.WHITE,234));
//                }
//        );
//    }
}
