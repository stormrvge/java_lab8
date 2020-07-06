package sample.connection.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.commands.LoadTableCmd;
import sample.commands.exceptions.OutOfBoundsException;
import sample.logic.collectionClasses.Route;
import sample.logic.collectionClasses.RouteFX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class ProgramMainWindow extends Application {
    private Client client;
    private TableManager tableManager;

    private ObservableList<RouteFX> routesData = FXCollections.observableArrayList();

    @FXML
    private TableView<RouteFX> tableCollection;

    @FXML
    private TableColumn<RouteFX, Integer> idColumn;

    @FXML
    private TableColumn<RouteFX, String> nameColumn;

    @FXML
    private TableColumn<RouteFX, Double> coordXColumn;

    @FXML
    private TableColumn<RouteFX, Double> coordYColumn;

    @FXML
    private TableColumn<RouteFX, Float> fromXColumn;

    @FXML
    private TableColumn<RouteFX, Integer> fromYColumn;

    @FXML
    private TableColumn<RouteFX, Integer> fromZColumn;

    @FXML
    private TableColumn<RouteFX, Float> toXColumn;

    @FXML
    private TableColumn<RouteFX, Integer> toYColumn;

    @FXML
    private TableColumn<RouteFX, Integer> toZColumn;

    @FXML
    private TableColumn<RouteFX, Float> distanceColumn;

    @FXML
    private TableColumn<RouteFX, String> ownerColumn;

    @FXML
    private Text username;

    @FXML
    private Button logout;

    @Override
    public void start(Stage mainWindow) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/programMainWindow.fxml"));
        mainWindow.setTitle("Main Window");
        mainWindow.setScene(new Scene(root, 800, 600));

        mainWindow.show();
    }

    @FXML
    public void initialize() {
        client = AuthorizationWindow.getClient();
        username.setText(client.getUser().getUsername());
        loadTable();

        logout.setOnAction(this::logoutButton);
    }

    @FXML
    private void logoutButton(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) logout.getScene().getWindow();
            stage.close();

            AuthorizationWindow authorizationWindow = new AuthorizationWindow();
            authorizationWindow.start(stage);
        } catch (Exception e) {
            e.getMessage();
        }
    }


    @FXML
    private void loadTable() {
        initTable();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        coordXColumn.setCellValueFactory(new PropertyValueFactory<>("coordX"));
        coordYColumn.setCellValueFactory(new PropertyValueFactory<>("coordY"));
        toXColumn.setCellValueFactory(new PropertyValueFactory<>("toX"));
        toYColumn.setCellValueFactory(new PropertyValueFactory<>("toY"));
        toZColumn.setCellValueFactory(new PropertyValueFactory<>("toZ"));
        fromXColumn.setCellValueFactory(new PropertyValueFactory<>("fromX"));
        fromYColumn.setCellValueFactory(new PropertyValueFactory<>("fromY"));
        fromZColumn.setCellValueFactory(new PropertyValueFactory<>("fromZ"));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));

        tableCollection.setItems(routesData);
    }

    private void initTable() {
        try {
            LoadTableCmd cmd = new LoadTableCmd();
            client.handleRequest(cmd);
            ArrayList<Route> check = (ArrayList<Route>) client.getAnswer();
            check = check.stream().sorted(Comparator.comparing(Route::getId))
                    .collect(Collectors.toCollection(ArrayList::new));
            for (Route route : check) {
                try {
                    routesData.add(route.transformToFX());
                } catch (OutOfBoundsException e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
    }
}
