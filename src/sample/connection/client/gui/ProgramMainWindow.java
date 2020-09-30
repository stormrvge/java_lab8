package sample.connection.client.gui;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import sample.commands.*;
import sample.commands.exceptions.OutOfBoundsException;
import sample.connection.client.Client;
import sample.connection.client.localization.Localizer;
import sample.connection.client.threads.SyncCheckerThread;
import sample.logic.SerializableColor;
import sample.logic.Vector;
import sample.logic.collectionClasses.Route;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class ProgramMainWindow extends Application {
    private static Client client;
    private static Boolean synced = true;
    private boolean isSortedByDistance = false;

    private final Localizer localizer = AuthorizationWindow.getLocalizer();

    private StringConverter<Integer> integerStringConverter;
    private StringConverter<Double> doubleStringConverter;
    private StringConverter<Float> floatStringConverter;


    private Circle circle;
    private static final int pixel_step = 20;

    private static final ObservableList<Route> routesData = FXCollections.observableArrayList();
    private static final ObservableList<Circle> circlesList = FXCollections.observableArrayList();
    private static final HashMap<Circle, Route> routeHashMap = new HashMap<>();
    private static HashMap<String, SerializableColor> ownerColorHashMap = new HashMap<>();
    private static Route selectedRoute;

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

    @FXML private Tab collection;
    @FXML private Text username;
    @FXML private Text usernameText;
    @FXML private TextField filteringSearchText;
    @FXML private Text filteringBy;
    @FXML private Text errorMsg;
    @FXML private Text languageText;

    @FXML private ChoiceBox<String> filterChoiceBox;
    @FXML private ChoiceBox<String> languageChoiceBox;

    @FXML private Button logoutButton;
    @FXML private Button addElementButton;
    @FXML private Button infoButton;
    @FXML private Button clearCollectionButton;
    @FXML private Button countByDistanceButton;
    @FXML private Button removeButton;
    @FXML private Button uniqueDistanceButton;
    @FXML private Button showButton;
    @FXML private Button sortButton;
    @FXML private Button executeScriptButton;
    @FXML private Button helpButton;

    @FXML private AnchorPane coordpane;
    @FXML private Pane drawpane;
    @FXML private Tab coordTab;

    Thread printPermissionError = new Thread() {
        @Override
       public void run() {
            try {
                errorMsg.setText(localizer.get().getString("PermissionException"));
                Thread.sleep(3000);
                errorMsg.setText("");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }}};
    ExecutorService executorService = Executors.newFixedThreadPool(1);

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

        languageLoad();

        Task<Void> task = new SyncCheckerThread(this);
        new Thread(task).start();

        username.setText(client.getUser().getUsername());
        doubleStringConverter = new DoubleStringConverter();
        floatStringConverter = new FloatStringConverter();
        integerStringConverter = new IntegerStringConverter();

        //COLLECTION INIT
        languageChoice();
        loadTable();
        draw();
        filtering();

        //ANIMATION INIT
        initShapes();
        drawShapes();

        //BUTTONS ACTION LISTENERS
        logoutButton.setOnAction(this::logoutButton);
        addElementButton.setOnAction(this::addElementButton);
        showButton.setOnAction(this::showButton);
        uniqueDistanceButton.setOnAction(this::uniqueDistanceButton);
        infoButton.setOnAction(this::infoButton);
        removeButton.setOnAction(this::removeButton);
        helpButton.setOnAction(this::helpButton);
        countByDistanceButton.setOnAction(this::countByDistanceButton);
        sortButton.setOnAction(this::sortButton);
        clearCollectionButton.setOnAction(this::clearCollectionButton);
        executeScriptButton.setOnAction(this::executeScriptButton);
        //coordTab.setOnSelectionChanged(this::draw);
        coordTab.setContent(drawpane);

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

    private void languageLoad() {
        collection.setText(localizer.get().getString("Collection"));
        coordTab.setText(localizer.get().getString("Coordinates"));
        usernameText.setText(localizer.get().getString("Username"));
        logoutButton.setText(localizer.get().getString("Logout"));
        filteringBy.setText(localizer.get().getString("FilteringText"));
        languageText.setText(localizer.get().getString("Language"));

        idColumn.setText(localizer.get().getString("IdColumn"));
        nameColumn.setText(localizer.get().getString("NameColumn"));
        coordXColumn.setText(localizer.get().getString("XCoordColumn"));
        coordYColumn.setText(localizer.get().getString("YCoordColumn"));
        fromXColumn.setText(localizer.get().getString("XFromColumn"));
        fromYColumn.setText(localizer.get().getString("YFromColumn"));
        fromZColumn.setText(localizer.get().getString("ZFromColumn"));
        toXColumn.setText(localizer.get().getString("XToColumn"));
        toYColumn.setText(localizer.get().getString("YToColumn"));
        toZColumn.setText(localizer.get().getString("ZToColumn"));
        distanceColumn.setText(localizer.get().getString("DistanceColumn"));
        creationDate.setText(localizer.get().getString("CreationDateColumn"));
        ownerColumn.setText(localizer.get().getString("OwnerColumn"));

        addElementButton.setText(localizer.get().getString("AddElement"));
        infoButton.setText(localizer.get().getString("InfoButton"));
        helpButton.setText(localizer.get().getString("HelpButton"));
        clearCollectionButton.setText(localizer.get().getString("ClearCollection"));
        countByDistanceButton.setText(localizer.get().getString("CountByDistanceButton"));
        removeButton.setText(localizer.get().getString("Remove"));
        uniqueDistanceButton.setText(localizer.get().getString("UniqueDistanceButton"));
        showButton.setText(localizer.get().getString("ShowButton"));
        sortButton.setText(localizer.get().getString("Sort"));
        executeScriptButton.setText(localizer.get().getString("ExecuteScript"));
    }

    private void executeScriptButton(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();

            ExecScriptWindow execScriptWindow = new ExecScriptWindow();
            execScriptWindow.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sortButton(ActionEvent actionEvent) {
        if (isSortedByDistance) {
            routesData.sort(Comparator.comparing(Route::getId));
        } else {
            routesData.sort(Comparator.comparing(Route::getDistance));
        }
        isSortedByDistance = !isSortedByDistance;
    }

    private void countByDistanceButton(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            CountByDistanceWindow window = new CountByDistanceWindow();
            window.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void helpButton(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            HelpCmd cmd = new HelpCmd();
            client.handleRequest(cmd);

            TextedCmd textedCmdWindow = new TextedCmd();
            textedCmdWindow.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearCollectionButton(ActionEvent actionEvent) {
        try {
            ClearCmd cmd = new ClearCmd();
            client.handleRequest(cmd, "");

            ArrayList<Route> check = new ArrayList<>();
            check = routesData.stream().filter(route -> route.getOwner().equals(client.getUser().getUsername()))
                    .collect(Collectors.toCollection(ArrayList::new));
            routesData.removeAll(check);

            initTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void removeButton(ActionEvent actionEvent) {
        try {
            Route route = tableCollection.getSelectionModel().getSelectedItem();
            RemoveCmd cmd = new RemoveCmd();
            client.handleRequest(cmd, route.getId());
            routesData.remove(route);

            synced = false;
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println(localizer.get().getString("NotSelectedElementForDelete"));
        }
    }

    private void infoButton(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            InfoCmd cmd = new InfoCmd();
            client.handleRequest(cmd);

            TextedCmd textedCmdWindow = new TextedCmd();
            textedCmdWindow.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uniqueDistanceButton(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            UniqueDistanceCmd cmd = new UniqueDistanceCmd();
            client.handleRequest(cmd);

            TextedCmd textedCmdWindow = new TextedCmd();
            textedCmdWindow.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showButton(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            ShowCmd cmd = new ShowCmd();
            client.handleRequest(cmd);

            TextedCmd textedCmdWindow = new TextedCmd();
            textedCmdWindow.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addElementButton(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();

            AddElementWindow addElementWindow = new AddElementWindow();
            addElementWindow.start(stage);

            synced = false;
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
            synced = false;
        } else {
            initTable();
            executorService.submit(printPermissionError);
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
                initShapes();
                drawShapes();

                synced = false;
            } catch (IOException | InterruptedException | OutOfBoundsException e) {
                System.err.println(e.getMessage());
            }
            System.out.println(route.getCoordX());
        } else {
            initTable();
            executorService.submit(printPermissionError);
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
                initShapes();
                drawShapes();  

                synced = false;
            } catch (IOException | InterruptedException | OutOfBoundsException e) {
                System.err.println(e.getMessage());
            }
            System.out.println(route.getCoordY());
        } else {
            initTable();
            executorService.submit(printPermissionError);
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

            synced = false;
        } else {
            initTable();
            executorService.submit(printPermissionError);
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

            synced = false;
        } else {
            initTable();
            executorService.submit(printPermissionError);
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

            synced = false;
        } else {
            initTable();
            executorService.submit(printPermissionError);
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

            synced = false;
        } else {
            initTable();
            executorService.submit(printPermissionError);
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

            synced = false;
        } else {
            initTable();
            executorService.submit(printPermissionError);
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

            synced = false;
        } else {
            initTable();
            executorService.submit(printPermissionError);
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

                initShapes(); 
                drawShapes(); 
            } catch (IOException | InterruptedException | OutOfBoundsException e) {
                System.err.println(e.getMessage());
            }
            System.out.println(route.getDistance());

            synced = false;
        } else {
            initTable();                                                          
            executorService.submit(printPermissionError);
        }
    }

    @FXML
    private void loadTable() {
        routesData.clear();
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

    public static void initTable() {
        try {
            routesData.clear();
            
            LoadTableCmd cmd = new LoadTableCmd();
            ArrayList<Route> check = new ArrayList<>();
            client.handleRequest(cmd);
            if (client.getAnsPacket().getArgument() != null) {          //FIX
                Object[] answer = (Object[]) client.getAnsPacket().getArgument();

                check = (ArrayList<Route>) answer[0];
                ownerColorHashMap = (HashMap<String, SerializableColor>) answer[1];
            } else {
                System.out.println("Answer from server is empty.");
            }
            check = check.stream().sorted(Comparator.comparing(Route::getId))
                    .collect(Collectors.toCollection(ArrayList::new));
            routesData.addAll(check);

            synced = true;
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        } catch (ClassCastException ignored) {}
    }

    public static void setTable(ArrayList<Route> routes) {
        routesData.clear();
        routes = routes.stream().sorted(Comparator.comparing(Route::getId))
                .collect(Collectors.toCollection(ArrayList::new));
        routesData.addAll(routes);

        synced = true;
    }

    private void filtering() {
        String id = localizer.get().getString("IdColumn");
        String name = localizer.get().getString("NameColumn");
        String distance = localizer.get().getString("DistanceColumn");
        String creationDate = localizer.get().getString("CreationDateColumn");
        String owner = localizer.get().getString("OwnerColumn");
        ObservableList<String> filteringBy = FXCollections.observableArrayList(id, name, distance,
                creationDate, owner);
        filterChoiceBox.setValue(id);
        filterChoiceBox.setItems(filteringBy);

        FilteredList<Route> filteredList = new FilteredList<>(routesData, p -> true);
        filteringSearchText.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(route -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();


                if (filterChoiceBox.getValue().equals(name) &&
                        route.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (filterChoiceBox.getValue().equals(owner) &&
                        route.getOwner().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (filterChoiceBox.getValue().equals(distance) &&
                        Float.toString(route.getDistance()).contains(lowerCaseFilter)) {
                    return true;
                } else if (filterChoiceBox.getValue().equals(creationDate) &&
                        route.getCreationDate().contains(newValue)) {
                    return true;
                } else if (filterChoiceBox.getValue().equals(id) &&
                        Integer.toString(route.getId()).contains(newValue)) {
                    return true;
                }
                return false;
            });
        });
        SortedList<Route> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(tableCollection.comparatorProperty());
        tableCollection.setItems(sortedList);
    }

    private void languageChoice() {
        String RUS = "Русский";
        String ENG = "English";

        ObservableList<String> languageList = FXCollections.observableArrayList(RUS, ENG);
        languageChoiceBox.setValue(RUS);
        languageChoiceBox.setItems(languageList);

        languageChoiceBox.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) ->
                {
                    if (newValue.equals(RUS)) {
                        localizer.setRus();
                        languageLoad();
                    } else if (newValue.equals(ENG)) {
                        localizer.setEng();
                        languageLoad();
                    }
                });
    }

    //                  VISUALISATION WINDOW
    private void draw() {
        double width = drawpane.getPrefWidth();
        double height = drawpane.getPrefHeight();

        //ОТРИСОВКА ОСЕЙ
        drawpane.getChildren().add(new Line(width/2 , 0, width/2, height));
        drawpane.getChildren().add(new Line(0, height/2, width, height/2));

        //ОТРИСОВКА линий каждые 5
        for(int i = (int) (width/2 % pixel_step); i < width; i += pixel_step) {
            drawpane.getChildren().add(
                    new Line(i, height/2 - 5, i, height/2 + 5)); }
        for(int i = (int) (height/2 % pixel_step); i < height; i += pixel_step) {
            drawpane.getChildren().add(new Line(width/2 - 5 , i, width/2 + 5, i));}

        //ОТРИСОВКА сетки
        for(int i = (int) (width/2) % pixel_step; i < width; i += pixel_step/2) {
            Line line = new Line(i, 0, i, height);
            line.setStrokeWidth(0.2);
            drawpane.getChildren().add(line); }
        for(int i = (int) height/2 % pixel_step; i < height; i += pixel_step/2) {
            Line line = new Line(0, i, width, i);
            line.setStrokeWidth(0.2);
            drawpane.getChildren().add(line); }
    }

    EventHandler<MouseEvent> circleEvent = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            circle = (Circle) mouseEvent.getSource();
            System.out.println("TEST CIRCLE EVENT");
            selectedRoute = routeHashMap.get(circle);
            System.out.println(routeHashMap.get(circle).getId());

            ChangeElementWindow changeElementWindow = new ChangeElementWindow();
            try {
                Stage stage = new Stage();
                changeElementWindow.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void initShapes() {
        circlesList.clear();

        for (Route route : routesData) {
            Vector vector = Vector.toPixels(drawpane, pixel_step, route.getCoordX(), route.getCoordY());
            circle = new Circle(vector.getX(), vector.getY(), route.getDistance() * 3);
            circle.setFill(ownerColorHashMap.get(route.getOwner()).getColor());
            circle.addEventFilter(MouseEvent.MOUSE_CLICKED, circleEvent);
            circlesList.add(circle);
            routeHashMap.put(circle, route);
        }
    }

    private void drawShapes() {
        // СДЕЛАТЬ ЧЕРЕЗ ИТЕРАТОР
        drawpane.getChildren().removeAll(circlesList);

        ArrayList<Node> circles = drawpane.getChildren().stream().filter(node -> node.getClass().getSimpleName().equals("Circle"))
                .collect(Collectors.toCollection(ArrayList::new));
        drawpane.getChildren().removeAll(circles);

        for (int i = 0; circlesList.size() > i; i++) {
            drawpane.getChildren().add(circlesList.get(i));
        }
    }

    public static Boolean getSynced() {
        return synced;
    }

    public static void setSynced(boolean bool) {
        synced = bool;
    }

    public Client getClient() {
        return client;
    }


    public static Route getSelectedRoute() {
        return selectedRoute;
    }

    public void sync(ArrayList<Route> routes) {
        setTable(routes);
        initShapes();
        drawShapes();

        synced = true;
    }
}

