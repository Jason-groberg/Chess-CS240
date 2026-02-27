package server;
import Parser.JsonDecoder;
import Requests.CreateGameRequest;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import service.CreateGameService;

public class CreateGameHandler {
    public static void serviceCreateGame(Context ctx){
        try{
            CreateGameRequest request = JsonDecoder.makeCreateGameRequest(ctx);
            if(request.authToken()==null||request.gameName()==null){
                throw new BadRequestException("Error: bad request");
            }
            CreateGameService service = new CreateGameService();
            service.createGame(request);
        }

        catch(BadRequestException e){
            ctx.status(400);
            ctx.result(new Gson().toJson(e));
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
