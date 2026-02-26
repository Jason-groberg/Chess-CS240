package server;
import Parser.JsonDecoder;
import Requests.LoginRequest;
import Results.LoginResult;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import dataaccess.UserNotFoundExecption;
import io.javalin.http.Context;
import service.LoginService;

public class LoginHandler {

    public static void serviceLogin(Context ctx){
        try{
            LoginRequest request = JsonDecoder.makeLoginRequest(ctx);
            if(request.username() == null || request.password()==null){
                throw new BadRequestException("Error: bad request");
            }
            LoginService service = new LoginService();
            LoginResult result = service.loginUser(request);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        }
        catch(BadRequestException e){
            ctx.status(400);
            ctx.result(new Gson().toJson(e));
        }
        catch (UserNotFoundExecption e){
            ctx.status(401);
            ctx.result(new Gson().toJson(e));
        }
        catch(UnauthorizedException e){
            ctx.status(401);
            ctx.result(new Gson().toJson(e));
        }
        catch (Exception e){
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
        }
    }
}
