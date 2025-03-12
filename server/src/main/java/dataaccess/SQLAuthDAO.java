package dataaccess;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import chess.ChessGame;
import com.google.gson.Gson;
import java.sql.*;
import java.sql.SQLException;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO {

    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    AuthData createAuth(String username) {

    };
    AuthData getAuth(String authToken) {

    };
    void deleteAuth(AuthData authData) {

    };
    void clearData() {

    };
    boolean empty() {

    };







    // TODO
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(type),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    }

}
