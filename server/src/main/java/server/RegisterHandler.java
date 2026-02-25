package server;
import Parser.JsonDecoder;
import Requests.RegisterRequest;
import Results.RegisterResult;
import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import service.RegisterService;

public class RegisterHandler {
    public static void serviceRegister(Context ctx){
        try{
            RegisterRequest request = JsonDecoder.makeRegisterRequest(ctx);
            if(request.username() == null || request.email() == null || request.password()==null){
                throw new DataAccessException("Error: bad request");
            }
            RegisterService service = new RegisterService();
            RegisterResult result = service.registerUser(request);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        }
        catch (AlreadyTakenException e){
            ctx.status(403);
            ctx.result(new Gson().toJson(e));
        }
        catch(DataAccessException e) {
            ctx.status(400);
            ctx.result(new Gson().toJson(e));
        }
        catch (Exception e){
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
        }
    }
}
