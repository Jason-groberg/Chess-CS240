package service;
import Requests.LoginRequest;
import Requests.RegisterRequest;
import Results.LoginResult;
import dataaccess.AuthDOA;
import dataaccess.AuthMemoryDOA;
import dataaccess.UserDOA;
import dataaccess.UserMemoryDOA;

public class LoginService {
    private final UserDOA userDoa;
    private final AuthDOA authDao;

    public LoginService() {
        this.authDao = new AuthMemoryDOA();
        this.userDoa = new UserMemoryDOA();
    }

    public LoginResult loginUser(LoginRequest request){

    }

}
