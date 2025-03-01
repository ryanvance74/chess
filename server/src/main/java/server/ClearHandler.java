package server;
import dataaccess.AuthDAO;
import spark.Spark;
import spark.Request;
import spark.Response;
import service.ClearService;
import dataaccess.*;

class ClearHandler {
    ClearService clearService;
    GameDAO gameDao;
    UserDAO userDao;
    AuthDAO authDao;

    public ClearHandler(AuthDAO authDao, GameDAO gameDao, UserDAO userDao) {
        this.clearService = new ClearService();
        this.gameDao = gameDao;
        this.userDao = userDao;
        this.authDao = authDao;
    }
    public Object delete(Request req, Response res) {
        try {
            ClearService.clearDatabase(authDao, userDao, gameDao);
        }

    }
}
