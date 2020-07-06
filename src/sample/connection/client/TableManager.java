package sample.connection.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import sample.commands.LoadTableCmd;
import sample.commands.exceptions.OutOfBoundsException;
import sample.logic.collectionClasses.Route;
import sample.logic.collectionClasses.RouteFX;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class TableManager {
    private Client client;
    private ObservableList<RouteFX> routesData = FXCollections.observableArrayList();

    @FXML
    private TableView<RouteFX> tableRoutes;

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

    TableManager(Client client) {
        this.client = client;
    }

    @FXML
    public void loadTable() {
        initData();

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

        tableRoutes.setItems(routesData);
    }

    private void initData() {
        try {
            LoadTableCmd cmd = new LoadTableCmd();
            client.handleRequest(cmd);
            CopyOnWriteArrayList<Route> check = (CopyOnWriteArrayList<Route>) client.getAnswer();
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
