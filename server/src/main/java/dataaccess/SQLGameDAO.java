package dataaccess;

import model.GameData;

import java.util.Collection;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        // TODO
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS auth (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `white_username` varchar(255) NOT NULL,
              `black_username` varchar(255) NOT NULL,
              `game` TEXT NOT NULL
              `json` TEXT NOT NULL,
              PRIMARY KEY (`token`),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
        DatabaseDAOCommunicator.configureDatabase(createStatements);
    }

    GameData createGame(String gameName) {
        DatabaseDAOCommunicator.configureDatabase(createStatements);
    }
    Collection<GameData> listGames() {

    }
    void updateGame(int gameId, String username, String playerColor) throws DuplicateUserException {

    }
    void clearData() {

    }

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
