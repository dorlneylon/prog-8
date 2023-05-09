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
import itmo.lab8.ui.types.FilterOption;
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

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

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
    }

    public void previousPage() {
        if (this.offset > 0) {
            this.offset -= 20;
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
        creationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreationDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault()))));
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
                    showMovieRow(movie);
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

    private void setMovieList(ObservableList<Movie> movieList) {
        this.movieList = movieList;
        showTable.setItems(movieList);
    }

    private void updateMovieList() {
        showTable.setItems(movieList);
        showTable.sort();
    }

    private void addMovie(Movie movie) {
        movieList.add(movie);
        updateMovieList();
    }

    private void removeMovie(Movie movie) {
        movieList.remove(movie);
        updateMovieList();
    }

    private void updateMovie(Movie movie) {
        movieList.removeIf(m -> Objects.equals(m.getId(), movie.getId()));
        movieList.add(movie);
        updateMovieList();
    }

    public TableView<Movie> getShowTable() {
        return showTable;
    }

    private void showMovieRow(Movie movie) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("itemshow.fxml"));
            Stage stage = new Stage();
            stage.setTitle(movie.getName());
            ShowItemController controller = new ShowItemController(movie);
            fxmlLoader.setController(controller);
            Pane root = fxmlLoader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/insert.css")).toExternalForm());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            synchronized (movieList) {
                for (Movie movie : movieList) {
                    short opId;
                    try {
                        opId = ConnectionManager.getInstance().newOperation(new Command(CommandType.SERVICE, "movie_color %d".formatted(movie.getId())));
                    } catch (Exception e) {
                        continue;
                    }
                    Response response = ConnectionManager.getInstance().waitForResponse(opId);
                    String responseText = response.getStringMessage();
                    Canvas canvas = new ItemCanvas(movie.getId(), responseText);
                    double randomX = Math.random() * (617 - 50);
                    double randomY = Math.random() * (995 - 50);
                    canvas.relocate(randomX, randomY);
                    viewController.getCanvasPane().getChildren().add(canvas);
                }
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
                        var items = showTable.getItems();
                        movieList = showTable.getItems();
                        if (array == null) continue;
                        movieList.clear();
                        movieList.addAll(array);
                        showTable.refresh();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("Show thread stopped");
            });
        }
    }
}

