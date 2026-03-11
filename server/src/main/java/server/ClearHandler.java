package server;
import com.google.gson.Gson;
import io.javalin.http.Context;
import service.ClearService;

import java.util.Map;


public class ClearHandler {
        public static void serviceClear(Context ctx) {
            try {
                ClearService service = new ClearService();
                service.clear();
                ctx.status(200);
                ctx.result("{}");
            }
            catch (Exception e) {
                ctx.status(500);
                ctx.result(new Gson().toJson(Map.of("message","Error" + e.getMessage())));
            }
        }
}
