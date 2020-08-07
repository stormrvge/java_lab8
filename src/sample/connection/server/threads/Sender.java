package sample.connection.server.threads;

import sample.logic.Packet;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Sender implements Runnable {
    private final Packet answer;
    private final ObjectOutputStream out;
    private final Boolean isLogin;

    Sender(Packet answer, ObjectOutputStream out, Boolean isLogin) {
        this.answer = answer;
        this.out = out;
        this.isLogin = isLogin;
    }

    @Override
    public void run() {
        try {
            answer.setIsLogin(isLogin);
            out.writeObject(answer);        // EXCEPTION STREAM HEADER ON SYNC METHOD
            //out.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(answer.getArgument().toString() + " ...");
    }
}
