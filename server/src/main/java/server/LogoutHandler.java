package server;
import Parser.JsonDecoder;
import com.google.gson.Gson;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import service.LogoutService;
import java.util.Map;

public class LogoutHandler {

    public static void serviceLogout(Context ctx){
        try{
            String authToken = JsonDecoder.getAuthToken(ctx);
            LogoutService service = new LogoutService();
            service.logoutUser(authToken);
            ctx.status(200);
            ctx.result("{}");
        }

        catch(UnauthorizedException e){
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
        catch(Exception e){
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }
}
