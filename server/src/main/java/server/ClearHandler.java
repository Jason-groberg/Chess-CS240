package server;
import io.javalin.http.Context;
import service.ClearService;
import Results.ClearResult;

public class ClearHandler {
        public static void serviceClear(Context ctx) {
            try {
                ClearService service = new ClearService();
                service.clear();

                ctx.status(200);
                ctx.json(new ClearResult());
            }
            catch (Exception e) {
                ctx.status(500);
                ctx.result("{ \"message\": \"Error: " + e.getMessage() + "\"}");
            }
        }
}
