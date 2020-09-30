package sample.connection.client.gui;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import sample.connection.client.Client;
import sample.connection.client.localization.Localizer;
import sample.logic.collectionClasses.Route;

public class ChangeElementWindow extends Application {
    private final Localizer localizer = AuthorizationWindow.getLocalizer();
    private final Route selectedRoute = ProgramMainWindow.getSelectedRoute();

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
    @FXML private TextField owner;

    @FXML private Text nameText;
    @FXML private Text coordinateText;
    @FXML private Text locationText;
    @FXML private Text fromText;
    @FXML private Text toText;
    @FXML private Text distanceText;
    @FXML private Text ownerText;

    @Override
    public void start(Stage changeElementWindow) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/changeElementWindow.fxml"));
        changeElementWindow.setTitle("Main Window");
        changeElementWindow.setScene(new Scene(root, 300, 360));

        changeElementWindow.show();
    }

    @FXML
    public void initialize() {
        Client client = AuthorizationWindow.getClient();

        StringConverter<Integer> integerStringConverter = new IntegerStringConverter();
        StringConverter<Double> doubleStringConverter = new DoubleStringConverter();
        StringConverter<Float> floatStringConverter = new FloatStringConverter();

        nameText.setText(localizer.get().getString("AddName"));
        name.setPromptText(localizer.get().getString("AddNamePrompt"));
        coordinateText.setText(localizer.get().getString("AddCoordinates"));
        locationText.setText(localizer.get().getString("AddLocation"));
        fromText.setText(localizer.get().getString("AddFrom"));
        toText.setText(localizer.get().getString("AddTo"));
        distance.setPromptText(localizer.get().getString("AddDistance"));
        distanceText.setText(localizer.get().getString("DistanceColumn"));
        ownerText.setText(localizer.get().getString("OwnerColumn"));


        name.setText(selectedRoute.getName());
        coordX.setText(doubleStringConverter.toString(selectedRoute.getCoordX()));
        coordY.setText(doubleStringConverter.toString(selectedRoute.getCoordY()));
        fromX.setText(floatStringConverter.toString(selectedRoute.getFromX()));
        fromY.setText(integerStringConverter.toString(selectedRoute.getFromY()));
        fromZ.setText(integerStringConverter.toString(selectedRoute.getFromZ()));
        toX.setText(floatStringConverter.toString(selectedRoute.getToX()));
        toY.setText(integerStringConverter.toString(selectedRoute.getToY()));
        toZ.setText(integerStringConverter.toString(selectedRoute.getToZ()));
        distance.setText(floatStringConverter.toString(selectedRoute.getDistance()));
        owner.setText(selectedRoute.getOwner());

        name.setEditable(false);
        coordX.setEditable(false);
        coordY.setEditable(false);
        fromX.setEditable(false);
        fromY.setEditable(false);
        fromZ.setEditable(false);
        toX.setEditable(false);
        toY.setEditable(false);
        toZ.setEditable(false);
        distance.setEditable(false);
        owner.setEditable(false);
    }
}
