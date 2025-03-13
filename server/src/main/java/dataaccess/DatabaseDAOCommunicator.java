package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Types.NULL;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DatabaseDAOCommunicator {

    public final static String emptyQueryStub =
            """
        SELECT COUNT(*) FROM
        """;

     public static void configureDatabase(String[] createStatements) throws DataAccessException {
         DatabaseManager.createDatabase();
         try (var conn = DatabaseManager.getConnection()) {
             for (var statement : createStatements) {
                 try (var preparedStatement = conn.prepareStatement(statement)) {
                     preparedStatement.executeUpdate();
                 }
             }
         } catch (SQLException ex) {
             throw new DataAccessException(String.format("[500] Unable to configure database: %s", ex.getMessage()));
         }
     }

    public static ChessGame readGame(ResultSet rs) throws SQLException {
         Gson gson = new Gson();
         // ID?
         return gson.fromJson(rs.getString("json"), ChessGame.class);
    }


    public static String serializeGame(ChessGame game) {
         Gson gson = new Gson();
         return gson.toJson(game, ChessGame.class);
    }

    public static int executeUpdate(String statement, Object... params) throws DataAccessException, DuplicateUserException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                prepareStatementHelper(ps, params);
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DuplicateUserException(e.getMessage());
            } else {
                throw new DataAccessException(String.format("[500] unable to update database: %s, %s", statement, e.getMessage()));
            }

        }

    }

    public static ResultSet executeQuery(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                prepareStatementHelper(ps, params);
                ps.executeUpdate();

                return ps.executeQuery();

            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("[500] unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    public static boolean checkEmptyTable(String table) throws DataAccessException {
        try (ResultSet rs = DatabaseDAOCommunicator.executeQuery(emptyQueryStub + table)) {
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private static void prepareStatementHelper(PreparedStatement ps, Object... params) throws SQLException {
        for (var i = 0; i < params.length; i++) {
            var param = params[i];
            // TODO maybe done?
            switch (param) {
                case String p -> ps.setString(i + 1, p);
                case Integer p -> ps.setInt(i + 1, p);
                case ChessGame p -> ps.setString(i + 1, serializeGame(p));
                case null -> ps.setNull(i + 1, NULL);
                default -> {
                }
            }
        }
    }



}
