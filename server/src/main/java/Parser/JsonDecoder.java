package Parser;
import Requests.*;
import chess.ChessGame;
import com.google.gson.Gson;
import io.javalin.http.Context;



public class JsonDecoder {

    private static Gson serializer = new Gson();

    public static RegisterRequest makeRegisterRequest(Context ctx){
        return serializer.fromJson(ctx.body(), RegisterRequest.class);
    }
    public static LoginRequest makeLoginRequest(Context ctx){
        return serializer.fromJson(ctx.body(), LoginRequest.class);
    }

    public static String getAuthToken(Context ctx){
        String token = ctx.header("Authorization");
        return token;
    }
    public static CreateGameRequest makeCreateGameRequest(Context ctx){
        record gameBody(String gameName){}
        gameBody body = serializer.fromJson(ctx.body(), gameBody.class);
        String token = ctx.header("Authorization");
        return new CreateGameRequest(token, body.gameName());
    }
    public static JoinGameRequest makeJoinRequest(Context ctx){
        record joinBody(String playerColor, int gameID){}
        joinBody body = serializer.fromJson(ctx.body(), joinBody.class);
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
