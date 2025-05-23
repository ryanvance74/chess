package server;

import dataaccess.*;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DuplicateUserException;
import dataaccess.exceptions.ServerErrorException;
import dataaccess.exceptions.UnauthorizedRequestException;
import requests.*;
import service.*;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

class GameHandler {
    GameService gameService;
    Gson gson;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
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