package itmo.lab8.ui.controllers;

import itmo.lab8.ClientMain;
import itmo.lab8.basic.baseclasses.Coordinates;
import itmo.lab8.basic.baseclasses.Movie;
import itmo.lab8.basic.baseenums.MovieGenre;
import itmo.lab8.basic.baseenums.MpaaRating;
import itmo.lab8.basic.utils.serializer.Serializer;
import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandType;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.shared.Response;
import itmo.lab8.ui.Controller;
import itmo.lab8.ui.ItemCanvas;
import itmo.lab8.ui.LocaleManager;
import itmo.lab8.ui.WindowManager;
import itmo.lab8.ui.types.FilterOption;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

public class ShowController extends Controller {
    private Integer offset = 0;
    private ViewController viewController;
    @FXML
    private Label filter_by_label;
    @FXML
    private ComboBox<FilterOption> filter_options;
    @FXML
    private TextField filter_criteria;
    @FXML
    private Button apply_button;
    @FXML
    private Button visualise_button;
    @FXML
    private TableView<Movie> showTable;
    private ObservableList<Movie> movieList = FXCollections.observableArrayList();
    @FXML
    private TableColumn<Movie, Integer> idColumn;
    @FXML
    private TableColumn<Movie, String> nameColumn;
    @FXML
    private TableColumn<Movie, Coordinates> coordinatesColumn;
    @FXML
    private TableColumn<Movie, String> creationDateColumn;
    @FXML
    private TableColumn<Movie, Integer> oscarsColumn;
    @FXML
    private TableColumn<Movie, MovieGenre> genreColumn;
    @FXML
    private TableColumn<Movie, MpaaRating> mpaaColumn;
    @FXML
    private TableColumn<Movie, String> directorNameColumn;
    private ShowThread showThread = new ShowThread();

    public void nextPage() {
        this.offset += 20;
        if (viewController != null) updateView();
    }

    public void previousPage() {
        if (this.offset > 0) {
            this.offset -= 20;
            if (viewController != null) updateView();
        }
    }

