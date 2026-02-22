package server;

import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        createHandlers(javalin);
        // Register your endpoints and exception handlers here.
        createHandlers(javalin);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void createHandlers(Javalin javalinServer){
        javalinServer.delete("/db", ClearHandler::serviceClear);
        javlinServer.post("/user", RegisterHandler::serviceRegister);

    }

    public void stop() {
        javalin.stop();
    }
}
