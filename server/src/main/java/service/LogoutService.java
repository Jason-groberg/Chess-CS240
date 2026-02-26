package service;
import Requests.LogoutRequest;
import dataaccess.*;
import model.AuthData;

public class LogoutService {
    private final UserDOA userDao;
    private final AuthDOA authDao;

    public LogoutService(){
        this.userDao = new UserMemoryDOA();
        this.authDao = new AuthMemoryDOA();
    }

    public void logoutUser(LogoutRequest request) throws DataAccessException {
        AuthData authData = authDao.getAuth(request.authToken());
        if(authData.authToken()!= request.authToken()){
            throw new DataAccessException("Error: unauthorized");
        }
        authDao.deleteAuth(request.authToken());
    }


}
