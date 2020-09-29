package sample.connection.client.threads;

import sample.connection.client.Client;
import sample.logic.Packet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.ReentrantLock;

public class MessageReader extends Thread {
    private final SocketChannel channel;
    private final Client client;

    private final ReentrantLock lock;

    private final ByteBuffer msg;

    private Packet packet;
    private Object answer;
    private Boolean boolAnswer;
    private static Boolean syncAnswer;

    public MessageReader(Client client, SocketChannel channel) {
        this.client = client;
        this.channel = channel;
        syncAnswer = true;

        lock = new ReentrantLock();

        msg = ByteBuffer.allocate(8192);
    }

    @Override
    public void run() {
        try {
            readMessage();
        } catch (StreamCorruptedException e) {
            client.reconnect();
        }

    }


    public void readMessage() throws StreamCorruptedException {
        if (channel.isConnected()) {
            while (true) {
                try {
                    lock.lock();
                    msg.clear();
                    Thread.sleep(100);

                    channel.read(msg);
                    if (msg.position() != 0) {       //position?
                        packet = (Packet) deserialize(msg.array());
                        answer = packet.getArgument();
                        boolAnswer = packet.getBoolAnswer();
                        syncAnswer = packet.getSync();


                        System.out.println("ACCEPT PACKET");
                        client.getUser().setLoginState(packet.isLogin());

                        if (syncAnswer) {
                            SyncCheckerThread.NEED = true;

                            System.out.println("SYNC ANS");
                        } else {
                            client.getAtomicPacket().set(packet);
                        }
                    }
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(bytes);
        ObjectInputStream objStream = new ObjectInputStream(byteArrayInput);

        return objStream.readObject();
    }

    public Packet getPacket() {
        return packet;
    }

    public Object getAnswer() {
        return answer;
    }

    public Boolean getBoolAnswer() {
        return boolAnswer;
    }

    public Boolean getSyncAnswer() {
        return syncAnswer;
    }

    public static void setSyncAnswer(Boolean sync) {
        syncAnswer = sync;
    }

}
