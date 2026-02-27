package server;

import io.javalin.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        createHandlers(javalin);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void createHandlers(Javalin javalinServer){
        javalinServer.delete("/db", ClearHandler::serviceClear);
        javalinServer.post("/user", RegisterHandler::serviceRegister);
        javalinServer.post("/session", LoginHandler::serviceLogin);
        javalinServer.delete("/session", LogoutHandler::serviceLogout);
        javalinServer.get("/game", ListHandler::serviceList);
        javalinServer.post("/game", CreateGameHandler::serviceCreateGame);
        javalinServer.put("/game", JoinGameHandler::serviceJoinGame);
    }

    public void stop() {
        javalin.stop();
    }
}
