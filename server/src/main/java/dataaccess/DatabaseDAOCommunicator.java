package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DatabaseDAOCommunicator {

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
         return gson.toJson(game);
    }

    public static int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    // TODO
//                    if (param instanceof String p) ps.setString(i + 1, p);
//                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
//                    else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
//                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("[500] unable to update database: %s, %s", statement, e.getMessage()));
        }
}
