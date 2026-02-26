package server;
import Parser.JsonDecoder;
import Requests.AuthRequest;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import service.LogoutService;


public class LogoutHandler {

    public static void serviceLogout(Context ctx){
        try{
            AuthRequest request = JsonDecoder.makeAuthRequest(ctx);
            LogoutService service = new LogoutService();
            service.logoutUser(request);
            ctx.status(200);
            ctx.result("{}");

        }

        catch(UnauthorizedException e){
            ctx.status(401);
            ctx.result(new Gson().toJson(e));
        }
        catch(Exception e){
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
        }
    }
}
