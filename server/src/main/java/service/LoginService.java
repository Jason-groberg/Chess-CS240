package service;
import org.mindrot.jbcrypt.BCrypt;
import requests.LoginRequest;
import results.LoginResult;
import dataaccess.*;
import model.AuthData;
import model.UserData;
import java.util.UUID;

public class LoginService {
    private final UserDOA userDoa;
    private final AuthDOA authDao;

    public LoginService() throws DataAccessException {
        this.authDao = new AuthSqlDao();
        this.userDoa = new UserSqlDao();
    }

    public String createAuthToken(){return UUID.randomUUID().toString();}

    public boolean verifyPassword(UserData user, String password) {
        return BCrypt.checkpw(password, user.password());
    }

    public LoginResult loginUser(LoginRequest request) throws Exception{
        try {
            UserData userData = userDoa.getUser(request.username());
            if(userData == null) {
                throw new UnauthorizedException("Error : Unauthorized");
            }
            if (!verifyPassword(userData, request.password())){
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
