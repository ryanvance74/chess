package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class MemoryGameDAO implements GameDAO{
    Collection<GameData> gameDataHashSet = HashSet.newHashSet(1000);

    public GameData createGame(String gameName) {
        int newGameId = UUID.randomUUID().hashCode();
        ChessGame newChessGame = new ChessGame();
        GameData newGameData = new GameData(newGameId, null, null, gameName, newChessGame);
        gameDataHashSet.add(newGameData);

        return newGameData;
    }
   public Collection<GameData> listGames() {
        // TODO
        return null;
    }
    public void updateGame(String gameID) {
        // TODO
    }

    public void clearData() {
        gameDataHashSet.clear();
    }
}
