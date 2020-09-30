package sample.connection.client.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
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


public class ExecScriptWindow extends Application {
    private Client client;
    private Localizer localizer;

    @FXML private Text cmdName;
    @FXML private Text chosenFileText;
    @FXML private Text resultText;
    @FXML private Text filename;
    @FXML private Button chooseFileButton;
    @FXML private Button stopScriptButton;
    @FXML private TextArea executionRes;
    @FXML private ProgressIndicator progressBar;

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
        localizer = new Localizer();

        cmdName.setText(localizer.get().getString("ExecuteScriptName"));
        chooseFileButton.setText(localizer.get().getString("ChooseFile"));
        chosenFileText.setText(localizer.get().getString("ChosenFile"));
        stopScriptButton.setText(localizer.get().getString("StopScript"));
        resultText.setText(localizer.get().getString("ExecResultText"));

        chooseFileButton.setOnAction(this::chooseFileButton);
    }

    private void chooseFileButton(ActionEvent actionEvent) {
        StringBuilder resultString = new StringBuilder();

        Stage stage = new Stage();
        final FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            filename.setText(file.getName());
            progressBar.setVisible(true);
            ArrayList<Packet> packets = new ExecScriptCmd(client).execute(file.getAbsolutePath());
            if (packets != null && !packets.isEmpty()) {
                for (Packet packet : packets) {
                    try {
                        client.sendPacket(packet);
                        Thread.sleep(100);
                        resultString.append((String)client.getMessageReader().getAnswer()).append('\n');
                        executionRes.setText(String.valueOf(resultString));
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            progressBar.setVisible(false);
        }
    }


}
