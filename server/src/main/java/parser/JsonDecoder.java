package parser;
import requests.*;
import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.http.Context;

public class JsonDecoder {

    private final static Gson serializer = new Gson();

    public static RegisterRequest makeRegisterRequest(Context ctx){
        return serializer.fromJson(ctx.body(), RegisterRequest.class);
    }
    public static LoginRequest makeLoginRequest(Context ctx){
        return serializer.fromJson(ctx.body(), LoginRequest.class);
    }

    public static String getAuthToken(Context ctx){
        return ctx.header("Authorization");

    }

    public static CreateGameRequest makeCreateGameRequest(Context ctx){
        return serializer.fromJson(ctx.body(), CreateGameRequest.class);
    }

    public static JoinGameRequest makeJoinRequest(Context ctx){
        record JoinBody(String playerColor, int gameID){}
        JoinBody body = serializer.fromJson(ctx.body(), JoinBody.class);
        if(body.playerColor()!= null && body.playerColor().equalsIgnoreCase("WHITE")){
            return new JoinGameRequest(ChessGame.TeamColor.WHITE, body.gameID());
        }
        else if(body.playerColor()!=null && body.playerColor().equalsIgnoreCase("BLACK")){
            return new JoinGameRequest(ChessGame.TeamColor.BLACK, body.gameID());
        }
        else{
            return new JoinGameRequest(null, body.gameID());
        }
    }
}
