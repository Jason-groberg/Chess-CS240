package server.websocket;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections;
    private final AuthDOA authDao;
    private final GameDOA gameDao;

    public WebSocketHandler() {
        try {
            connections = new ConnectionManager();
            authDao = new AuthSqlDao();
            gameDao = new GameSqlDao();
        }catch(DataAccessException e){
            throw new RuntimeException("Error: Could not initialize or connect to Database");
        }
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            String authToken = command.getAuthToken();
            String username = authDao.getAuth(authToken).userName();
            if(username == null){
                throw new UnauthorizedException("Error : Unauthorized");
            }

            Session session = ctx.session;

            switch (command.getCommandType()) {
                case CONNECT -> connect(command, username, session);
                case  MAKE_MOVE -> makeMove(command, username, session);
                case LEAVE -> leaveGame(command, username, session);
                case RESIGN -> resign(command, username, session);
            }

        }catch(DataAccessException e){
            sendError("Error: unauthorized", ctx.session);
        }
        catch (IOException ex) {
            sendError("Error: " + ex.getMessage(), ctx.session);
        }
        catch(Exception e){
            sendError("Error: " + e.getMessage(), ctx.session);
        }
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void sendError(String message, Session session) throws IOException{
        if(message==null || !message.toUpperCase().contains("ERROR")){
            message = "unexpected error";
        }

        ErrorMessage error = new ErrorMessage(message);
        String errorMessage = new Gson().toJson(error);
        session.getRemote().sendString(errorMessage);
    }


    private void connect(UserGameCommand command, String username, Session session) throws IOException {
        try{
            int gameID = command.getGameID();
            GameData game = gameDao.getGame(gameID);
            if(game==null){
                sendError("Error: no game found with given ID:  " + gameID + ".", session);
                return;
            }
            connections.add(gameID, username, session);

            LoadGameMessage loadGame = new LoadGameMessage(game.game());
            String loadGameString = new Gson().toJson(loadGame);
            session.getRemote().sendString(loadGameString);

            String notification;
            if(username.equals(game.whiteUsername())){
                notification = String.format("%s joined game as white", username);
            }
            else if(username.equals(game.blackUsername())){
                notification = String.format("%s joined game as black", username);
            }
            else{
                notification = String.format("%s joined game as an observer", username);
            }

            Notification notify = new Notification(notification);
            connections.broadcast(gameID, username, notify);

        }catch(DataAccessException e){
            sendError("Error: " + e.getMessage(), session);

        }catch(Exception e ){
            sendError("Error: " + e.getMessage(), session);
        }
    }

    private void leaveGame(UserGameCommand command, String username, Session session) throws IOException {
//        var message = String.format("%s left the shop", visitorName);
//        var notification = new Notification(Notification.Type.DEPARTURE, message);
//        connections.broadcast(session, notification);
//        connections.remove(session);
    }

    private void makeMove(UserGameCommand command, String username, Session session){

    }

    private void resign(UserGameCommand command, String username, Session session){

    }

//    public void makeNoise(String petName, String sound) throws ResponseException {
//        try {
//            var message = String.format("%s says %s", petName, sound);
//            var notification = new Notification(Notification.Type.NOISE, message);
//            connections.broadcast(null, notification);
//        } catch (Exception ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
//        }
//    }
}