package Parser;
import Requests.LoginRequest;
import io.javalin.http.Context;
import Requests.RegisterRequest;

public class JsonDecoder {
    public static RegisterRequest makeRegisterRequest(Context ctx){
        return ctx.bodyAsClass(RegisterRequest.class);
    }
    public static LoginRequest makeLoginRequest(Context ctx){
        return ctx.bodyAsClass(LoginRequest.class);
    }
}
