package sample.connection.client.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.commands.RegisterCmd;
import sample.commands.LoginCmd;
import sample.connection.client.Client;
import sample.connection.client.localization.Localizer;

import java.io.IOException;

public class AuthorizationWindow extends Application {
    static private Client client;
    private Localizer localizer;

    @FXML private Text text;
    @FXML private Button reg;
    @FXML private Button log;
    @FXML private TextField textField;
    @FXML private PasswordField passField;
    @FXML private Text msg;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/authorization.fxml"));
        primaryStage.setTitle("lab8");
        primaryStage.setScene(new Scene(root, 350, 250));
        primaryStage.setResizable(false);

        primaryStage.show();

        client = new Client("localhost", 27027);
        client.run();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    public void initialize() {
        localizer = new Localizer();

        text.setText(localizer.get().getString("Authorization"));
        log.setText(localizer.get().getString("Login"));
        reg.setText(localizer.get().getString("Register"));
        textField.setPromptText(localizer.get().getString("EnterLogin"));
        passField.setPromptText(localizer.get().getString("EnterPass"));

        reg.setOnAction(this::regButton);
        log.setOnAction(this::loginButton);
    }


    @FXML
    private void regButton(ActionEvent actionEvent) {
        String[] args = new String[] {textField.getText(), passField.getText()};
        RegisterCmd cmd = new RegisterCmd();
        try {
            client.handleRequest(cmd, args);
            if (client.getMessageReader().getBoolAnswer()) {
                msg.setFill(Color.GREEN);
                msg.setText(localizer.get().getString("RegisterSuccess"));
            } else {
                msg.setText(localizer.get().getString("RegisterFail"));
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loginButton(ActionEvent actionEvent) {
        String[] args = new String[] {textField.getText(), passField.getText()};
        LoginCmd cmd = new LoginCmd();
        try {
            client.handleRequest(cmd, (Object[]) args);
            if (client.getUser().getLoginState()) {
                Stage stage = (Stage) log.getScene().getWindow();
                stage.close();

                ProgramMainWindow programMainWindow = new ProgramMainWindow();
                programMainWindow.start(stage);
            } else {
                msg.setText(localizer.get().getString("LoginFail"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public Client getClient() {return client;}
}
