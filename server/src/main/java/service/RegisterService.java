package service;
import requests.RegisterRequest;
import results.RegisterResult;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class RegisterService {

    private final UserDOA userDOA;
    private final AuthDOA authDOA;

    public RegisterService() throws DataAccessException{
        this.userDOA = new UserSqlDao();
        this.authDOA = new AuthSqlDao();
    }

    public String createAuthToken(){return UUID.randomUUID().toString();}

    public RegisterResult registerUser(RegisterRequest request) throws Exception {
        try{
             if(userDOA.containsUser(request.username())){
                throw new AlreadyTakenException("Error: already taken");
             }
             UserData newUser = new UserData(request.username(), request.password(), request.email());
             userDOA.insertUser(newUser);
             String authToken = createAuthToken();
             AuthData newAuthData = new AuthData(authToken, request.username());
             authDOA.insertAuth(newAuthData);
             return new RegisterResult(authToken, request.username());
        }
        catch(DataAccessException e){
            throw new Exception();
        }
    }
}
