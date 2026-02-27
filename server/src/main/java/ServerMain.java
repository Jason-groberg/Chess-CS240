import server.Server;

public class ServerMain {
    public static void main(String[] args) {
        int requestedPort = 8080;
        Server server = new Server();
        server.run(requestedPort);
        System.out.println("CS 240 Chess Server is running of port" + requestedPort);
    }
}
