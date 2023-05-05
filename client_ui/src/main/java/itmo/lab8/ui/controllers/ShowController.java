package itmo.lab8.ui.controllers;

import itmo.lab8.basic.baseclasses.Coordinates;
import itmo.lab8.basic.baseclasses.Movie;
import itmo.lab8.basic.baseenums.MovieGenre;
import itmo.lab8.basic.baseenums.MpaaRating;
import itmo.lab8.basic.utils.serializer.Serializer;
import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandType;
import itmo.lab8.shared.Response;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.ui.SceneManager;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class ShowController {
    private final ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");

    @FXML
    private Label filter_by_label;
    @FXML
    private ComboBox<String> filter_options;
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
    private ShowThread showThread;

    public ShowThread getMainThread() {
        return showThread;
    }

    public ShowController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        fieldInit();
        showTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        showThread = new ShowThread();
        showThread.start();
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

        filter_options.getItems().addAll(
                resources.getString("none_filter_option"),
                resources.getString("id_filter_option"),
                resources.getString("name_filter_option"),
                resources.getString("coordinates_filter_option"),
                resources.getString("creation_date_filter_option"),
                resources.getString("oscars_filter_option"),
                resources.getString("genre_filter_option"),
                resources.getString("mpaa_rating_filter_option"),
                resources.getString("director_name_filter_option")
        );
    }

    public void fieldInit() {
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(FXML.class) && field.getType().equals(Label.class)) {
                try {
                    Label label = new Label(resources.getString(field.getName()));
                    field.set(this, label);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void onFilterButtonClick() {
        // take filter option from combobox and filter criteria from textfield. try to parse the type of filter criteria and hang to other method.
        String filterOption = filter_options.getValue();
        String filterCriteria = filter_criteria.getText();

        if (filterOption == null || filterCriteria == null) {
            return;
        }

        if (filterOption.equals(resources.getString("id_filter_option"))) {
            try {
                int id = Integer.parseInt(filterCriteria);
                filterById(id);
            } catch (NumberFormatException e) {
                // smt
            }
        }

        if (filterOption.equals(resources.getString("name_filter_option"))) {
            filterByName(filterCriteria);
        }

        if (filterOption.equals(resources.getString("coordinates_filter_option"))) {
            try {
                String[] coordinates = filterCriteria.split(" ");
                float x = Float.parseFloat(coordinates[0]);
                Integer y = Integer.parseInt(coordinates[1]);
                filterByCoordinates(new Coordinates(x, y));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                // smt
            }
        }

        if (filterOption.equals(resources.getString("creation_date_filter_option"))) {
            filterByCreationDate(filterCriteria);
        }

        if (filterOption.equals(resources.getString("oscars_filter_option"))) {
            try {
                int oscars = Integer.parseInt(filterCriteria);
                filterByOscars(oscars);
            } catch (NumberFormatException e) {
                // smt
            }
        }

        if (filterOption.equals(resources.getString("genre_filter_option"))) {
            try {
                MovieGenre genre = MovieGenre.valueOf(filterCriteria.toUpperCase());
                filterByGenre(genre);
            } catch (IllegalArgumentException e) {
                // smt
            }
        }

        if (filterOption.equals(resources.getString("mpaa_rating_filter_option"))) {
            try {
                MpaaRating mpaaRating = MpaaRating.valueOf(filterCriteria.toUpperCase());
                filterByMpaaRating(mpaaRating);
            } catch (IllegalArgumentException e) {
                // smt
            }
        }

        if (filterOption.equals(resources.getString("director_name_filter_option"))) {
            filterByDirectorName(filterCriteria);
        }

        if (filterOption.equals(resources.getString("none_filter_option"))) {
            showTable.setItems(movieList);
            showThread.start();
            return;
        }

        showThread.stop();
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

    public class ShowThread extends Thread {
        public ShowThread() {
            super(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        short id = ConnectionManager.getInstance().newOperation(new Command(CommandType.SHOW, 0));
                        Response response = ConnectionManager.getInstance().waitForResponse(id);
                        ArrayList<Movie> array = (ArrayList<Movie>) Serializer.deserialize(response.getMessage());
                        var items = showTable.getItems();
                        movieList = showTable.getItems();
                        if (array == null) continue;
                        if (items.size() == 0) {
                            items.addAll(array);
                        } else {
                            for (Movie movie : array) {
                                boolean flag = false;
                                for (int i = 0; i < items.size(); i++) {
                                    if (movie.getId().equals(items.get(i).getId())) {
                                        flag = true;
                                        if (!movie.equals(items.get(i))) {
                                            items.set(i, movie);
                                            movieList.set(i, movie);
                                        }
                                        break;
                                    }
                                }
                                if (!flag) {
                                    items.add(movie);
                                    movieList.add(movie);
                                }
                            }
                        }
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
                System.out.println("Thread is interrupted");
            });
        }
    }
}

