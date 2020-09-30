package sample.connection.client.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.commands.ExecScriptCmd;
import sample.connection.client.Client;
import sample.connection.client.localization.Localizer;
import sample.logic.Packet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;


public class ExecScriptWindow extends Application {
    private Client client;
    private final Localizer localizer = AuthorizationWindow.getLocalizer();
    private ArrayList<Packet> packets;
    private File file;

    @FXML private Text cmdName;
    @FXML private Text chosenFileText;
    @FXML private Text resultText;
    @FXML private Text filename;
    @FXML private Button chooseFileButton;
    @FXML private Button startScriptButton;
    @FXML private TextArea executionRes;

    @Override
    public void start(Stage mainWindow) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/execScriptWindow.fxml"));
        mainWindow.setTitle("Execute Script");
        mainWindow.setScene(new Scene(root, 600, 400));

        mainWindow.show();
    }

    @FXML
    public void initialize() {
        client = AuthorizationWindow.getClient();

        cmdName.setText(localizer.get().getString("ExecuteScriptName"));
        chooseFileButton.setText(localizer.get().getString("ChooseFile"));
        startScriptButton.setText(localizer.get().getString("Execute"));
        chosenFileText.setText(localizer.get().getString("ChosenFile"));
        resultText.setText(localizer.get().getString("ExecResultText"));

        chooseFileButton.setOnAction(this::chooseFileButton);
        startScriptButton.setOnAction(this::startScriptButton);
    }

    private void chooseFileButton(ActionEvent actionEvent) {
        Stage stage = new Stage();
        final FileChooser fileChooser = new FileChooser();
        file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            filename.setText(file.getName());
        }
    }

    private void startScriptButton(ActionEvent actionEvent) {
        filename.setFill(Color.BLACK);
        StringBuilder ans = new StringBuilder();
        ExecutorService execThread = Executors.newFixedThreadPool(3);

        packets = new ArrayList<>();

        Callable<ArrayList<Packet>> execTask = () -> packets = new ExecScriptCmd(client)
                .execute(file.getAbsolutePath());

        Thread print = new Thread(() -> {
            if (packets != null && !packets.isEmpty()) {
                for (Packet packet : packets) {
                    try {
                        client.sendPacket(packet);
                        Thread.sleep(100);

                        String answer = (String) client.getMessageReader().getAnswer();
                        if (answer.startsWith("id")) {
                            String[] lines = answer.split("\n");
                            for (int i = 0; i < lines.length; i++) {
                                lines[i] = lines[i].replace("id", localizer.get().getString("IdColumn"))
                                        .replace("name", localizer.get().getString("NameColumn"))
                                        .replace("coordinates", localizer.get().getString("Coordinates"))
                                        .replace("creation date", localizer.get().getString("CreationDateColumn"))
                                        .replace("location from", localizer.get().getString("AddFrom"))
                                        .replace("location to", localizer.get().getString("AddTo"))
                                        .replace("distance", localizer.get().getString("DistanceColumn"))
                                        .replace("owner", localizer.get().getString("OwnerColumn"));
                            }
                            for (String line : lines) {
                                ans.append(line).append('\n');
                            }
                        } else if (answer.startsWith("Type")) {
                            ans.append(answer.replace("Type", localizer.get().getString("Type"))
                                    .replace("collection size", localizer.get().getString("CollectionSize")))
                                    .append("\n");
                        } else if (answer.startsWith("Unique")) {
                            ans.append(answer.replace("Unique distance", localizer.get()
                                    .getString("UniqueDistance"))).append("\n");
                        } else if (answer.startsWith("Help")) {
                            ans.append(localizer.get().getString("HelpAnswer")).append("\n");
                        }

                        executionRes.setText(String.valueOf(ans));
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Future<ArrayList<Packet>> result = execThread.submit(execTask);
        try {
            packets = result.get();
        } catch (InterruptedException | ExecutionException | NullPointerException e) {
            filename.setFill(Color.RED);
            filename.setText(localizer.get().getString("FileError"));
        }
        execThread.submit(print);
    }
}
