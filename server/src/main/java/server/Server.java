package server;

import dataaccess.*;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import model.*;
import spark.*;
import service.*;

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
        AuthDAO authDAO = new MemoryAuthDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

//        UserService userService = new UserService();
        UserHandler userHandler = new UserHandler();
        GameHandler gameHandler = new GameHandler();
        ClearHandler clearHandler = new ClearHandler(authDAO, gameDAO, userDAO);

        // clearHandler
        Spark.delete("/db", clearHandler::delete);
        // userHandler
        Spark.post("/user", userHandler::registerUser);
        Spark.post("/session", userHandler::loginSession);
        Spark.delete("/session", userHandler::deleteSession);
        //gameHandler
        Spark.get("/game", gameHandler::getGame);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
