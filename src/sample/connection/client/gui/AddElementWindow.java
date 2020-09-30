package sample.connection.client.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.commands.AddCmd;
import sample.commands.AddIfMaxCmd;
import sample.commands.AddIfMinCmd;
import sample.commands.exceptions.OutOfBoundsException;
import sample.connection.client.Client;
import sample.connection.client.localization.Localizer;
import sample.logic.User;
import sample.logic.collectionClasses.Coordinates;
import sample.logic.collectionClasses.Location;
import sample.logic.collectionClasses.Route;

import java.io.IOException;

public class AddElementWindow extends Application  {
    private final Localizer localizer = AuthorizationWindow.getLocalizer();
    private Client client;

    @FXML private TextField name;
    @FXML private TextField coordX;
    @FXML private TextField coordY;
    @FXML private TextField fromX;
    @FXML private TextField fromY;
    @FXML private TextField fromZ;
    @FXML private TextField toX;
    @FXML private TextField toZ;
    @FXML private TextField toY;
    @FXML private TextField distance;

    @FXML private Text nameText;
    @FXML private Text coordinateText;
    @FXML private Text locationText;
    @FXML private Text fromText;
    @FXML private Text toText;
    @FXML private Text msgText;

    @FXML private Button addButton;
    @FXML private Button clearButton;

    @FXML private CheckBox addIfMaxCheck;
    @FXML private CheckBox addIfMinCheck;

    @Override
    public void start(Stage addElementWindow) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/addElementWindow.fxml"));
        addElementWindow.setTitle("Main Window");
        addElementWindow.setScene(new Scene(root, 300, 360));

        addElementWindow.show();
    }

    @FXML
    public void initialize() {
        client = AuthorizationWindow.getClient();

        nameText.setText(localizer.get().getString("AddName"));
        name.setPromptText(localizer.get().getString("AddNamePrompt"));
        coordinateText.setText(localizer.get().getString("AddCoordinates"));
        locationText.setText(localizer.get().getString("AddLocation"));
        fromText.setText(localizer.get().getString("AddFrom"));
        toText.setText(localizer.get().getString("AddTo"));
        distance.setPromptText(localizer.get().getString("AddDistance"));
        addIfMaxCheck.setText(localizer.get().getString("AddIfMax"));
        addIfMinCheck.setText(localizer.get().getString("AddIfMin"));
        addButton.setText(localizer.get().getString("AddButton"));
        clearButton.setText(localizer.get().getString("ClearButton"));


        addButton.setOnAction(this::addButton);
        clearButton.setOnAction(this::clearButton);
    }

    private void clearButton(ActionEvent actionEvent) {
        name.setText("");
        coordX.setText("");
        coordY.setText("");
        fromX.setText("");
        fromY.setText("");
        fromZ.setText("");
        toX.setText("");
        toY.setText("");
        toZ.setText("");
        distance.setText("");
        addIfMaxCheck.setSelected(false);
        addIfMinCheck.setSelected(false);
    }

    private void addButton(ActionEvent actionEvent) {
        User user = AuthorizationWindow.getClient().getUser();
        Route newRoute = null;
        try {
            newRoute = addElement(user);
        } catch (OutOfBoundsException e) {
            e.getMessage();
        }

        if (newRoute != null) {
            try {
                if (addIfMaxCheck.isSelected()) {
                    AddIfMaxCmd cmd = new AddIfMaxCmd();
                    client.handleRequest(cmd, newRoute);
                    if (client.getMessageReader().getBoolAnswer()) {
                        msgText.setFill(Color.GREEN);
                        msgText.setText(localizer.get().getString("AddElementSuccess"));
                    } else {
                        msgText.setFill(Color.RED);
                        msgText.setText(localizer.get().getString("AddElementMaxFail"));
                    }
                } else if (addIfMinCheck.isSelected()) {
                    AddIfMinCmd cmd = new AddIfMinCmd();
                    client.handleRequest(cmd, newRoute);
                    if (client.getMessageReader().getBoolAnswer()) {
                        msgText.setFill(Color.GREEN);
                        msgText.setText(localizer.get().getString("AddElementSuccess"));
                    } else {
                        msgText.setFill(Color.RED);
                        msgText.setText(localizer.get().getString("AddElementMinFail"));
                    }
                } else {
                    AddCmd cmd = new AddCmd();
                    client.handleRequest(cmd, newRoute);
                    if (client.getMessageReader().getBoolAnswer()) {
                        msgText.setFill(Color.GREEN);
                        msgText.setText(localizer.get().getString("AddElementSuccess"));
                    } else {
                        msgText.setFill(Color.RED);
                        msgText.setText(localizer.get().getString("AddElementFail"));
                    }
                }
                ProgramMainWindow.initTable();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        } else {
            msgText.setText(localizer.get().getString("NumberFormatException"));
        }

    }

    private Route addElement(User user) throws OutOfBoundsException {
        try {

            String name = this.name.getText();
            double coordX = Double.parseDouble(this.coordX.getText());
            double coordY = Double.parseDouble(this.coordY.getText());
            float fromX = Float.parseFloat(this.fromX.getText());
            int fromY = Integer.parseInt(this.fromY.getText());
            int fromZ = Integer.parseInt(this.fromZ.getText());
            float toX = Float.parseFloat(this.toX.getText());
            int toY = Integer.parseInt(this.toY.getText());
            int toZ = Integer.parseInt(this.toZ.getText());
            float distance = Float.parseFloat(this.distance.getText());

            if (!name.equals("")) {
                return new Route(name, new Coordinates(coordX, coordY), new Location(fromX, fromY, fromZ) ,
                        new Location(toX, toY, toZ), distance, user.getUsername());
            }
        } catch (NumberFormatException e) {
            msgText.setFill(Color.RED);
            msgText.setText(localizer.get().getString("NumberFormatException"));
        }
        return null;
    }
}
