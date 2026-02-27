package server;
import parser.JsonDecoder;
import requests.RegisterRequest;
import results.RegisterResult;
import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import io.javalin.http.Context;
import service.RegisterService;
import java.util.Map;

public class RegisterHandler {
    public static void serviceRegister(Context ctx){
        try{
            RegisterRequest request = JsonDecoder.makeRegisterRequest(ctx);
            if(request.username() == null || request.password() == null || request.email()== null){
                throw new BadRequestException("Error: bad request");
            }
            RegisterService service = new RegisterService();
            RegisterResult result = service.registerUser(request);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        }
        catch (AlreadyTakenException e){
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
        catch(BadRequestException e) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
        catch (Exception e){
            ctx.status(500);
            ctx.result(new Gson().toJson(Map.of("message", e.getMessage())));
        }
    }
}
