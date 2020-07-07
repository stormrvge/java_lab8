package sample.connection.client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.commands.AddCmd;
import sample.commands.Command;
import sample.commands.exceptions.OutOfBoundsException;
import sample.logic.User;
import sample.logic.collectionClasses.Coordinates;
import sample.logic.collectionClasses.Location;
import sample.logic.collectionClasses.Route;

import java.io.IOException;

public class AddElementWindow extends Application  {
    private Client client;
    private ProgramMainWindow mainWindow;
    private Route route;

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

    @FXML private Button addButton;
    @FXML private Button clearButton;

    /*
    AddElementWindow(ProgramMainWindow mainWindow) {
        super();
        this.mainWindow = mainWindow;
    }
     */

    @Override
    public void start(Stage addElementWindow) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/addElementWindow.fxml"));
        addElementWindow.setTitle("Main Window");
        addElementWindow.setScene(new Scene(root, 300, 320));

        addElementWindow.show();
    }

    @FXML
    public void initialize() {
        client = AuthorizationWindow.getClient();

        addButton.setOnAction(this::addButton);
        clearButton.setOnAction(this::clearButton);
    }

    private void clearButton(ActionEvent actionEvent) {
    }

    private void addButton(ActionEvent actionEvent) {
        User user = AuthorizationWindow.getClient().getUser();

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

            Route route = new Route(name, new Coordinates(coordX, coordY), new Location(fromX, fromY, fromZ) ,
                    new Location(toX, toY, toZ), distance, user.getUsername());

            Command cmd = new AddCmd();
            client.handleRequest(cmd, route);

        } catch (OutOfBoundsException e) {
            System.err.println(e.getMessage());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

    }


}
