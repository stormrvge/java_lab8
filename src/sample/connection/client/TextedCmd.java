package sample.connection.client;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class TextedCmd extends Application  {
    private ProgramMainWindow mainWindow;

    @FXML private Text cmdName;
    @FXML private TextArea textArea;
    @FXML private HBox hBox;

    @Override
    public void start(Stage addElementWindow) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/textedCmd.fxml"));
        addElementWindow.setTitle("Command");
        addElementWindow.setScene(new Scene(root, 600, 400));

        addElementWindow.show();
    }

    @FXML
    public void initialize() {
        Client client = AuthorizationWindow.getClient();

        cmdName.setText(client.getCommandName());
        textArea.setText((String) client.getMessageReader().getAnswer());

        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(30));
        HBox.setHgrow(textArea, Priority.ALWAYS);
    }
}
