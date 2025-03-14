package service;

import dataaccess.*;
import dataaccess.exceptions.ClearDataException;
import dataaccess.exceptions.DataAccessException;

public class ClearService {
    public static void clearDatabase(AuthDAO authDao, UserDAO userDao, GameDAO gameDao) throws DataAccessException, ClearDataException {
        if (authDao == null || userDao == null || gameDao == null) {
            throw new ClearDataException("ERROR: received null-valued input.");
        }
        authDao.clearData();
        userDao.clearData();
        gameDao.clearData();
    }
}
