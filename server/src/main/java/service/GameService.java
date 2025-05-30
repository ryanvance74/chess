package service;
import chess.ChessMove;
import chess.ChessGame;
import chess.InvalidMoveException;
import dataaccess.*;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DuplicateUserException;
import dataaccess.exceptions.ServerErrorException;
import dataaccess.exceptions.UnauthorizedRequestException;
import model.AuthData;
import model.GameData;
import requests.*;

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
                Collection<GameResultSingle> lsGameResults = new ArrayList<>();
                for (GameData game : games) {
                    lsGameResults.add(new GameResultSingle(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));
                }
                return new ListGamesResult(lsGameResults);
            } catch (Exception e) {
                throw new ServerErrorException(e.getMessage());
            }
        }
    }

    public GameData getGameFromId(String authToken, int gameId) throws DataAccessException, UnauthorizedRequestException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedRequestException("Error: unauthorized");
        } else {
            try {
                Collection<GameData> games = gameDao.listGames();
                for (GameData game : games) {
                    if (game.gameID() == gameId) {
                        return game;
                    }
                }
            } catch (Exception e) {
                throw new ServerErrorException(e.getMessage());
            }
        }
        return null;
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

    public ChessGame updateGameState(String authToken, int gameId, ChessMove move) throws DataAccessException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedRequestException("Error: unauthorized");
        } else {
            try {
                GameData gameData = getGameFromId(authToken, gameId);
                ChessGame game = gameData.game();
                game.makeMove(move);
                gameDao.updateGameState(game, gameData.gameID());
                return game;
            } catch (DuplicateUserException e) {
                throw new DuplicateUserException(e.getMessage());
            } catch (Exception e) {
//                System.out.println("catching an error here");
                throw new ServerErrorException(e.getMessage());
            }
        }
    }

    public void removeUserFromGame(String authToken, int gameId) throws DataAccessException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedRequestException("Error: unauthorized");
        } else {
            try {
                gameDao.removePlayerFromGame(gameId, authData.username());
            } catch (DuplicateUserException e) {
                throw new DuplicateUserException(e.getMessage());
            } catch (Exception e) {
//                System.out.println("catching an error here");
                throw new ServerErrorException(e.getMessage());
            }
        }
    }

    public void setGameHasResigned(String authToken, int gameId) throws DataAccessException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedRequestException("Error: unauthorized");
        } else {
            try {
                GameData gameData = getGameFromId(authToken, gameId);
                ChessGame game = gameData.game();
                game.setHasResigned();
                gameDao.updateGameState(game, gameData.gameID());
            } catch (DuplicateUserException e) {
                throw new DuplicateUserException(e.getMessage());
            } catch (Exception e) {
//                System.out.println("catching an error here");
                throw new ServerErrorException(e.getMessage());
            }
        }
    }
}
