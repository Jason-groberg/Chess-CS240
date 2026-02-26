package Parser;
import Requests.CreateGameRequest;
import Requests.LoginRequest;
import Requests.AuthRequest;
import com.google.gson.Gson;
import io.javalin.http.Context;
import Requests.RegisterRequest;

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
    public static CreateGameRequest makeCreateGameRequest(Context ctx){
        String gameName = ctx.body();
        String token = ctx.header("Authorization");
        return new CreateGameRequest(token,gameName);
    }
}
