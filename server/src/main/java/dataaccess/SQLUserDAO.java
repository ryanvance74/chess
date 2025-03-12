package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO{

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
            INSERT INTO user (username, password, game, email, json) VALUES(?,?,?,?,?)
            """
        };

        String[] clearStatement = {
                """
            TRUNCATE TABLE user
            """
        };

        DatabaseDAOCommunicator.configureDatabase(createStatements);
    }

    public UserData createUser(String username, String password, String email) throws DuplicateUserException {

    }

    public UserData getUser(String username) {

    }

    public void clearData() {

    }
}
