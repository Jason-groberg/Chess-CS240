package service;
import dataaccess.*;

public class ClearService {
    private final UserDOA userDao;
    private final AuthDOA authDao ;
    private final GameDOA gameDao;

    public ClearService(){
        this.userDao = new UserMemoryDOA();
        this.authDao = new AuthMemoryDOA();
        this.gameDao = new GameMemoryDAO();
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
