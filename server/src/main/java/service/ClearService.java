package service;
import dataaccess.*;

public class ClearService {
    private final UserDOA userDao;
    private final AuthDOA authDao ;
    private final GameDOA gameDao;

    public ClearService() throws DataAccessException{
        this.userDao = new UserSqlDao();
        this.authDao = new AuthSqlDao();
        this.gameDao = new GameSqlDao();
    }

    public void clear() throws Exception {
        try {
            userDao.clear();
            authDao.clear();
            gameDao.clear();
        }
        catch (DataAccessException e) {
            throw new Exception();
        }
    }
}
