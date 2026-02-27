package server;
import parser.JsonDecoder;
import results.ListofListResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import service.ListService;
import java.util.Map;

public class ListHandler {

    public static void serviceList(Context ctx){
        try{
            String authToken = JsonDecoder.getAuthToken(ctx);
            if(authToken == null){
                throw new DataAccessException("Error: bad request");
            }
            ListService service = new ListService();
            ListofListResult result = service.listGames(authToken);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
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
