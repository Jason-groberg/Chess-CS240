package server;
import Parser.JsonDecoder;
import Requests.JoinGameRequest;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import io.javalin.http.Context;
import service.JoinGameService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JoinGameHandler {

    public static void serviceJoinGame(Context ctx){
        try{
            Collection<String> playerColors = List.of("black", "white");
            String authToken = JsonDecoder.getAuthToken(ctx);
            JoinGameRequest request = JsonDecoder.makeJoinRequest(ctx);
            if(request.gameID()==0 || !playerColors.contains(request.playerColor()) ){
                throw new BadRequestException("Error: bad request");
            }
            JoinGameService service = new JoinGameService();
            service.joinGame(authToken, request);
        }
        catch(Exception e){
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
        }
    }
}
