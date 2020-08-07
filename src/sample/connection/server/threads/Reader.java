package sample.connection.server.threads;

import sample.commands.Command;
import sample.commands.RefreshTableCmd;
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
    private ExecutorService senderExecutor;
    private ExecutorService handlerExecutor;
    private Handler handler;
    private ObjectOutputStream out;

    private Packet packet;
    private User user;
    private Boolean isLogin;

    public Reader(Server server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println("Server accepted client number: " + server.getNumOfClients());

        if (clientSocket != null) {
            try {
                senderExecutor = Executors.newCachedThreadPool();
                handlerExecutor = Executors.newFixedThreadPool(10);

                while (true) {
                    packet = readMessage();
                    Command commandServer = packet.getCommand();
                    user = packet.getUser();
                    isLogin = server.getSqlStatements().login(user);

                    if (commandServer != null &&
                            (isLogin || !commandServer.getRequireLogin())) {
                        handler = new Handler(packet, server);
                        Future<Packet> result = handlerExecutor.submit(handler);

                        Packet answerToClient = result.get();

                        out = new ObjectOutputStream(clientSocket.getOutputStream());
                        Sender sender = new Sender(answerToClient, out, isLogin);
                        senderExecutor.submit(sender);


                        if (commandServer.isNeedSync()) {
                            server.sync();
                        }
                        /*

                         */

                    } else {
                        Sender sender = new Sender(null, out, isLogin);
                        senderExecutor.submit(sender);
                    }
                }
            } catch (NullPointerException | IOException e) {
                System.out.println("Client " + server.getNumOfClients() + " was disconnected!");
                server.closeConnection(clientSocket);
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found: " + e.getMessage());
                server.closeConnection(clientSocket);
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


    public void sync() throws InterruptedException, ExecutionException {
        Packet syncPacket = new Packet(new RefreshTableCmd(), "SYNC CMD", user);

        handler = new Handler(syncPacket, server);
        Future<Packet> result = handlerExecutor.submit(handler);

        Packet answerToClient = result.get();

        Sender sender = new Sender(answerToClient, out, isLogin);
        senderExecutor.submit(sender);
    }
}
