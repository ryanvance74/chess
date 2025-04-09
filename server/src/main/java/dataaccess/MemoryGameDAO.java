package dataaccess;

import chess.ChessGame;
import dataaccess.exceptions.DuplicateUserException;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

public class MemoryGameDAO implements GameDAO{
    HashMap<Integer, GameData> gameDataHashMap = new HashMap<Integer, GameData>();

    public GameData createGame(String gameName) {
        int newGameId = UUID.randomUUID().hashCode() & Integer.MAX_VALUE;
        ChessGame newChessGame = new ChessGame();
        GameData newGameData = new GameData(newGameId, null, null, gameName, newChessGame);
        gameDataHashMap.put(newGameData.gameID(), newGameData);

        return newGameData;
    }
   public Collection<GameData> listGames() {
        return gameDataHashMap.values();
    }

    public void updateGame(int gameId, String username, String playerColor) throws DuplicateUserException {
        GameData game = gameDataHashMap.get(gameId);
        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername() != null) {
                throw new DuplicateUserException("Error: already taken");
            } else {
                gameDataHashMap.put(gameId, new GameData(gameId, username, game.blackUsername(), game.gameName(), game.game()));
            }
        } else {
            if (game.blackUsername() != null) {
                throw new DuplicateUserException("Error: already taken");
            } else {
                gameDataHashMap.put(gameId, new GameData(gameId, game.whiteUsername(), username, game.gameName(), game.game()));
            }
        }

    }
    public void clearData() {
        gameDataHashMap.clear();
    }

    public boolean empty() {
        return gameDataHashMap.isEmpty();
    }

    public void removePlayerFromGame(int number, String word) {}
    public void updateGameState(ChessGame game, int number) {}
}
