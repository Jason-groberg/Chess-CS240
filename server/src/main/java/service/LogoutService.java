package service;
import dataaccess.*;


public class LogoutService {
    private final AuthDOA authDao;

    public LogoutService(){
        this.authDao = new AuthMemoryDOA();
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
