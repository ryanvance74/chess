package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{
    String insertStatement;
    String clearStatement;
    String userQuery;
    String deleteStatement;
    String emptyQuery;

    public SQLUserDAO() throws DataAccessException {
        // TODO
        String[] createStatements = {
                """
                DROP TABLE user
            """,
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

        this.userQuery =
                """
            SELECT json FROM user WHERE (username=?) VALUES(?)
            """;

        this.deleteStatement =
                """
            DELETE FROM user WHERE (username=?) VALUES(?)
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

        try (ResultSet rs = DatabaseDAOCommunicator.executeQuery(userQuery, username)) {
            return new Gson().fromJson(rs.getString("json"),UserData.class);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

    }

    public void clearData() throws DataAccessException{
        DatabaseDAOCommunicator.executeUpdate(clearStatement);
    }

    private String hashPassword(String clearTextPassword) {
        return BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

    }

    boolean verifyUser(String hashedPassword, String providedClearTextPassword) {
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }
}
