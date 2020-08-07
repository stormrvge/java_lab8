package sample.connection.client;

import sample.commands.*;
import sample.connection.client.threads.MessageReader;
import sample.connection.client.threads.SyncThread;
import sample.logic.Packet;
import sample.logic.User;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicReference;

public class Client {
    private SocketChannel channel;
    private SocketAddress addr;
    private final int port;
    private final String hostname;
    private static boolean close = false;
    private Packet packet;

    private MessageReader messageReader;
    private SyncThread syncThread;
    private ByteBuffer msg;

    private static Object answer;
    private static Boolean boolAnswer;
    private static Boolean sync;
    private User user;

    public Packet ansPacket;
    private final AtomicReference<Packet> atomicPacket = new AtomicReference<>();

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
                channel.configureBlocking(false);    //false

            } catch (SocketException e) {
                System.out.println("Cant connect to the connection.server. Server is down.");
                reconnect();
            }
        } catch (NullPointerException | IOException e) {
            System.out.println(e.getMessage());
        }

        messageReader = new MessageReader(this, channel, user);
        messageReader.start();
    }

    void handleRequest(Command command, Object ... args) throws IOException, InterruptedException {
        if (command != null) {
            packet = command.execOnClient(this, args);
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

            for(int i = 0; i < 50; i++) {
                if (atomicPacket.get() != null) {
                    ansPacket = atomicPacket.get();
                    atomicPacket.set(null);
                }
            }
            //messageReader.readMessage();
        }
    }

    private static byte[] serializeObject(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream out  = new ObjectOutputStream(b);

        out.writeObject(obj);
        return  b.toByteArray();
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

    public void disconnect()  {
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

    public String getCommandName() {
        return packet.getCommand().getName();
    }

    public MessageReader getMessageReader() {
        return messageReader;
    }

    public Packet getAnsPacket() {
        return ansPacket;
    }

    public AtomicReference<Packet> getAtomicPacket() {
        return atomicPacket;
    }
}






/*
    new Thread(() -> {
        msg = ByteBuffer.allocate(4096);
        while (true) {
            try {
                Thread.sleep(100);
                channel.read(msg);
                if (msg.position() > 0) {       //position?
                    Packet packet = (Packet) deserialize(msg.array());
                    if (packet.getSync()) {
                        ans = packet.getArgument();
                        ProgramMainWindow.setTable((ArrayList<Route>) ans);
                    } else {
                        user.setLoginState(packet.isLogin());       //?????????

                        atomicPacket.set(packet);
                    }
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }).start();
     */













