package sample.connection.client;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import sample.commands.LoadTableCmd;
import sample.logic.collectionClasses.Route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;


public class TableManager extends Application  {
    private Client client;
    private ObservableList<Route> routesData = FXCollections.observableArrayList();
    private StringConverter<Integer> integerStringConverter;
    private StringConverter<Double> doubleStringConverter;
    private StringConverter<Float> floatStringConverter;

    @FXML private TableView<Route> tableCollection;
    @FXML private TableColumn<Route, Integer> idColumn;
    @FXML private TableColumn<Route, String> nameColumn;
    @FXML private TableColumn<Route, Double> coordXColumn;
    @FXML private TableColumn<Route, Double> coordYColumn;
    @FXML private TableColumn<Route, Float> fromXColumn;
    @FXML private TableColumn<Route, Integer> fromYColumn;
    @FXML private TableColumn<Route, Integer> fromZColumn;
    @FXML private TableColumn<Route, Float> toXColumn;
    @FXML private TableColumn<Route, Integer> toYColumn;
    @FXML private TableColumn<Route, Integer> toZColumn;
    @FXML private TableColumn<Route, Float> distanceColumn;
    @FXML private TableColumn<Route, String> creationDate;
    @FXML private TableColumn<Route, String> ownerColumn;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/programMainWindow.fxml"));

        doubleStringConverter = new DoubleStringConverter();
        floatStringConverter = new FloatStringConverter();
        integerStringConverter = new IntegerStringConverter();
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
        creationDate.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        ownerColumn.setCellValueFactory(new PropertyValueFactory<>("owner"));

        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        coordXColumn.setCellFactory(TextFieldTableCell.forTableColumn(doubleStringConverter));
        coordYColumn.setCellFactory(TextFieldTableCell.forTableColumn(doubleStringConverter));
        fromXColumn.setCellFactory(TextFieldTableCell.forTableColumn(floatStringConverter));
        fromYColumn.setCellFactory(TextFieldTableCell.forTableColumn(integerStringConverter));
        fromZColumn.setCellFactory(TextFieldTableCell.forTableColumn(integerStringConverter));
        toXColumn.setCellFactory(TextFieldTableCell.forTableColumn(floatStringConverter));
        toYColumn.setCellFactory(TextFieldTableCell.forTableColumn(integerStringConverter));
        toZColumn.setCellFactory(TextFieldTableCell.forTableColumn(integerStringConverter));
        distanceColumn.setCellFactory(TextFieldTableCell.forTableColumn(floatStringConverter));

        tableCollection.setItems(routesData);
    }

    private void initTable() {
        try {
            LoadTableCmd cmd = new LoadTableCmd();
            client.handleRequest(cmd);
            ArrayList<Route> check = (ArrayList<Route>) client.getAnswer();
            check = check.stream().sorted(Comparator.comparing(Route::getId))
                    .collect(Collectors.toCollection(ArrayList::new));
            routesData.addAll(check);
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }
    }

    public void addInTable(Route route) {
        routesData.add(route);
        tableCollection.setItems(routesData);
    }
}
