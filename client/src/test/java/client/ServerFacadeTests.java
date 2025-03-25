package client;

import chess.ChessGame;
import model.GameData;
import org.junit.jupiter.api.*;
import requests.*;
import server.Server;
import facade.*;

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

    @Test
    public void goodListGames() {
        var authData = facade.register(new RegisterRequest("username1234", "password", "p1@email.com"));
        Collection<GameData> array = new ArrayList<>();
        facade.createGame(new CreateGameRequest(authData.authToken(), "game2345"));
        facade.createGame(new CreateGameRequest(authData.authToken(), "spiel21"));

        Assertions.assertTrue(() -> {
            try {
                ListGamesResult result = facade.listGames(authData.authToken());
                boolean found1 = false;
                boolean found2 = false;
                for (ListGameResultSingle game : result.games()) {
                    if (game.gameName().equals("game2345")) {
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

    }

    @Test
    public void badListGames() {
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames("234"));

    }

    @Test
    public void goodCreateGame() {
        var authData = facade.register(new RegisterRequest("username1234", "password", "p1@email.com"));
        Assertions.assertTrue(() -> {
            try {
                CreateGameResult result = facade.createGame(new CreateGameRequest(authData.authToken(), "game234"));
                return true;
            } catch (Exception e) {
                return false;
            }
        });
    }

    @Test
    public void badCreateGame() {
        Assertions.assertThrows(ResponseException.class, () -> facade.createGame(new CreateGameRequest("234", "test_game")));
    }

    @Test
    public void goodJoinGame() {
        var authData = facade.register(new RegisterRequest("username1234", "password", "p1@email.com"));
        Assertions.assertTrue(() -> {
            try {
                CreateGameResult result = facade.createGame(new CreateGameRequest(authData.authToken(), "game234"));
                facade.joinGame(new UpdateGameRequest(authData.authToken(), ChessGame.TeamColor.WHITE, result.gameID()));
                return true;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }


        });
    }

    @Test
    public void badJoinGame() {
        Assertions.assertThrows(ResponseException.class, () -> {
                    facade.joinGame(new UpdateGameRequest("234", ChessGame.TeamColor.WHITE,234));
                }
        );
    }
}
