package service;
import Requests.LoginRequest;
import Results.LoginResult;
import dataaccess.*;
import model.AuthData;
import model.UserData;

public class LoginService {
    private final UserDOA userDoa;
    private final AuthDOA authDao;

    public LoginService() {
        this.authDao = new AuthMemoryDOA();
        this.userDoa = new UserMemoryDOA();
    }

    public LoginResult loginUser(LoginRequest request) throws DataAccessException{
        if(userDoa.getUser(request.username()) == null){
            throw new UserNotFoundExecption("Error: username not found");
        }
        UserData userData = userDoa.getUser(request.username());
        if(!userData.password().equalsIgnoreCase(request.password())){
            throw new DataAccessException("Error: unauthorized, password incorrect");
        }
        String authToken = authDao.createAuth();
        AuthData authData = new AuthData(request.username(), authToken);
        authDao.insertAuth(authData);
        return new LoginResult(request.username(), authToken);
    }
}
