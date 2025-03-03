package service;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.ServerErrorException;
import dataaccess.UnauthorizedRequestException;
import model.AuthData;
import model.GameData;
import java.util.Collection;

public class GameService {
    GameDAO gameDao;
    AuthDAO authDao;
    public GameService(GameDAO gameDao, AuthDAO authDao) {
        this.gameDao = gameDao;
        this.authDao = authDao;
    }

    public ListGamesResult listGames(String authToken) throws UnauthorizedRequestException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedRequestException("Error: unauthorized");
        } else {
            try {
                return new ListGamesResult(gameDao.listGames());
            } catch (Exception e) {
                throw new ServerErrorException(e.getMessage());
            }

        }
    }

    public CreateGameResult createGame(CreateGameRequest request) throws UnauthorizedRequestException {
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

    public void updateGame(UpdateGameRequest request) throws UnauthorizedRequestException {
        AuthData authData = authDao.getAuth(request.authToken());
        if (authData == null) {
            throw new UnauthorizedRequestException("Error: unauthorized");
        } else {
            try {
                gameDao.updateGame(request.gameID(), authData.username(), request.playerColor());
            } catch (Exception e) {
                System.out.println("catching an error here");
                throw new ServerErrorException(e.getMessage());
            }

        }
    }
}
