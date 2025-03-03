package service;
import dataaccess.*;
import model.UserData;
import model.AuthData;
import server.Server;

import java.rmi.ServerError;

public class UserService {
    UserDAO userDao;
    AuthDAO authDao;
    public UserService(UserDAO userDao, AuthDAO authDao) {
        this.userDao = userDao;
        this.authDao = authDao;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DuplicateUserException, BadRequestException, ServerErrorException {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException("Error: bad request");
        }
        try {
            UserData result = userDao.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
            AuthData authData = authDao.createAuth(result.username());
            return new RegisterResult(authData.username(), authData.authToken());
        } catch (DuplicateUserException e) {
           throw new DuplicateUserException(e.getMessage());
        } catch (Exception e) {
            throw new ServerErrorException(e.getMessage());
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws UnauthorizedRequestException, ServerErrorException {
        UserData user = userDao.getUser(loginRequest.username());

        if (user == null || !user.password().equals(loginRequest.password())) {
            throw new UnauthorizedRequestException("Error: unauthorized");
        } else {
            try {
                AuthData newAuthData = authDao.createAuth(loginRequest.username());
                return new LoginResult(loginRequest.username(), newAuthData.authToken());
            } catch (Exception e) {
                throw new ServerErrorException(e.getMessage());
            }

        }
    }

    public void logout(LogoutRequest logoutRequest) throws UnauthorizedRequestException {

        AuthData authData = authDao.getAuth(logoutRequest.authToken());
        if (authData == null) {
            throw new UnauthorizedRequestException("Error: unauthorized");
        } else {
            try {
                authDao.deleteAuth(authData);
            } catch (Exception e) {
                throw new ServerErrorException(e.getMessage());
            }

        }
    }
}
