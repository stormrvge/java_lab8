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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import sample.commands.Command;
import sample.commands.LoadTableCmd;
import sample.commands.UpdateIdCmd;
import sample.commands.exceptions.OutOfBoundsException;
import sample.logic.collectionClasses.Route;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;


public class ProgramMainWindow extends Application {
    private Client client;
    private StringConverter<Integer> integerStringConverter;
    private StringConverter<Double> doubleStringConverter;
    private StringConverter<Float> floatStringConverter;

    private final ObservableList<Route> routesData = FXCollections.observableArrayList();

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

    @FXML private Text username;

    @FXML private Button logoutButton;
    @FXML private Button addElementButton;
    @FXML private Button addIfMaxButton;
    @FXML private Button infoButton;
    @FXML private Button clearCollectionButton;
    @FXML private Button filterByDistanceButton;
    @FXML private Button removeButton;
    @FXML private Button uniqueDistanceButton;
    @FXML private Button helpButton;
    @FXML private Button sortButton;
    @FXML private Button executeScriptButton;

    @Override
    public void start(Stage mainWindow) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("fxmls/programMainWindow.fxml"));
        mainWindow.setTitle("Main Window");
        mainWindow.setScene(new Scene(root, 900, 600));

        mainWindow.show();
    }

    @FXML
    public void initialize() {
        client = AuthorizationWindow.getClient();
        username.setText(client.getUser().getUsername());
        doubleStringConverter = new DoubleStringConverter();
        floatStringConverter = new FloatStringConverter();
        integerStringConverter = new IntegerStringConverter();
        loadTable();

        //BUTTONS ACTION LISTENERS
        logoutButton.setOnAction(this::logoutButton);
        addElementButton.setOnAction(this::addElementButton);
        addIfMaxButton.setOnAction(this::addIfMaxButton);

        //EDITABLE COLS
        nameColumn.setOnEditCommit(this::nameEdit);
        coordXColumn.setOnEditCommit(this::coordXEdit);
        coordYColumn.setOnEditCommit(this::coordYEdit);
        fromXColumn.setOnEditCommit(this::fromXEdit);
        fromYColumn.setOnEditCommit(this::fromYEdit);
        fromZColumn.setOnEditCommit(this::fromZEdit);
        toXColumn.setOnEditCommit(this::toXEdit);
        toYColumn.setOnEditCommit(this::toYEdit);
        toZColumn.setOnEditCommit(this::toZEdit);
        distanceColumn.setOnEditCommit(this::distanceEdit);
    }

    private void addIfMaxButton(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();

            AddElementWindow addElementWindow = new AddElementWindow();
            addElementWindow.start(stage);
        } catch (Exception ignored) {}
    }

    private void addElementButton(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();

            AddElementWindow addElementWindow = new AddElementWindow();
            addElementWindow.start(stage);
        } catch (Exception ignored) {}
    }

    @FXML
    private void logoutButton(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.close();

            AuthorizationWindow authorizationWindow = new AuthorizationWindow();
            authorizationWindow.start(stage);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void nameEdit(TableColumn.CellEditEvent<Route, String> routeStringCellEditEvent) {
        Route route = tableCollection.getSelectionModel().getSelectedItem();

        if (client.getUser().getUsername().equals(route.getOwner())) {
            try {
                route.setName(routeStringCellEditEvent.getNewValue());
                Command cmd = new UpdateIdCmd();
                Object[] args = new Object[]{route.getId(), route};
                client.handleRequest(cmd, args);
            } catch (IOException | InterruptedException e) {
                System.err.println(e.getMessage());
            }

            System.out.println(route.getName());
        } else {
            System.err.println("DO ERR IN GUI");
        }
    }

    public void coordXEdit(TableColumn.CellEditEvent<Route, Double> routeDoubleCellEditEvent) {
        Route route = tableCollection.getSelectionModel().getSelectedItem();

        if (client.getUser().getUsername().equals(route.getOwner())) {
            try {
                route.setCoordX(routeDoubleCellEditEvent.getNewValue());
                Command cmd = new UpdateIdCmd();
                Object[] args = new Object[]{route.getId(), route};
                client.handleRequest(cmd, args);
            } catch (IOException | InterruptedException | OutOfBoundsException e) {
                System.err.println(e.getMessage());
            }
            System.out.println(route.getCoordX());
        } else {
            System.err.println("DO ERR IN GUI");
        }
    }

    public void coordYEdit(TableColumn.CellEditEvent<Route, Double> routeDoubleCellEditEvent) {
        Route route = tableCollection.getSelectionModel().getSelectedItem();

        if (client.getUser().getUsername().equals(route.getOwner())) {
            try {
                route.setCoordY(routeDoubleCellEditEvent.getNewValue());
                Command cmd = new UpdateIdCmd();
                Object[] args = new Object[]{route.getId(), route};
                client.handleRequest(cmd, args);
            } catch (IOException | InterruptedException | OutOfBoundsException e) {
                System.err.println(e.getMessage());
            }
            System.out.println(route.getCoordY());
        } else {
            System.err.println("DO ERR IN GUI");
        }
    }


    public void fromXEdit(TableColumn.CellEditEvent<Route, Float> routeFloatCellEditEvent) {
        Route route = tableCollection.getSelectionModel().getSelectedItem();

        if (client.getUser().getUsername().equals(route.getOwner())) {
            try {
                route.setFromX(routeFloatCellEditEvent.getNewValue());
                Command cmd = new UpdateIdCmd();
                Object[] args = new Object[]{route.getId(), route};
                client.handleRequest(cmd, args);
            } catch (IOException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
            System.out.println(route.getFromX());
        } else {
            System.err.println("DO ERR IN GUI");
        }
    }

    public void fromYEdit(TableColumn.CellEditEvent<Route, Integer> routeIntegerCellEditEvent) {
        Route route = tableCollection.getSelectionModel().getSelectedItem();

        if (client.getUser().getUsername().equals(route.getOwner())) {
            try {
                route.setFromY(routeIntegerCellEditEvent.getNewValue());
                Command cmd = new UpdateIdCmd();
                Object[] args = new Object[]{route.getId(), route};
                client.handleRequest(cmd, args);
            } catch (IOException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
            System.out.println(route.getFromY());
        } else {
            System.err.println("DO ERR IN GUI");
        }
    }

    public void fromZEdit(TableColumn.CellEditEvent<Route, Integer> routeIntegerCellEditEvent) {
        Route route = tableCollection.getSelectionModel().getSelectedItem();

        if (client.getUser().getUsername().equals(route.getOwner())) {
            try {
                route.setFromZ(routeIntegerCellEditEvent.getNewValue());
                Command cmd = new UpdateIdCmd();
                Object[] args = new Object[]{route.getId(), route};
                client.handleRequest(cmd, args);
            } catch (IOException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
            System.out.println(route.getFromZ());
        } else {
            System.err.println("DO ERR IN GUI");
        }
    }

    public void toXEdit(TableColumn.CellEditEvent<Route, Float> routeFloatCellEditEvent) {
        Route route = tableCollection.getSelectionModel().getSelectedItem();

        if (client.getUser().getUsername().equals(route.getOwner())) {
            try {
                route.setToX(routeFloatCellEditEvent.getNewValue());
                Command cmd = new UpdateIdCmd();
                Object[] args = new Object[]{route.getId(), route};
                client.handleRequest(cmd, args);
            } catch (IOException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
            System.out.println(route.getToX());
        } else {
            System.err.println("DO ERR IN GUI");
        }
    }

    public void toYEdit(TableColumn.CellEditEvent<Route, Integer> routeIntegerCellEditEvent) {
        Route route = tableCollection.getSelectionModel().getSelectedItem();

        if (client.getUser().getUsername().equals(route.getOwner())) {
            try {
                route.setToY(routeIntegerCellEditEvent.getNewValue());
                Command cmd = new UpdateIdCmd();
                Object[] args = new Object[]{route.getId(), route};
                client.handleRequest(cmd, args);
            } catch (IOException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
            System.out.println(route.getToY());
        } else {
            System.err.println("DO ERR IN GUI");
        }
    }

    public void toZEdit(TableColumn.CellEditEvent<Route, Integer> routeIntegerCellEditEvent) {
        Route route = tableCollection.getSelectionModel().getSelectedItem();

        if (client.getUser().getUsername().equals(route.getOwner())) {
            try {
                route.setToZ(routeIntegerCellEditEvent.getNewValue());
                Command cmd = new UpdateIdCmd();
                Object[] args = new Object[]{route.getId(), route};
                client.handleRequest(cmd, args);
            } catch (IOException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
            System.out.println(route.getToZ());
        } else {
            System.err.println("DO ERR IN GUI");
        }
    }

    public void distanceEdit(TableColumn.CellEditEvent<Route, Float> routeFloatCellEditEvent) {
        Route route = tableCollection.getSelectionModel().getSelectedItem();

        if (client.getUser().getUsername().equals(route.getOwner())) {
            try {
                route.setDistance(routeFloatCellEditEvent.getNewValue());
                Command cmd = new UpdateIdCmd();
                Object[] args = new Object[]{route.getId(), route};
                client.handleRequest(cmd, args);
            } catch (IOException | InterruptedException | OutOfBoundsException e) {
                System.err.println(e.getMessage());
            }
            System.out.println(route.getDistance());
        } else {
            System.err.println("DO ERR IN GUI");
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
