package server;

import dataaccess.*;
import dataaccess.Exceptions.DataAccessException;
import dataaccess.Exceptions.DuplicateUserException;
import dataaccess.Exceptions.ServerErrorException;
import dataaccess.Exceptions.UnauthorizedRequestException;
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

    public Object listGames(Request req, Response res) throws DataAccessException {
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
        String authToken = req.headers("authorization");
        UpdateGameRequest partialRequest = gson.fromJson(req.body(), UpdateGameRequest.class);
        UpdateGameRequest updateGameRequest = new UpdateGameRequest(authToken, partialRequest.playerColor(), partialRequest.gameID());
        if (updateGameRequest.gameID() == 0 || updateGameRequest.authToken() == null || updateGameRequest.playerColor() == null) {
            System.out.println(updateGameRequest.gameID());
            System.out.println(updateGameRequest.authToken());
            System.out.println(updateGameRequest.playerColor() == null);
            res.status(400);
            return gson.toJson(new ErrorResult("Error: bad request"));
        }

        try {
            gameService.updateGame(updateGameRequest);
            return gson.toJson(new ErrorResult(""));
        } catch (UnauthorizedRequestException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(401);
            return gson.toJson(result);
        } catch (DuplicateUserException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(403);
            return gson.toJson(result);
        } catch (Exception e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(500);
            return gson.toJson(result);
        }
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
        } catch (Exception e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(500);
            return gson.toJson(result);
        }
    }
}