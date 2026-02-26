package Parser;
import Requests.LoginRequest;
import Requests.LogoutRequest;
import io.javalin.http.Context;
import Requests.RegisterRequest;

public class JsonDecoder {
    public static RegisterRequest makeRegisterRequest(Context ctx){
        return ctx.bodyAsClass(RegisterRequest.class);
    }
    public static LoginRequest makeLoginRequest(Context ctx){
        return ctx.bodyAsClass(LoginRequest.class);
    }
    public static LogoutRequest makeLogoutRequest(Context ctx){
        String token = ctx.header("Authorization");
        return new LogoutRequest(token);
    }
}
