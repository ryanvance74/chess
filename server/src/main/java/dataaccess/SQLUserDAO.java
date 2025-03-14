package dataaccess;

import dataaccess.Exceptions.DataAccessException;
import dataaccess.Exceptions.DuplicateUserException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class SQLUserDAO implements UserDAO{
    String insertStatement;
    String clearStatement;
    String getUserStatement;
    String deleteStatementStub;
    String emptyQuery;

    public SQLUserDAO() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar(255) NOT NULL,
              `password` varchar(255) NOT NULL,
              `email` varchar(255) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };

        this.insertStatement =
                """
            INSERT INTO user (username, password, email) VALUES(?,?,?)
            """;

        this.clearStatement =
                """
            TRUNCATE TABLE user
            """;

        this.getUserStatement =
                """
            SELECT username, password, email FROM user WHERE username=?
            """;

        this.deleteStatementStub =
                """
            DELETE FROM user WHERE username=
            """;

        this.emptyQuery =
                """
            SELECT EXISTS(SELECT 1 FROM user LIMIT 1)
            """;

        DatabaseDAOCommunicator.configureDatabase(createStatements);
    }

    public UserData createUser(String username, String password, String email) throws DataAccessException, DuplicateUserException {
        UserData userData = new UserData(username,password,email);

        String hashedPassword = hashPassword(password);
        System.out.println("running query: " + this.insertStatement + username + hashedPassword + email);
        int result = DatabaseDAOCommunicator.executeUpdate(this.insertStatement, username, hashedPassword, email);
        return userData;
    }

    public UserData getUser(String username) throws DataAccessException {

        ExecuteQueryHandler<UserData> handler = rs -> {
            if (rs.next()) {
                return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
            return null;
        };

        return DatabaseDAOCommunicator.executeQuery(getUserStatement, handler, username);
    }

    public void clearData() throws DataAccessException{
        DatabaseDAOCommunicator.executeUpdate(clearStatement);
    }

    private String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
    }

    public boolean verifyUser(String inputPassword, String hashedPassword) {
        return BCrypt.checkpw(inputPassword, hashedPassword);
    }

    public boolean empty() throws DataAccessException {
        return DatabaseDAOCommunicator.checkEmptyTable(this.emptyQuery);
    }
}
