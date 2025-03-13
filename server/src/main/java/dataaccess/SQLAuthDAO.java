package dataaccess;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import chess.ChessGame;
import com.google.gson.Gson;
import java.sql.*;
import java.sql.SQLException;
import java.util.UUID;

import model.AuthData;

import javax.xml.crypto.Data;

public class SQLAuthDAO implements AuthDAO {
    String insertStatement;
    String clearStatement;
    String authQuery;
    String deleteStatement;
    String emptyQuery;


    public SQLAuthDAO() throws DataAccessException {
        // TODO
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS auth (
              `auth_token` varchar(255) NOT NULL,
              `username` varchar(255) NOT NULL,
              PRIMARY KEY (`auth_token`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };

         this.insertStatement =
                """
            INSERT INTO auth (auth_token, username) VALUES(?,?)
            """;

         this.clearStatement =
                """
            TRUNCATE TABLE auth
            """;

        this.authQuery =
                """
            SELECT username, auth_token FROM auth WHERE auth_token=?
            """;

        this.deleteStatement =
                """
            DELETE FROM auth WHERE auth_token=?
            """;

        DatabaseDAOCommunicator.configureDatabase(createStatements);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        String newAuthToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(newAuthToken,username);

        DatabaseDAOCommunicator.executeUpdate(this.insertStatement, authData.authToken(), authData.username());
        return authData;
    };

    public AuthData getAuth(String authToken) throws DataAccessException {

        ExecuteQueryHandler<AuthData> handler = rs -> {
            if (rs.next()) {
                return new AuthData(rs.getString("auth_token"), rs.getString("username"));
            }
            return null;
        };

        return DatabaseDAOCommunicator.executeQuery(authQuery, handler, authToken);

//        try (ResultSet rs = DatabaseDAOCommunicator.executeQuery(authQuery, authToken)) {
//            String username = rs.getString("username");
//
//            return new AuthData(authToken, username);
//        } catch (SQLException e) {
//            throw new DataAccessException(e.getMessage());
//        }

    };

    public void deleteAuth(AuthData authData) throws DataAccessException {
        DatabaseDAOCommunicator.executeUpdate(this.deleteStatement, authData.authToken());
    };

    public void clearData() throws DataAccessException {
        DatabaseDAOCommunicator.executeUpdate(this.clearStatement);
    };

    public boolean empty() throws DataAccessException {
        return false;
//        return DatabaseDAOCommunicator.checkEmptyTable("auth");
    };


}


