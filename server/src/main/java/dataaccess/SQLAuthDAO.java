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
        // TODO
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS auth (
              `token` int NOT NULL,
              `username` varchar(255) NOT NULL,
              `json` TEXT NOT NULL,
              PRIMARY KEY (`token`),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };

        String[] insertStatement = {
                """
            INSERT INTO auth (token, username, json) VALUES(?,?,?)
            """
        };

        String[] clearStatement = {
                """
            TRUNCATE TABLE auth
            """
        };
        DatabaseDAOCommunicator.configureDatabase(createStatements);
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


}

}
