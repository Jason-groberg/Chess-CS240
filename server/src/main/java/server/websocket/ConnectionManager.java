package server.websocket;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {

    public final Map<Integer, Map<String, Connection>> connectionsMap = new ConcurrentHashMap<>();

    public void add(Integer gameID,String username,  Session session) {
        connectionsMap.putIfAbsent(gameID, new ConcurrentHashMap<>());
        connectionsMap.get(gameID).put(username, new Connection(username,session));
    }

    public void remove(Integer gameID, String username) {
        Map<String, Connection> gameConnections = connectionsMap.get(gameID);
        if(gameConnections != null) {
            gameConnections.remove(username);
            if(gameConnections.isEmpty()){
                connectionsMap.remove(gameID);
            }
        }
    }

    public void broadcast(Integer gameID, String excludeUser, ServerMessage notification) throws IOException {
        Map<String, Connection> gameConnections = connectionsMap.get(gameID);
        if(gameConnections!=null){
            String message = new Gson().toJson(notification);
            for(var conn : gameConnections.values()){
                if(conn.session().isOpen()){
                    if(excludeUser==null || !conn.username().equals(excludeUser)){
                        conn.session().getRemote().sendString(message);
                    }
                }
            }
        }
    }
}