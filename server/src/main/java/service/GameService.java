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

    public Collection<GameData> listGames(String authToken) throws UnauthorizedRequestException {
        AuthData authData = authDao.getAuth(authToken);
        if (authData == null) {
            throw new UnauthorizedRequestException("Error: unauthorized");
        } else {
            try {
                return
            } catch (Exception e) {
                throw new ServerErrorException(e.getMessage());
            }

        }
    }
}
