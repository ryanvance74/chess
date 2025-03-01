package service;

import dataaccess.*;

public class ClearService {
    public static void clearDatabase(AuthDAO authDao, UserDAO userDao, GameDAO gameDao) throws ClearDataException {
        if (authDao == null || userDao == null || gameDao == null) {
            throw new ClearDataException("ERROR: received null-valued input.");
        }
        authDao.clearData();
        userDao.clearData();
        gameDao.clearData();
    }
}
