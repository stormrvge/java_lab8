package sample.connection.server;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("You have to choose PORT, on which the server will work");
        }
        else {
            int port = Integer.parseInt(args[0]);
            Server server = new Server(port);
            server.process();
        }
    }
}
