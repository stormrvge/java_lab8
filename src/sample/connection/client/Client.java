package sample.connection.client;

import sample.commands.*;
import sample.logic.Packet;
import sample.logic.User;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    private static SocketChannel channel;
    private SocketAddress addr;
    private final int port;
    private final String hostname;
    private static boolean close = false;

    private static Object answer;
    private static Boolean boolAnswer;
    private User user;

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    void run() {
        user = new User();

        try {
            try {
                addr = new InetSocketAddress(hostname, port);
                channel = SocketChannel.open(addr);
                channel.configureBlocking(false);

            } catch (SocketException e) {
                System.out.println("Cant connect to the connection.server. Server is down.");
                reconnect();
            }
        } catch (NullPointerException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    void handleRequest(Command command, String ... args) throws IOException, InterruptedException {
        if (command != null) {
            Packet packet = command.execOnClient(this, args);
            sendPacket(packet);
        }
    }

    void sendPacket(Packet packet) throws InterruptedException, IOException {
        if (packet != null) {
            byte[] message = serializeObject(packet);
            ByteBuffer wrap = ByteBuffer.wrap(message);

            try {
                channel.write(wrap);
                Thread.sleep(150);
            } catch (IOException | NullPointerException e) {
                reconnect();
                System.err.println(e.getMessage());
            }
            readMessage();
        }
    }

    private void readMessage() throws IOException {
        ByteBuffer msg = ByteBuffer.allocate(4096);
        msg.clear();

        if (channel.isConnected()) {
            channel.read(msg);

            if (msg.position() == 0) {
                System.out.println("Now connection.server is locked. Wait please...");
                try {
                    Thread.sleep(1000);
                    readMessage();
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            }

            try {
                Packet packet = (Packet) deserialize(msg.array());
                answer = packet.getArgument();
                boolAnswer = packet.getBoolAnswer();

                user.setLoginState(packet.isLogin());
                if (answer != null && user.getLoginState()) {
                    System.out.println(answer.toString());
                }
                else {
                    System.out.println("Null string");
                }
            } catch (ClassNotFoundException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    private static byte[] serializeObject(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream out  = new ObjectOutputStream(b);

        out.writeObject(obj);
        return  b.toByteArray();
    }

    private static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(bytes);
        ObjectInputStream objStream = new ObjectInputStream(byteArrayInput);

        return objStream.readObject();
    }

    private void reconnect() {
        System.out.println("Reconnecting...");
        try {
            for (int i = 0; i < 10; i++) {
                try {
                    channel = SocketChannel.open(addr);
                    run();
                    break;
                } catch (Exception e) {
                    System.err.println("No answer from connection.server, trying: " + (i + 1));
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void disconnect()  {
        close = true;
        try {
            channel.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.out.println("Client was closed...");
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

    public Object getAnswer() {
        return answer;
    }

    public Boolean getBoolAnswer() { return boolAnswer; }
}
