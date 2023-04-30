package itmo.lab8.ui.controllers;

import itmo.lab8.basic.baseclasses.Coordinates;
import itmo.lab8.basic.baseclasses.Movie;
import itmo.lab8.basic.baseenums.MovieGenre;
import itmo.lab8.basic.baseenums.MpaaRating;
import itmo.lab8.ui.SceneManager;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class ShowController {
    private final ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");

    @FXML
    private Label filter_by_label;
    @FXML
    private ChoiceBox filter_options;
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

    private final SceneManager sceneManager;

    public ShowController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        fieldInit();
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(Math.toIntExact(cellData.getValue().getId())).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        coordinatesColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getCoordinates()));
        creationDateColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCreationDate()
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault()))));
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
                    setText(String.format(Locale.getDefault(), "(%.2f, %.2f)", coordinates.getX(), coordinates.getY()));
                }
            }
        });
    }

    public void fieldInit() {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(FXML.class) && field.getType().equals(Label.class)) {
                try {
                    field.set(this, resources.getString(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void filterById(int id) {
        showTable.setItems(movieList.filtered(movie -> movie.getId() > id));
    }

    public void filterByName(String name) {
        showTable.setItems(movieList.filtered(movie -> movie.getName().contains(name)));
    }

    public void filterByCoordinates(Coordinates coordinates) {
        showTable.setItems(movieList.filtered(movie -> movie.getCoordinates().equals(coordinates)));
    }

    public void filterByCreationDate(String creationDate) {
        showTable.setItems(movieList.filtered(movie -> movie.getCreationDate().equals(creationDate)));
    }

    public void filterByOscars(int oscars) {
        showTable.setItems(movieList.filtered(movie -> movie.getOscarsInt() > oscars));
    }

    public void filterByGenre(MovieGenre genre) {
        showTable.setItems(movieList.filtered(movie -> movie.getGenre().equals(genre)));
    }

    public void filterByMpaaRating(MpaaRating mpaaRating) {
        showTable.setItems(movieList.filtered(movie -> movie.getRating().equals(mpaaRating)));
    }

    public void filterByDirectorName(String directorName) {
        showTable.setItems(movieList.filtered(movie -> movie.getDirector().getName().contains(directorName)));
    }

    public void setMovieList(ObservableList<Movie> movieList) {
        this.movieList = movieList;
        showTable.setItems(movieList);
    }

    private void updateMovieList() {
        showTable.setItems(movieList);
    }

    public void addMovie(Movie movie) {
        movieList.add(movie);
        updateMovieList();
    }

    public void removeMovie(Movie movie) {
        movieList.remove(movie);
        updateMovieList();
    }

    public void updateMovie(Movie movie) {
        movieList.removeIf(m -> m.getId() == movie.getId());
        movieList.add(movie);
        updateMovieList();
    }

    public TableView<Movie> getShowTable() {
        return showTable;
    }
}

