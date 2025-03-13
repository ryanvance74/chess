package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import javax.xml.crypto.Data;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class SQLGameDAO implements GameDAO {
    String insertStatement;
    String clearStatement;
    String gameQuery;
    String deleteStatement;
    String listQuery;

    public SQLGameDAO() throws DataAccessException {
        // TODO
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS game (
              `game_id` int NOT NULL AUTO_INCREMENT,
              `white_username` varchar(255),
              `black_username` varchar(255),
              `game_name` TEXT NOT NULL,
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

        DatabaseDAOCommunicator.configureDatabase(createStatements);
    }

    public GameData createGame(String gameName) throws DataAccessException {

        ChessGame newGame = new ChessGame();
        int gameId = DatabaseDAOCommunicator.executeUpdate(this.insertStatement, "","", gameName, newGame);
        return new GameData(gameId, "","", gameName, newGame);

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
        Gson gson = new Gson();

        GameData gameData = validateId(gameId);
        // TODO: adapt this to SQL DAO. maybe need to abstract this between the two implementations of this DAO
        if (playerColor.equals("WHITE")) {
            if (gameData.whiteUsername() != null) {
                throw new DuplicateUserException("Error: already taken");
            } else {

                String updateStatement = "UPDATE game SET white_username=" + username + "WHERE game_id=" + gameId;
                int result = DatabaseDAOCommunicator.executeUpdate(updateStatement);
                if (result != 0) {
                    throw new DataAccessException("ERROR upon execution of update query");
                }
            }
        } else {
            if (gameData.blackUsername() != null) {
                throw new DuplicateUserException("Error: already taken");
            } else {
                String updateStatement = "UPDATE game SET black_username=" + username + "WHERE game_id=" + gameId;
                int result = DatabaseDAOCommunicator.executeUpdate(updateStatement);
                if (result != 0) {
                    throw new DataAccessException("ERROR upon execution of update query");
                }
            }
        }
    }

    public void updateGame(int gameId, ChessMove move) throws DataAccessException {
        Gson gson = new Gson();
        GameData gameData = validateId(gameId);

        ChessGame game = gameData.game();

        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new DataAccessException(e.getMessage());
        }

        String serializedGame = DatabaseDAOCommunicator.serializeGame(game);
        String updateStatement = "UPDATE game SET game=" + serializedGame + "WHERE game_id=" + gameId;
        int result = DatabaseDAOCommunicator.executeUpdate(updateStatement);
        if (result != 0) {
            throw new DataAccessException("ERROR upon execution of update query");
        }
    }

    private GameData validateId(int gameId) throws DataAccessException {
//        GameData gameData;
//        Gson gson = new Gson();
//        String updateQuery = "SELECT white_username, black_username FROM game WHERE gameId=" + String.valueOf(gameId);
//
//        try (ResultSet rs = DatabaseDAOCommunicator.executeQuery(updateQuery)) {
//
//            if (!rs.next()) {
//                throw new DataAccessException("ERROR: invalid game ID.");
//            } else {
//                gameData = processGame(rs, gson);
//            }
//        } catch (SQLException e) {
//            throw new DataAccessException(e.getMessage());
//        }
//        return gameData;
    return null;
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

}
