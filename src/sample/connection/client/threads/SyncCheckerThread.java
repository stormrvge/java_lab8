package sample.connection.client.threads;

import javafx.application.Platform;
import javafx.concurrent.Task;
import sample.connection.client.gui.ProgramMainWindow;
import sample.logic.Packet;
import sample.logic.collectionClasses.Route;

import java.util.ArrayList;

public class SyncCheckerThread extends Task<Void> {        //TASK
    ProgramMainWindow programMainWindow;
    MessageReader messageReader;

    public static boolean NEED = false;

    public SyncCheckerThread(ProgramMainWindow programMainWindow) {
        this.programMainWindow = programMainWindow;
        this.messageReader = programMainWindow.getClient().getMessageReader();
    }

    public void sync() throws InterruptedException {
        while (true) {
            Thread.sleep(100);

        /*
        if (!programMainWindow.getSynced()) {
            LoadTableCmd cmd = new LoadTableCmd();
            programMainWindow.getClient().handleRequest(cmd);
            Thread.sleep(100);
        }

         */

            if (messageReader.getSyncAnswer()) {
                Packet packet = messageReader.getPacket();

                Object[] ans = (Object[]) packet.getArgument();

                System.out.println("TRYING SYNC TABLE");

                Platform.runLater(() -> programMainWindow.sync((ArrayList<Route>) ans[0]));


                MessageReader.setSyncAnswer(false);
            }
        }
    }

    @Override
    public void run() {
        try {
            sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassCastException ignored) {}
    }

    @Override
    protected Void call() {
        try {
            sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
