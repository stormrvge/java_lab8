package sample.connection.client.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.FloatStringConverter;
import sample.commands.CountByDistanceCmd;
import sample.connection.client.Client;
import sample.connection.client.localization.Localizer;

import java.io.IOException;


public class CountByDistanceWindow extends Application  {
    private Client client;
    private final Localizer localizer = AuthorizationWindow.getLocalizer();

    @FXML private Text cmdName;
    @FXML private TextArea textArea;
    @FXML private TextField enterDistance;
    @FXML private Button countButton;


    @Override
    public void start(Stage TextedCmd) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/countByDistanceWindow.fxml"));
        TextedCmd.setTitle("Count By Distance");
        TextedCmd.setScene(new Scene(root, 381, 199));

        TextedCmd.show();
    }

    @FXML
    public void initialize() {
        client = AuthorizationWindow.getClient();

        cmdName.setText(localizer.get().getString("CountByDistanceText"));
        enterDistance.setPromptText(localizer.get().getString("AddDistance"));
        countButton.setText(localizer.get().getString("CountButton"));

        countButton.setOnAction(this::countButton);
    }

    private void countButton(ActionEvent actionEvent) {
        CountByDistanceCmd cmd = new CountByDistanceCmd();
        try {
            StringConverter<Float> doubleStringConverter = new FloatStringConverter();
            Float distance = doubleStringConverter.fromString(enterDistance.getText());
            client.handleRequest(cmd, distance);
            Thread.sleep(100);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        String answer = (String) client.getMessageReader().getAnswer();
        answer = answer.replace("Number of coincidences", localizer.get().getString("NumberOfCoincidences"));
        textArea.setText(answer);
    }
}
