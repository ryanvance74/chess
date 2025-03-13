package server;

import dataaccess.*;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.*;
import spark.*;
import service.*;

import javax.xml.crypto.Data;

public class Server {

    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        createRoutes();
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private static void createRoutes() {
        AuthDAO authDao;
        UserDAO userDao;
        GameDAO gameDao;

        try {
            authDao = new SQLAuthDAO();
            userDao = new SQLUserDAO();
            gameDao = new SQLGameDAO();

            UserHandler userHandler = new UserHandler(userDao, authDao);
            GameHandler gameHandler = new GameHandler(gameDao, authDao);
            ClearHandler clearHandler = new ClearHandler(authDao, gameDao, userDao);

            // clearHandler
            Spark.delete("/db", clearHandler::delete);
            // userHandler
            Spark.post("/user", userHandler::registerUser);
            Spark.post("/session", userHandler::loginSession);
            Spark.delete("/session", userHandler::deleteSession);
            //gameHandler
            Spark.get("/game", gameHandler::listGames);
            Spark.post("/game", gameHandler::createGame);
            Spark.put("/game", gameHandler::joinGame);

        } catch (DataAccessException e) {
            System.out.println("FATAL ERROR WHEN CREATING DAOs.");
            System.exit(1);
        }


    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
