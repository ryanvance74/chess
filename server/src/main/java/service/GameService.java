package service;
import dataaccess.*;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    GameDAO gameDao;
    AuthDAO authDao;
    public GameService(GameDAO gameDao, AuthDAO authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public ListGamesResult listGames(String authToken) throws DataAccessException, UnauthorizedRequestException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedRequestException("Error: unauthorized");
        } else {
            try {
                Collection<GameData> games = gameDao.listGames();
                Collection<ListGameResultSingle> listGameResultSingles = new ArrayList<>();
                for (GameData game : games) {
                    listGameResultSingles.add(new ListGameResultSingle(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
                }
                return new ListGamesResult(listGameResultSingles);
            } catch (Exception e) {
                throw new ServerErrorException(e.getMessage());
            }

        }
    }

    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException, UnauthorizedRequestException {
        AuthData authData = authDao.getAuth(request.authToken());
        if (authData == null) {
            throw new UnauthorizedRequestException("Error: unauthorized");
        } else {
            try {
                GameData game = gameDao.createGame(request.gameName());
                return new CreateGameResult(game.gameID());
            } catch (Exception e) {
                throw new ServerErrorException(e.getMessage());
            }

        }
    }

    public void updateGame(UpdateGameRequest request) throws DataAccessException, UnauthorizedRequestException, DuplicateUserException {
        AuthData authData = authDao.getAuth(request.authToken());
        if (authData == null) {
            throw new UnauthorizedRequestException("Error: unauthorized");
        } else {
            try {
                gameDao.updateGame(request.gameID(), authData.username(), request.playerColor().toString());
            } catch (DuplicateUserException e) {
                throw new DuplicateUserException(e.getMessage());
            } catch (Exception e) {
//                System.out.println("catching an error here");
                throw new ServerErrorException(e.getMessage());
            }

        }
    }
}
