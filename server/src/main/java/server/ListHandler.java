package server;
import Parser.JsonDecoder;
import Requests.AuthRequest;
import Results.ListResult;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.ListService;
import java.util.Collection;

public class ListHandler {

    public static void serviceList(Context ctx){
        try{
            AuthRequest request = JsonDecoder.makeAuthRequest(ctx);
            if(request.authToken() == null){
                throw new DataAccessException("Error: bad request");
            }
            ListService service = new ListService();
            Collection<ListResult> result = service.listGames(request);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
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
