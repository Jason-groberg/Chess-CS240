package Parser;
import Requests.*;
import com.google.gson.Gson;
import io.javalin.http.Context;

import java.util.Locale;

public class JsonDecoder {
    public static RegisterRequest makeRegisterRequest(Context ctx){
        return new Gson().fromJson(ctx.body(), RegisterRequest.class);
    }
    public static LoginRequest makeLoginRequest(Context ctx){
        return new Gson().fromJson(ctx.body(), LoginRequest.class);
    }
    public static AuthRequest makeAuthRequest(Context ctx){
        String token = ctx.header("Authorization");
        return new AuthRequest(token);
    }
    public static String getAuthToken(Context ctx){
        String token = ctx.header("Authorization");
        return token;
    }
    public static CreateGameRequest makeCreateGameRequest(Context ctx){
        String gameName = ctx.body();
        String token = ctx.header("Authorization");
        return new CreateGameRequest(token,gameName);
    }
    public static JoinGameRequest makeJoinRequest(Context ctx){
        return new Gson().fromJson(ctx.body(), JoinGameRequest.class);
    }
}
