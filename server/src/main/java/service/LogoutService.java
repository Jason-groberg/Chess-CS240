package service;
import dataaccess.*;


public class LogoutService {
    private final AuthDOA authDao;

    public LogoutService()throws DataAccessException{
        this.authDao = new AuthSqlDao();
    }

    public void logoutUser(String authToken) throws Exception {
        try {
            if(!authDao.isAuthorized(authToken)){
                throw new UnauthorizedException("Error: unauthorized");
            }
            authDao.deleteAuth(authToken);
        }
        catch(DataAccessException e){
            throw new Exception();
        }
    }
}
