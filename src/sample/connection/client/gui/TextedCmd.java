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
    private final Localizer localizer = AuthorizationWindow.getLocalizer();

    @Override
    public void start(Stage TextedCmd) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/textedCmd.fxml"));
        TextedCmd.setTitle("Command");
        TextedCmd.setScene(new Scene(root, 600, 400));
        TextedCmd.setResizable(false);

        TextedCmd.show();
    }

    @FXML
    public void initialize() {
        Client client = AuthorizationWindow.getClient();

        StringBuilder ans = new StringBuilder();
        String answer = (String) client.getMessageReader().getAnswer();

        switch (client.getCommandName()) {
            case "Show":
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
                break;
            case "Info":
                ans.append(answer.replace("Type", localizer.get().getString("Type"))
                        .replace("collection size", localizer.get().getString("CollectionSize")));
                break;
            case "Unique distance":
                ans.append(answer.replace("Unique distance", localizer.get().getString("UniqueDistance")));
                break;
            case "Help":
                ans.append(localizer.get().getString("HelpAnswer"));
                break;
        }


        cmdName.setText(localizer.get().getString(client.getCommandName()));
        try {
            textArea.setText(ans.toString());
        } catch (ClassCastException ignored) {}


        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(30));
        HBox.setHgrow(textArea, Priority.ALWAYS);
    }
}
