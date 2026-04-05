package websocketFacade;
import com.google.gson.Gson;
import jakarta.websocket.*;
import ui.ResponseException;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//need to extend Endpoint for websocket to work properly
public class WebsocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebsocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

                    switch(serverMessage.getServerMessageType()){
                        case LOAD_GAME -> {
                            LoadGameMessage loadMessage = new Gson().fromJson(message, LoadGameMessage.class);
                            notificationHandler.updateGame(loadMessage.getGame());
                        }
                        case ERROR -> {
                            ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                            notificationHandler.printError(errorMessage.getMessage());
                        }
                        case NOTIFICATION -> {
                            NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
                            notificationHandler.notify(notificationMessage.getNotification());
                        }
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, Integer gameID) throws ResponseException{
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        sendCommand(command);
    }

    public void leaveGame(String authToken, Integer gameID) throws ResponseException{
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        sendCommand(command);
    }


    public void sendCommand(Object command) throws ResponseException{
        try {
            session.getBasicRemote().sendText(new Gson().toJson(command));
        }catch(IOException e){
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }
}