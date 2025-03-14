package dataaccess;
import dataaccess.Exceptions.DataAccessException;
import dataaccess.Exceptions.DuplicateUserException;
import model.GameData;
import java.util.Collection;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(int gameId, String username, String playerColor) throws DataAccessException, DuplicateUserException;
    void clearData() throws DataAccessException;

}
