package server;

import dataaccess.BadRequestException;
import dataaccess.DuplicateUserException;
import dataaccess.GameDAO;
import dataaccess.ServerErrorException;
import service.ErrorResult;
import service.GameService;
import service.RegisterRequest;
import service.RegisterResult;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

class GameHandler {
    GameService gameService;
    GameDAO gameDao;

    public GameHandler(GameDAO gameDao) {
        this.gameService = new GameService();
        this.gameDao = gameDao;
    }

    public Object listGames(Request req, Response res) {
        Gson gson = new Gson();
        String authToken = req.headers("authorization");
        try {
            ListGamesResult result = gameService.listGames(authToken);
            return gson.toJson(result);
        } catch (DuplicateUserException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(403);
            return gson.toJson(result);
        } catch (BadRequestException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(400);
            return gson.toJson(result);
        } catch (ServerErrorException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(500);
            return gson.toJson(result);
        }
    }

    public Object joinGame(Request req, Response res) {
        return "empty";
    }

    public Object createGame(Request req, Response res) {
        return "pass";
    }
}