    @FXML
    public void initialize() {
        fieldInit();
        showTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        showTable.fixedCellSizeProperty().setValue(20);
        showTable.setEditable(false);
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Math.toIntExact(cellData.getValue().getId())).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        coordinatesColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCoordinates()));
        creationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreationDate().format(DateTimeFormatter.ofPattern(LocaleManager.getInstance().getResource("date_pattern"), Locale.getDefault()))));
        oscarsColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Math.toIntExact(cellData.getValue().getOscars())).asObject());
        genreColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getGenre()));
        mpaaColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getMpaaRating()));
        directorNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDirector().getName()));
        coordinatesColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Coordinates coordinates, boolean empty) {
                super.updateItem(coordinates, empty);
                if (empty || coordinates == null) {
                    setText(null);
                } else {
                    setText(String.format(Locale.getDefault(), "(%.2f, %d)", coordinates.getX(), coordinates.getY()));
                }
            }
        });
        showTable.setRowFactory(tv -> {
            TableRow<Movie> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Movie movie = row.getItem();
                    try {
                        WindowManager.getInstance().newShowItemWindow(movie);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });
        filter_options.getItems().addAll(new FilterOption("none_filter_option"), new FilterOption("id_filter_option"), new FilterOption("name_filter_option"), new FilterOption("coordinates_filter_option"), new FilterOption("creation_date_filter_option"), new FilterOption("oscars_filter_option"), new FilterOption("genre_filter_option"), new FilterOption("mpaa_rating_filter_option"), new FilterOption("director_name_filter_option"));
        filter_options.setValue(filter_options.getItems().get(0));
        showThread.start();
    }

    @Override
    public void updateUi() {
        super.updateUi();
        ObservableList<FilterOption> newOptions = FXCollections.observableArrayList();
        for (FilterOption filterOption : filter_options.getItems()) {
            filterOption.refresh();
            newOptions.add(filterOption);
        }
        filter_options.getItems().clear();
        filter_options.setItems(newOptions);
        filter_options.setValue(filter_options.getItems().get(0));
    }

    @Override
    public void close() {
        this.showThread.interrupt();
        if (showThread.isAlive()) {
            showThread.interrupt();
        }
    }

    public void fieldInit() {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(FXML.class) && field.getType().equals(Label.class)) {
                try {
                    Label label = (Label) field.get(this);
                    label.setText(LocaleManager.getInstance().getResource((label.getId())));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (field.isAnnotationPresent(FXML.class) && field.getType().equals(Button.class)) {
                try {
                    Button button = (Button) field.get(this);
                    button.setText(LocaleManager.getInstance().getResource(button.getId()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void onFilterButtonClick() {
        // take filter option from combobox and filter criteria from textfield. try to parse the type of filter criteria and hang to other method.
        FilterOption filterOption = filter_options.getValue();
        String filterCriteria = filter_criteria.getText();

        if (filterOption == null || filterCriteria == null) {
            return;
        }
        switch (filterOption.getTypeName()) {
            case "id_filter_option" -> {
                try {
                    int id = Integer.parseInt(filterCriteria);
                    filterById(id);
                } catch (NumberFormatException e) {
                    // smt
                }
            }
            case "name_filter_option" -> {
                filterByName(filterCriteria);
            }
            case "coordinates_filter_option" -> {
                try {
                    String[] coordinates = filterCriteria.split(" ");
                    float x = Float.parseFloat(coordinates[0]);
                    Integer y = Integer.parseInt(coordinates[1]);
                    filterByCoordinates(new Coordinates(x, y));
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    // smt
                }
            }
            case "creation_date_filter_option" -> {
                filterByCreationDate(filterCriteria);
            }
            case "oscars_filter_option" -> {
                try {
                    int oscars = Integer.parseInt(filterCriteria);
                    filterByOscars(oscars);
                } catch (NumberFormatException e) {
                    // smt
                }
            }
            case "genre_filter_option" -> {
                try {
                    MovieGenre genre = MovieGenre.valueOf(filterCriteria.toUpperCase());
                    filterByGenre(genre);
                } catch (IllegalArgumentException e) {
                    // smt
                }
            }
            case "mpaa_rating_filter_option" -> {
                try {
                    MpaaRating mpaaRating = MpaaRating.valueOf(filterCriteria.toUpperCase());
                    filterByMpaaRating(mpaaRating);
                } catch (IllegalArgumentException e) {
                    // smt
                }
            }
            case "director_name_filter_option" -> {
                filterByDirectorName(filterCriteria);

            }
            default -> {
                showTable.setItems(movieList);
                if (showThread.isInterrupted()) {
                    showThread = new ShowThread();
                    showThread.start();
                }
                return;
            }
        }
        showThread.interrupt();
    }

    private void filterById(int id) {
        showTable.setItems(movieList.filtered(movie -> movie.getId() == id));
    }

    private void filterByName(String name) {
        showTable.setItems(movieList.filtered(movie -> movie.getName().contains(name)));
    }

    private void filterByCoordinates(Coordinates coordinates) {
        showTable.setItems(movieList.filtered(movie -> movie.getCoordinates().equals(coordinates)));
    }

    public void filterByCreationDate(String creationDate) {
        showTable.setItems(movieList.filtered(movie -> movie.getCreationDate().toString().equals(creationDate)));
    }

    private void filterByOscars(int oscars) {
        showTable.setItems(movieList.filtered(movie -> movie.getOscarsInt() > oscars));
    }

    private void filterByGenre(MovieGenre genre) {
        showTable.setItems(movieList.filtered(movie -> movie.getGenre().equals(genre)));
    }

    private void filterByMpaaRating(MpaaRating mpaaRating) {
        showTable.setItems(movieList.filtered(movie -> movie.getRating().equals(mpaaRating)));
    }

    private void filterByDirectorName(String directorName) {
        showTable.setItems(movieList.filtered(movie -> movie.getDirector().getName().contains(directorName)));
    }

    @FXML
    public void onViewButtonClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("viewpage.fxml"));
            Stage stage = new Stage();
            stage.setTitle("View");
            ViewController controller = new ViewController();
            this.viewController = controller;
            fxmlLoader.setController(controller);
            Pane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            updateView();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateView() {
        if (viewController != null) {
            short id = 0;
            try {
                id = ConnectionManager.getInstance().newOperation(new Command(CommandType.SHOW, offset));
            } catch (Exception e) {
                return;
            }
            Response response = ConnectionManager.getInstance().waitForResponse(id);
            if (response == null) return;
            ArrayList<Movie> array = (ArrayList<Movie>) Serializer.deserialize(response.getMessage());
            viewController.getCanvasPane().getChildren().clear();
            for (Movie movie : array) {
                Thread thread = new Thread(() -> {
                    short opId;
                    try {
                        opId = ConnectionManager.getInstance().newOperation(new Command(CommandType.SERVICE, "movie_color %d".formatted(movie.getId())));
                    } catch (Exception e) {
                        return;
                    }
                    Response responseWithColor;
                    responseWithColor = ConnectionManager.getInstance().waitForResponse(opId);
                    if (responseWithColor == null) {
                        try {
                            opId = ConnectionManager.getInstance().newOperation(new Command(CommandType.SERVICE, "movie_color %d".formatted(movie.getId())));
                            responseWithColor = ConnectionManager.getInstance().waitForResponse(opId);
                            if (responseWithColor == null) System.err.println("Skipped");
                        } catch (Exception e) {
                            return;
                        }
                    }
                    String responseText = responseWithColor.getStringMessage();
                    Canvas canvas = new ItemCanvas(movie.getId(), responseText);
                    canvas.setOnMouseClicked(event -> {
                        try {
                            WindowManager.getInstance().newShowItemWindow(movie);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
//                    double X = movie.getCoordinates().getX();
//                    double Y = movie.getCoordinates().getY();
                    double X = Math.random() * (viewController.getCanvasPane().getWidth() - canvas.getWidth());
                    double Y = Math.random() * (viewController.getCanvasPane().getHeight() - canvas.getHeight());
                    canvas.relocate(X, Y);
                    canvas.setOpacity(0);
                    Timeline timeline = new Timeline();
                    KeyValue keyValue = new KeyValue(canvas.opacityProperty(), 1);
                    KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), keyValue);
                    timeline.getKeyFrames().add(keyFrame);
                    // Update the canvas list in a thread-safe way using Platform.runLater()
                    Platform.runLater(() -> {
                        viewController.getCanvasPane().getChildren().add(canvas);
                        timeline.play();
                    });
                });
                thread.start();
            }
        }
    }


    public class ShowThread extends Thread {
        public ShowThread() {
            super(() -> {
                System.out.println("Show thread started");
                while (!Thread.interrupted()) {
                    try {
                        short id = ConnectionManager.getInstance().newOperation(new Command(CommandType.SHOW, offset));
                        Response response = ConnectionManager.getInstance().waitForResponse(id);
                        if (response == null) continue;
                        ArrayList<Movie> array = (ArrayList<Movie>) Serializer.deserialize(response.getMessage());
                        movieList = showTable.getItems();
                        if (array == null) continue;
                        movieList.clear();
                        movieList.addAll(array);
                        showTable.refresh();
                        showTable.sort();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("Show thread stopped");
            });
        }
    }
}

