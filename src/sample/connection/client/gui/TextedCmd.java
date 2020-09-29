package sample.connection.client.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.connection.client.Client;
import sample.connection.client.localization.Localizer;


public class TextedCmd extends Application  {

    @FXML private Text cmdName;
    @FXML private TextArea textArea;
    @FXML private HBox hBox;

    @Override
    public void start(Stage TextedCmd) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/textedCmd.fxml"));
        TextedCmd.setTitle("Command");
        TextedCmd.setScene(new Scene(root, 600, 400));

        TextedCmd.show();
    }

    @FXML
    public void initialize() {
        Localizer localizer = new Localizer();
        Client client = AuthorizationWindow.getClient();

        cmdName.setText(localizer.get().getString(client.getCommandName()));
        try {
            textArea.setText((String) client.getMessageReader().getAnswer());
        } catch (ClassCastException ignored) {}


        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(30));
        HBox.setHgrow(textArea, Priority.ALWAYS);
    }
}
