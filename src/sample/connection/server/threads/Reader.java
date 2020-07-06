package sample.connection.server.threads;

import sample.commands.Command;
import sample.connection.server.Server;
import sample.logic.Packet;
import sample.logic.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Reader extends Thread {
    private final Server server;
    private final Socket clientSocket;
    private ObjectOutputStream out;

    public Reader(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        server.addNumOfClients();
        System.out.println("Server accepted connection.client number: " + server.getNumOfClients());

        if (clientSocket != null) {
            try {
                ExecutorService senderExecutor = Executors.newCachedThreadPool();
                ExecutorService handlerExecutor = Executors.newFixedThreadPool(1);

                while (true) {
                    Packet packet = readMessage();
                    Command commandServer = packet.getCommand();
                    User user = packet.getUser();
                    Boolean isLogin = server.getSqlStatements().login(user);

                    if (commandServer != null &&
                            (isLogin || !commandServer.getRequireLogin())) {
                        Handler handler = new Handler(packet, server);
                        Future<Packet> result = handlerExecutor.submit(handler);

                        Packet answerToClient = result.get();

                        out = new ObjectOutputStream(clientSocket.getOutputStream());
                        Sender sender = new Sender(answerToClient, out, isLogin);
                        senderExecutor.submit(sender);
                    } else {
                        Sender sender = new Sender(null, out, isLogin);
                        senderExecutor.submit(sender);
                    }
                }
            } catch (NullPointerException | IOException e) {
                System.out.println("Client " + server.getNumOfClients() + " was disconnected!");
                Server.closeConnection();
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found: " + e.getMessage());
                Server.closeConnection();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private Packet readMessage() throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
        return (Packet) objectInputStream.readObject();
    }
}
