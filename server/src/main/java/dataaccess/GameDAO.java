package dataaccess;
import model.AuthData;
import model.GameData;
import java.util.Collection;
import java.util.HashSet;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(int gameId, String username, String playerColor) throws DataAccessException, DuplicateUserException;
    void clearData() throws DataAccessException;

}
