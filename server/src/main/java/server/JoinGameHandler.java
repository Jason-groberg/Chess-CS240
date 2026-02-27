package server;
import Parser.JsonDecoder;
import Requests.JoinGameRequest;
import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.GameNotFoundException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import service.JoinGameService;
import java.util.Map;

public class JoinGameHandler {

    public static void serviceJoinGame(Context ctx){
        try{
            String authToken = JsonDecoder.getAuthToken(ctx);
            JoinGameRequest request = JsonDecoder.makeJoinRequest(ctx);
            if(request.playerColor()==null||request.gameID()==0){
                throw new BadRequestException("Error: bad request");
            }
            JoinGameService service = new JoinGameService();
            service.joinGame(authToken, request);
            ctx.status(200);
            ctx.result("{}");
        }
        catch(BadRequestException e){
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
        catch(UnauthorizedException e){
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
        catch(AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
        catch(GameNotFoundException e){
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
        catch(Exception e){
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }
}
