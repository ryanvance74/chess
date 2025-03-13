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
              `auth_token` int NOT NULL,
              `username` varchar(255) NOT NULL,
              `json` TEXT NOT NULL,
              PRIMARY KEY (`auth_token`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };

        String insertStatement =
                """
            INSERT INTO auth (auth_token, username, json) VALUES(?,?,?)
            """;

        String clearStatement =
                """
            TRUNCATE TABLE auth
            """;

        String authQuery =
                """
            SELECT json FROM auth WHERE auth_token=?
            """;

        String deleteStatement =
                """
            DELETE FROM auth WHERE (auth_token=?) VALUES(?)
            """;

        DatabaseDAOCommunicator.configureDatabase(createStatements);
    }

    public AuthData createAuth(String username) throws DataAccessException {
        String newAuthToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(newAuthToken,username);

        DatabaseDAOCommunicator.executeUpdate(this.insertStatement, authData.authToken(), authData.username(), new Gson().toJson(authData, AuthData.class));
        return authData;
    };

    public AuthData getAuth(String authToken) throws DataAccessException {

        try (ResultSet rs = DatabaseDAOCommunicator.executeQuery(authQuery, authToken)) {
            return new Gson().fromJson(rs.getString("json"), AuthData.class);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

    };

    public void deleteAuth(AuthData authData) throws DataAccessException {
        DatabaseDAOCommunicator.executeUpdate(this.deleteStatement, authData.authToken());
    };

    public void clearData() throws DataAccessException {
        DatabaseDAOCommunicator.executeUpdate(this.clearStatement);
    };

    public boolean empty() throws DataAccessException {
        return DatabaseDAOCommunicator.checkEmptyTable("auth");
    };


}


