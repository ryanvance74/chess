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
        SELECT EXISTS(SELECT 1 FROM %s LIMIT 1)
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
                int rows = ps.executeUpdate();

                try (var rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
//                var rs = ps.getGeneratedKeys();
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }

                return rows;
                // tried overriding failure with this exception and it seemed to fix the createUser method. createGame is still broken.
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DuplicateUserException(e.getMessage());
            } else {
                throw new DataAccessException(String.format("[500] unable to update database: %s, %s", statement, e.getMessage()));
            }

        }
    }

    public static <T> T executeQuery(String statement, ExecuteQueryHandler<T> handler,  Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                prepareStatementHelper(ps, params);
                try (var rs = ps.executeQuery()) {
                    return handler.handle(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("[500] unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    public static boolean checkEmptyTable(String tableQuery) throws DataAccessException {
         DatabaseDAOCommunicator.executeQuery(tableQuery, rs -> {
             if (rs.next()) {
                 return rs.getInt(1);
             }
             return 0;
         });

         ExecuteQueryHandler<Boolean> handler = rs -> {
             if (rs.next()) {
                 return rs.getInt(1) == 0;
             }
             return true;
         };
         return executeQuery(tableQuery, handler);
//       try (ResultSet rs = DatabaseDAOCommunicator.executeQuery(tableQuery, rs -> {rs.getInt(1);})) {
//           return rs.getInt(1) == 0;
//       } catch (SQLException e) {
//          throw new DataAccessException(e.getMessage());
//       }
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
