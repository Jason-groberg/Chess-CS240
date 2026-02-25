package server;
import Parser.JsonDecoder;
import Requests.LoginRequest;
import Results.LoginResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.LoginService;

public class LoginHandler {

    public static void serviceLogin(Context ctx){
        try{
            LoginRequest request = JsonDecoder.makeLoginRequest(ctx);
            if(request.username() == null || request.password()==null){
                throw new DataAccessException("Error: bad request");
            }
            LoginService service = new LoginService();
            LoginResult result = service.loginUser(request);
        }
        catch (Exception e){
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
        }
    }
}
