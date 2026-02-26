package server;
import Parser.JsonDecoder;
import Requests.LoginRequest;
import Requests.LogoutRequest;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UserNotFoundExecption;
import io.javalin.http.Context;
import service.LoginService;
import service.LogoutService;


public class LogoutHandler {

    public static void serviceLogout(Context ctx){
        try{
            LogoutRequest request = JsonDecoder.makeLogoutRequest(ctx);
            LogoutService service = new LogoutService();
            service.logoutUser(request);
            ctx.status(200);
            ctx.result("{}");

        }

        catch(DataAccessException e){
            ctx.status(401);
            ctx.result(new Gson().toJson(e));
        }
        catch(Exception e){
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
        }
    }
}
