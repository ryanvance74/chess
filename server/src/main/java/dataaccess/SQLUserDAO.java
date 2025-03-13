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
            CREATE TABLE IF NOT EXISTS user (
              `username` varchar(255) NOT NULL,
              `password` varchar(255) NOT NULL,
              `email` varchar(255) NOT NULL,
              `json` TEXT NOT NULL,
              PRIMARY KEY (`username`),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };

        String[] insertStatement = {
                """
            INSERT INTO user (username, password, email, json) VALUES(?,?,?,?)
            """
        };

        String[] clearStatement = {
                """
            TRUNCATE TABLE user
            """
        };

        String userQuery =
                """
            SELECT json FROM user WHERE (username=?) VALUES(?)
            """;

        String deleteStatement =
                """
            DELETE FROM user WHERE (username=?) VALUES(?)
            """;

        String listQuery =
                """
           SELECT * FROM user
           """;

        DatabaseDAOCommunicator.configureDatabase(createStatements);
    }

    public UserData createUser(String username, String password, String email) throws DataAccessException, DuplicateUserException {
        UserData userData = new UserData(username,password,email);

        String hashedPassword = hashPassword(password);
        DatabaseDAOCommunicator.executeUpdate(insertStatement, username, hashedPassword, email, userData);
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
