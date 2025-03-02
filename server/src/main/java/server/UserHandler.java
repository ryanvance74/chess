package server;

import org.eclipse.jetty.server.Authentication;
import service.*;
import spark.Request;
import spark.Response;
import dataaccess.*;
import model.*;
import com.google.gson.Gson;
import service.UserService;
import com.google.gson.JsonArray;

class UserHandler {
    UserDAO userDao;
    Gson gson;
    UserService userService;
    public UserHandler(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.gson = new Gson();
        this.userService = new UserService(userDao, authDao);
    }

    public Object registerUser(Request req, Response res) {
        RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
        try {
            RegisterResult result = userService.register(registerRequest);
            return gson.toJson(result);
        } catch (DuplicateUserException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(403);
            return gson.toJson(result);
        } catch (BadRequestException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(400);
            return gson.toJson(result);
        } catch (ServerErrorException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(500);
            return gson.toJson(result);
        }
    }

    public Object loginSession(Request req, Response res) {
        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
        try {
            LoginResult result = userService.login(loginRequest);
            return gson.toJson(result);
        } catch (UnauthorizedRequestException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(401);
            return gson.toJson(result);
        } catch (ServerErrorException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(500);
            return gson.toJson(result);
        }
    }

    public Object deleteSession(Request req, Response res) {
        String authToken = req.headers("authorization");
        System.out.println("testing testing");
        System.out.println(authToken);
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        try {
            userService.logout(logoutRequest);
            return gson.toJson(new ErrorResult(""));
        } catch (UnauthorizedRequestException e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(401);
            return gson.toJson(result);
        } catch (Exception e) {
            ErrorResult result = new ErrorResult(e.getMessage());
            res.status(500);
            return gson.toJson(result);
        }

    }

}