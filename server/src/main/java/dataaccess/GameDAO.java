package dataaccess;
import chess.ChessGame;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DuplicateUserException;
import model.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface GameDAO {
    GameData createGame(String gameName) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    void updateGame(int gameId, String username, String playerColor) throws DataAccessException, DuplicateUserException;
    void clearData() throws DataAccessException;
    void updateGameState(ChessGame game, int gameId) throws DataAccessException;
    void removePlayerFromGame(int gameId, String username) throws DataAccessException;
}
