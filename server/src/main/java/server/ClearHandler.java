package server;
import dataaccess.AuthDAO;
import dataaccess.Exceptions.DataAccessException;
import service.ErrorResult;
import spark.Request;
import spark.Response;
import service.ClearService;
import dataaccess.*;
import com.google.gson.Gson;

class ClearHandler {
    GameDAO gameDao;
    UserDAO userDao;
    AuthDAO authDao;

    public ClearHandler(AuthDAO authDao, GameDAO gameDao, UserDAO userDao) {
        this.gameDao = gameDao;
        this.userDao = userDao;
        this.authDao = authDao;
    }
    public Object delete(Request req, Response res) {
        Gson gson = new Gson();
        try {
            ClearService.clearDatabase(authDao, userDao, gameDao);

            ErrorResult result = new ErrorResult("");
            return gson.toJson(result);
        } catch (DataAccessException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            return gson.toJson(result);
        }

    }
}
