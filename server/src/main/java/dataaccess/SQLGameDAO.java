package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.DuplicateUserException;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {
    String insertStatement;
    String clearStatement;
    String gameQuery;
    String deleteStatement;
    String listQuery;
    String emptyQuery;

    public SQLGameDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS game (
              `game_id` int NOT NULL AUTO_INCREMENT,
              `white_username` varchar(255),
              `black_username` varchar(255),
              `game_name` varchar(255) NOT NULL,
              `chess_game` TEXT NOT NULL,
              PRIMARY KEY (`game_id`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };

        this.insertStatement =
                """
            INSERT INTO game (white_username, black_username, game_name, chess_game) VALUES(?,?,?,?)
            """;

        this.clearStatement =
                """
            TRUNCATE TABLE game
            """;

        this.gameQuery =
                """
            SELECT json FROM game WHERE game_id=?
            """;

        this.deleteStatement =
                """
            DELETE FROM game WHERE game_id=?
            """;

        this.listQuery =
                """
           SELECT * FROM game
           """;

        this.emptyQuery =
                """
            SELECT EXISTS(SELECT 1 FROM game LIMIT 1)
            """;

        DatabaseDAOCommunicator.configureDatabase(createStatements);
    }

    public GameData createGame(String gameName) throws DataAccessException {

        ChessGame newGame = new ChessGame();
        int gameId = DatabaseDAOCommunicator.executeUpdate(this.insertStatement, null,null, gameName, newGame);
        return new GameData(gameId, null,null, gameName, newGame);

    }

    public Collection<GameData> listGames() throws DataAccessException {

        ExecuteQueryHandler<Collection<GameData>> handler = rs -> {
            Collection<GameData> games = new ArrayList<>();
            Gson gson = new Gson();
            while (rs.next()) {
                games.add(processGame(rs, gson));
            }
            return games;
        };
        return DatabaseDAOCommunicator.executeQuery(this.listQuery, handler);

    }

    public void updateGame(int gameId, String username, String playerColor) throws DataAccessException, DuplicateUserException {

        GameData gameData = validateId(gameId);
        if (gameData == null) {
            throw new DataAccessException("ERROR: failed to retrieve game by given game ID. Does not exist.");
        }

        if (playerColor.equals("WHITE")) {
            if (gameData.whiteUsername() != null) {
                throw new DuplicateUserException("Error: already taken");
            } else {
                String updateStatement = "UPDATE game SET white_username=? WHERE game_id=?";
                DatabaseDAOCommunicator.executeUpdate(updateStatement, username, gameData.gameID());
            }
        } else {
            if (gameData.blackUsername() != null) {
                throw new DuplicateUserException("Error: already taken");
            } else {
                String updateStatement = "UPDATE game SET black_username=? WHERE game_id=?";
                DatabaseDAOCommunicator.executeUpdate(updateStatement, username, gameData.gameID());
            }
        }
    }

    public void updateGameState(ChessGame game, int gameId) throws DataAccessException, DuplicateUserException {

        GameData gameData = validateId(gameId);
        if (gameData == null) {
            throw new DataAccessException("ERROR: failed to retrieve game by given game ID. Does not exist.");
        }
        String updateStatement = "UPDATE game SET chess_game=? WHERE game_id=?";
        DatabaseDAOCommunicator.executeUpdate(updateStatement, game, gameData.gameID());
    }

    public void removePlayerFromGame(int gameId, String username) throws DataAccessException {
        GameData gameData = validateId(gameId);
        if (gameData == null) {
            throw new DataAccessException("ERROR: failed to retrieve game by given game ID. Does not exist.");
        }
        String updateStatement = "UPDATE game SET white_username = IF(white_username = ?, NULL, white_username), " +
                "black_username = IF(black_username = ?, NULL, black_username) WHERE game_id=?";
        DatabaseDAOCommunicator.executeUpdate(updateStatement, username, username, gameId);
    }

    private GameData validateId(int gameId) throws DataAccessException {
        GameData gameData;
        String updateQuery = "SELECT game_id, white_username, black_username, game_name, chess_game FROM game WHERE game_id=?";

        ExecuteQueryHandler<GameData> handler = rs -> {
            if (rs.next()) {
                return processGame(rs, new Gson());
            }
            return null;
        };
        return DatabaseDAOCommunicator.executeQuery(updateQuery, handler, String.valueOf(gameId));
    }

    public void clearData() throws DataAccessException {
        DatabaseDAOCommunicator.executeUpdate(this.clearStatement);
    }

    private GameData processGame(ResultSet rs, Gson gson) throws SQLException {
        int gameId = rs.getInt("game_id");
        String whiteUsername = rs.getString("white_username");
        String blackUsername = rs.getString("black_username");
        String gameName      = rs.getString("game_name");
        ChessGame chessGame  = gson.fromJson(rs.getString("chess_game"),ChessGame.class);

        return new GameData(gameId, whiteUsername, blackUsername, gameName, chessGame);
    }

    public boolean empty() throws DataAccessException {
        return DatabaseDAOCommunicator.checkEmptyTable(this.emptyQuery);
    }
}
