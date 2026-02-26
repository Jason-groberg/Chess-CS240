package service;
import Requests.RegisterRequest;
import Results.RegisterResult;
import dataaccess.*;
import model.AuthData;
import model.UserData;

public class RegisterService {

    private final UserDOA userDOA;
    private final AuthDOA authDOA;

    public RegisterService(){
        this.userDOA = new UserMemoryDOA();
        this.authDOA = new AuthMemoryDOA();
    }

    public RegisterResult registerUser(RegisterRequest request) throws Exception {
        try{
             if(userDOA.getUser(request.username()) != null){
                throw new AlreadyTakenException("Error: already taken");
             }
             UserData newUser = new UserData(request.username(), request.password(), request.email());
             userDOA.insertUser(newUser);
             String authToken = authDOA.createAuth();
             AuthData newAuthData = new AuthData(authToken, request.username());
             authDOA.insertAuth(newAuthData);
             return new RegisterResult(authToken, request.username());
        }
        catch(DataAccessException e){
            throw new Exception();
        }
    }
}
