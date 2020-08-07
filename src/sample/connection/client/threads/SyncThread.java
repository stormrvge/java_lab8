package sample.connection.client.threads;

import sample.connection.client.Client;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SyncThread extends Thread {
    Client client;

    public SyncThread(Client client) {
        this.client = client;
    }
 /*
    @Override
    public void run() {
        ByteBuffer msg = client.getMsg();
        while (true) {
            if (msg.position() == 0) {
                System.out.print(".");
                try {
                    Thread.sleep(1000);
                    //client.readMessage();
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

  */
}
