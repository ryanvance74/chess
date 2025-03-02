package service;
import dataaccess.*;
import model.GameData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import passoff.model.*;
import server.Server;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Collection;

public class InternalUnitTests {
    private static MemoryUserDAO userDao;
    private static MemoryGameDAO gameDao;
    private static MemoryAuthDAO authDao;
    private static UserService userService;
    private static GameService gameService;

    @BeforeEach
    public void setup() {
        userDao = new MemoryUserDAO();
        gameDao = new MemoryGameDAO();
        authDao = new MemoryAuthDAO();
        userService = new UserService(userDao, authDao);
        gameService = new GameService();

    }

    @DisplayName("Clear Database")
    @Test
    public void goodClear() {


        Assertions.assertDoesNotThrow(() -> userDao.createUser("myUsername1234", "myPassword5678", "test@email.byu.edu"));
        Assertions.assertDoesNotThrow(() -> userDao.createUser("testUser_jdk", "epic_password_529", "vim@nano.edu"));

        gameDao.createGame("Deutsches Spiel");
        gameDao.createGame("Schweizer Spiel ");
        gameDao.createGame("Österreichisches Spiel");

        authDao.createAuth("small_user");
        authDao.createAuth("FC_Bayern279");

        Assertions.assertDoesNotThrow(() -> ClearService.clearDatabase(authDao, userDao, gameDao));
        Assertions.assertTrue(authDao.empty() && gameDao.empty() && userDao.empty());
    }

    @Test
    public void goodRegister() {
        Assertions.assertDoesNotThrow(() -> userService.register(new RegisterRequest("testUser_jdk", "epic_password_529", "vim@nano.edu")));
        Assertions.assertDoesNotThrow(() -> userService.register(new RegisterRequest("myUsername1234", "myPassword5678", "test@email.byu.edu")));

    }

    @Test
    public void badRegister() {
        Assertions.assertThrows(BadRequestException.class, () -> {
            userService.register(new RegisterRequest("myUsername1234", "", "test@email.byu.edu"));
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
        gameDao.createGame("game2435");
        gameDao.createGame("spiel21");
        Collection<GameData> array = new ArrayList<>();

        Assertions.assertTrue(() -> !gameService.listGames().isEmpty());

    }
}
