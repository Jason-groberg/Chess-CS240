package server;
import Requests.RegisterRequest;
import io.javalin.http.Context;
import service.RegisterService;


public class RegisterHandler {
    public static void serviceRegister(Context ctx){
        try{
            RegisterRequest request = new RegisterRequest(ctx);

            RegisterService service = new RegisterService();

            service.registerUser(request);


        }
    }

}
