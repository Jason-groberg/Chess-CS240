package Parser;
import io.javalin.http.Context;
import Requests.RegisterRequest;

public class JsonDecoder {

    public static RegisterRequest makeRegisterRequest(Context ctx){
        return ctx.bodyAsClass(RegisterRequest.class);
    }
}
