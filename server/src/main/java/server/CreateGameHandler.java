package server;
import Parser.JsonDecoder;
import Requests.CreateGameRequest;
import Results.CreateGameResult;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import service.CreateGameService;

import java.util.Map;

public class CreateGameHandler {
    public static void serviceCreateGame(Context ctx){
        try{
            CreateGameRequest request = JsonDecoder.makeCreateGameRequest(ctx);
            String authToken = JsonDecoder.getAuthToken(ctx);
            if(authToken == null || request.gameName()==null){
                throw new BadRequestException("Error: bad request");
            }
            CreateGameService service = new CreateGameService();
            CreateGameResult result = service.createGame(request, authToken);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        }

        catch(BadRequestException e){
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
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
