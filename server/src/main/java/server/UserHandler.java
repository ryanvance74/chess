package server;

import org.eclipse.jetty.server.Authentication;
import service.ErrorResult;
import service.RegisterRequest;
import service.RegisterResult;
import spark.Request;
import spark.Response;
import dataaccess.*;
import model.*;
import com.google.gson.Gson;
import service.UserService;

class UserHandler {
    UserDAO userDao;
    Gson gson;
    UserService userService;
    public UserHandler(UserDAO userDao) {
        this.userDao = userDao;
        this.gson = new Gson();
        this.userService = new UserService(userDao);
    }

    public Object registerUser(Request req, Response res) {
        RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
        try {
            RegisterResult result = userService.register(registerRequest);
            return gson.toJson(result);
        } catch (DataAccessException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            return gson.toJson(result);
        }
    }

    public Object loginSession(Request req, Response res) {
        return "empty";
    }

    public Object deleteSession(Request req, Response res) {
        return "pass";
    }

}