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

    public boolean verifyPassword(String userName, String password) throws Exception{
        String hashedPassword = userDoa.getUser(userName).password();
        return BCrypt.checkpw(password, hashedPassword);
    }

    public LoginResult loginUser(LoginRequest request) throws Exception{
        try {
            if (userDoa.getUser(request.username()) == null) {
                throw new UserNotFoundExecption("Error: username not found");
            }
            UserData userData = userDoa.getUser(request.username());
            if (!verifyPassword(request.username(), request.password())) {
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
