package sample.connection.client.threads;

import sample.connection.client.Client;
import sample.connection.client.ProgramMainWindow;
import sample.logic.Packet;
import sample.logic.User;
import sample.logic.collectionClasses.Route;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class MessageReader extends Thread {
    private final SocketChannel channel;
    private Client client;

    private final ByteBuffer msg;
    private Object ans;

    private Object answer;
    private Boolean boolAnswer;
    private  Boolean sync;
    private final User user;

    public MessageReader(Client client, SocketChannel channel, User user) {
        this.client = client;
        this.channel = channel;
        this.user = user;
        msg = ByteBuffer.allocate(4096);
    }

    @Override
    public void run() {
        try {
            readMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readMessage() throws IOException {
        if (channel.isConnected()) {
            while (true) {
                try {
                    msg.clear();
                    Thread.sleep(100);

                    channel.read(msg);
                    if (msg.position() != 0) {       //position?
                        Packet packet = (Packet) deserialize(msg.array());
                        answer = packet.getArgument();
                        boolAnswer = packet.getBoolAnswer();
                        sync = packet.getSync();

                        client.getUser().setLoginState(packet.isLogin());

                        if (sync) {
                            ans = packet.getArgument();
                            ProgramMainWindow.setTable((ArrayList<Route>) ans);
                        } else {
                            client.getAtomicPacket().set(packet);
                        }
                    }
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInput = new ByteArrayInputStream(bytes);
        ObjectInputStream objStream = new ObjectInputStream(byteArrayInput);

        return objStream.readObject();
    }

    public void setAnswer(Packet answer) {
        this.answer = answer;
    }

    public Object getAnswer() { return answer; }
    public Boolean getBoolAnswer() { return boolAnswer; }
    public Boolean getSyncBoolean() { return sync; }
    public ByteBuffer getMsg() { return msg; }
}
