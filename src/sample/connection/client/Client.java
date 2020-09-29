package sample.connection.client;

import sample.commands.*;
import sample.connection.client.threads.MessageReader;
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
    private User user;

    private MessageReader messageReader;
    private Packet packet;
    private Packet ansPacket;
    private final AtomicReference<Packet> atomicPacket = new AtomicReference<>();

    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public void run() {
        user = new User();

        try {
            try {
                addr = new InetSocketAddress(hostname, port);
                channel = SocketChannel.open(addr);
                channel.configureBlocking(false);

            } catch (SocketException e) {
                System.out.println("Cant connect to the server. Server is down.");
                reconnect();
            }
        } catch (NullPointerException | IOException e) {
            System.out.println(e.getMessage());
        }

        messageReader = new MessageReader(this, channel);
        messageReader.start();
    }

    public void handleRequest(Command command, Object ... args) throws IOException, InterruptedException {
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
        }
    }

    private static byte[] serializeObject(Object obj) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream out  = new ObjectOutputStream(b);

        out.writeObject(obj);
        return  b.toByteArray();
    }



    public void reconnect() {
        System.out.println("Reconnecting...");
        try {
            for (int i = 0; i < 10; i++) {
                try {
                    channel = SocketChannel.open(addr);
                    run();
                    break;
                } catch (Exception e) {
                    System.err.println("No answer from server, trying: " + (i + 1));
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
        }
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











