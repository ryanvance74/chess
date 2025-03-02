package server;

import dataaccess.*;
import service.*;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

class GameHandler {
    GameService gameService;
    GameDAO gameDao;
    AuthDAO authDao;
    Gson gson;

    public GameHandler(GameDAO gameDao, AuthDAO authDao) {
        this.gameService = new GameService(gameDao, authDao);
        this.gameDao = gameDao;
        this.gson = new Gson();
    }

    public Object listGames(Request req, Response res) {
        String authToken = req.headers("authorization");
        try {
            ListGamesResult result = gameService.listGames(authToken);
            return gson.toJson(result);
        } catch (UnauthorizedRequestException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(401);
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
        String authToken = req.headers("authorization");
        CreateGameRequest partialRequest = gson.fromJson(req.body(), CreateGameRequest.class);
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, partialRequest.gameName());
        if (createGameRequest.gameName().isEmpty() || createGameRequest.authToken().isEmpty()) {
            res.status(400);
            return new ErrorResult("Error: bad request");
        }

        try {
            CreateGameResult result = gameService.createGame(createGameRequest);
            return gson.toJson(result);
        } catch (UnauthorizedRequestException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(401);
            return gson.toJson(result);
        } catch (ServerErrorException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(500);
            return gson.toJson(result);
        }
    }
}