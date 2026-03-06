package service;
import requests.LoginRequest;
import results.LoginResult;
import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;


public class LoginService {
    private final UserDOA userDoa;
    private final AuthDOA authDao;

    public LoginService() {
        this.authDao = new AuthMemoryDOA();
        this.userDoa = new UserMemoryDOA();
    }

    public String createAuthToken(){return UUID.randomUUID().toString();}

    public LoginResult loginUser(LoginRequest request) throws Exception{
        try {
            if (userDoa.getUser(request.username()) == null) {
                throw new UserNotFoundExecption("Error: username not found");
            }
            UserData userData = userDoa.getUser(request.username());
            if (!userData.password().equalsIgnoreCase(request.password())) {
                throw new UnauthorizedException("Error: unauthorized, password incorrect");
            }
            String authToken = createAuthToken();
            AuthData authData = new AuthData(authToken, request.username());
            authDao.insertAuth(authData);
            return new LoginResult(request.username(), authToken);
        }
        catch(DataAccessException e){
            throw new Exception(e);
        }
    }
}
