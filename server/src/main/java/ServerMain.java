import server.Server;

public class ServerMain {
    public static void main(String[] args) {
        int requestedPort = Integer.parseInt(args[0]);

        Server server = new Server();
        server.run(requestedPort);


        System.out.println("CS 240 Chess Server is running of port" + requestedPort);
    }
}
