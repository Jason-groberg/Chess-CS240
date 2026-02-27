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

    public LoginResult loginUser(LoginRequest request) throws Exception{
        try {
            if (userDoa.getUser(request.username()) == null) {
                throw new UserNotFoundExecption("Error: username not found");
            }
            UserData userData = userDoa.getUser(request.username());
            if (!userData.password().equalsIgnoreCase(request.password())) {
                throw new UnauthorizedException("Error: unauthorized, password incorrect");
            }
            String authToken = authDao.createAuth();
            AuthData authData = new AuthData(authToken, request.username());
            authDao.insertAuth(authData);
            return new LoginResult(request.username(), authToken);
        }
        catch(DataAccessException e){
            throw new Exception(e);
        }
    }
}
