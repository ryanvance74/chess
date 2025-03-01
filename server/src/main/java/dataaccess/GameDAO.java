package dataaccess;
import model.AuthData;
import model.GameData;
import java.util.Collection;
import java.util.HashSet;

public interface GameDAO {
    GameData createGame(String gameName);
    Collection<GameData> listGames();
    void updateGame(String gameID);
    void clearData();

}
