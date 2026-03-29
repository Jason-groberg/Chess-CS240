package server.websocket;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.Notification;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConnectionManager {
    public final Map<Integer, Session> connections = new HashMap<>();

    public void add(Integer gameID, Session session) {
        connections.put(gameID, session);
    }

    public void remove(Integer gameID) {
        connections.remove(gameID);
    }

    public void broadcast(Session excludeSession, Notification notification) throws IOException {
        String msg = notification.toString();
        for (Session c : connections.values()) {
            if (c.isOpen()) {
                if (!c.equals(excludeSession)) {
                    c.getRemote().sendString(msg);
                }
            }
        }
    }
}