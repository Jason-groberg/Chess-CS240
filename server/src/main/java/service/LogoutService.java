package service;
import Requests.AuthRequest;
import dataaccess.*;
import model.AuthData;

public class LogoutService {
    private final AuthDOA authDao;

    public LogoutService(){
        this.authDao = new AuthMemoryDOA();
    }

    public void logoutUser(AuthRequest request) throws Exception {
        try {
            if(!authDao.isAuthorized(request.authToken())){
                throw new UnauthorizedException("Error: unauthorized");
            }
            authDao.deleteAuth(request.authToken());
        }
        catch(DataAccessException e){
            throw new Exception();
        }
    }
}
