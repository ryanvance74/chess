package dataaccess;

import java.util.UUID;

import dataaccess.Exceptions.DataAccessException;
import model.AuthData;

public class SQLAuthDAO implements AuthDAO {
    String insertStatement;
    String clearStatement;
    String authQuery;
    String deleteStatement;
    String emptyQuery;


    public SQLAuthDAO() throws DataAccessException {
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

        this.emptyQuery =
            """
        SELECT EXISTS(SELECT 1 FROM auth LIMIT 1)
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
    };

    public void deleteAuth(AuthData authData) throws DataAccessException {
        DatabaseDAOCommunicator.executeUpdate(this.deleteStatement, authData.authToken());
    };

    public void clearData() throws DataAccessException {
        DatabaseDAOCommunicator.executeUpdate(this.clearStatement);
    };

    public boolean empty() throws DataAccessException {
        return DatabaseDAOCommunicator.checkEmptyTable(this.emptyQuery);
    };


}
