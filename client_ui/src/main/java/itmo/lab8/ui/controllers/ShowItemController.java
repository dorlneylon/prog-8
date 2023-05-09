package itmo.lab8.ui.controllers;

import itmo.lab8.basic.baseclasses.Coordinates;
import itmo.lab8.basic.baseclasses.Location;
import itmo.lab8.basic.baseclasses.Movie;
import itmo.lab8.basic.baseclasses.Person;
import itmo.lab8.basic.baseenums.Color;
import itmo.lab8.basic.baseenums.MovieGenre;
import itmo.lab8.basic.baseenums.MpaaRating;
import itmo.lab8.commands.CollectionValidator;
import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandType;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.core.AppCore;
import itmo.lab8.shared.Response;
import itmo.lab8.ui.Controller;
import itmo.lab8.ui.LocaleManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import static itmo.lab8.commands.CollectionValidator.checkIfExists;
import static itmo.lab8.commands.CollectionValidator.isUserCreator;

public class ShowItemController extends Controller {
    private final Movie movie;
    @FXML
    private TextField id_insertion_label;
    @FXML
    private Label update_upper_label;
    @FXML
    private Button update_button_label;
    @FXML
    private Label movie_label;
    @FXML
    private TextField name_insertion_label;
    @FXML
    private TextField coords_insertion_label;
    @FXML
    private TextField creation_date_insertion_label;
    @FXML
    private TextField oscars_count_insertion_label;
    @FXML
    private ComboBox<MovieGenre> genre_choicebox;
    @FXML
    private ComboBox<MpaaRating> rating_choicebox;
    @FXML
    private Label director_label;
    @FXML
    private TextField director_name_insertion_label;
    @FXML
    private TextField birthdate_insertion_label;
    @FXML
    private TextField height_insertion_label;
    @FXML
    private TextField location_insertion_label;
    @FXML
    private ComboBox<Color> haircolor_choicebox;
    public ShowItemController(Movie movie) {
        this.movie = movie;
    }

    @FXML
    public void initialize() {
        boolean isUserCreator;
        try {
            isUserCreator = CollectionValidator.isUserCreator(AppCore.getInstance().getName(), movie.getId());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(FXML.class)) continue;
            if (field.getType().equals(Label.class)) {
                try {
                    Label label = (Label) field.get(this);
                    label.setText(LocaleManager.getInstance().getResource(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (field.getType().equals(TextField.class)) {
                try {
                    TextField textField = (TextField) field.get(this);
                    textField.setEditable(isUserCreator);
                    textField.setPromptText(LocaleManager.getInstance().getResource(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        id_insertion_label.setEditable(false);

        id_insertion_label.setText(String.valueOf(movie.getId()));
        name_insertion_label.setText(movie.getName());

        coords_insertion_label.setText(movie.getCoordinates().toString());

        oscars_count_insertion_label.setText(String.valueOf(movie.getOscars()));

        director_name_insertion_label.setText(movie.getDirector().getName());

        height_insertion_label.setText(String.valueOf(movie.getDirector().getHeight()));

        location_insertion_label.setText(movie.getDirector().getLocation().toString());

        genre_choicebox.getItems().addAll(MovieGenre.values());
        genre_choicebox.setValue(movie.getGenre());
        rating_choicebox.getItems().addAll(MpaaRating.values());
        rating_choicebox.setValue(movie.getRating());
        haircolor_choicebox.getItems().addAll(Color.values());
        haircolor_choicebox.setValue(movie.getDirector().getHairColor());

        creation_date_insertion_label.setText(new SimpleDateFormat(LocaleManager.getInstance().getResource("date_pattern")).format(Date.from(movie.getCreationDate().toInstant())));

        birthdate_insertion_label.setText(new SimpleDateFormat(LocaleManager.getInstance().getResource("date_pattern")).format(Date.from(movie.getDirector().getBirthday().toInstant())));
    }

    @FXML
    protected void onUpdateButtonClick() {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
        String id = id_insertion_label.getText();
        String name = name_insertion_label.getText();
        String coords = coords_insertion_label.getText();
        String creationDate = creation_date_insertion_label.getText();
        String oscarsCount = oscars_count_insertion_label.getText();
        MovieGenre genre = genre_choicebox.getValue();
        MpaaRating rating = rating_choicebox.getValue();
        String directorName = director_name_insertion_label.getText();
        String birthdate = birthdate_insertion_label.getText();
        String height = height_insertion_label.getText();
        String location = location_insertion_label.getText();
        Color hairColor = haircolor_choicebox.getValue();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LocaleManager.getInstance().getResource("date_pattern"));
        Coordinates coordinates1 = null;
        Location location1 = null;
        ZonedDateTime date = null;
        Date directorBirthdate = null;

        for (TextField s : Set.of(id_insertion_label, name_insertion_label, coords_insertion_label, creation_date_insertion_label, oscars_count_insertion_label, director_name_insertion_label, birthdate_insertion_label, height_insertion_label, location_insertion_label)) {
            if (s.getText().equals("")) {
                s.getStyleClass().add("empty-textfield");
            } else {
                s.getStyleClass().remove("empty-textfield");
            }
        }

        for (TextField f : Set.of(id_insertion_label, oscars_count_insertion_label, height_insertion_label)) {
            try {
                Integer.parseInt(f.getText());
                f.getStyleClass().remove("empty-textfield");
            } catch (NumberFormatException e) {
                f.getStyleClass().add("empty-textfield");
            }
        }

        try {
            if (checkIfExists(Long.parseLong(id)) || !isUserCreator(name, Long.parseLong(id))) {
                id_insertion_label.getStyleClass().add("empty-textfield");
            } else id_insertion_label.getStyleClass().remove("empty-textfield");
        } catch (Exception e) {
            id_insertion_label.getStyleClass().add("empty-textfield");
        }

        try {
            LocalDate date1 = LocalDate.parse(creationDate, formatter);
            date = date1.atStartOfDay(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.systemDefault());
        } catch (Exception e) {
            creation_date_insertion_label.getStyleClass().add("empty-textfield");
        }

        try {
            directorBirthdate = Date.from(LocalDate.parse(birthdate, formatter).atStartOfDay(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            birthdate_insertion_label.getStyleClass().add("empty-textfield");
        }

        try {
            String coordinates = coords.substring(1, coords.length() - 1);
            String[] coordsSplit = coordinates.split(";");
            coordinates1 = new Coordinates(numberFormat.parse(coordsSplit[0]).floatValue(), numberFormat.parse(coordsSplit[1]).intValue());
            coords_insertion_label.getStyleClass().remove("empty-textfield");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            coords_insertion_label.getStyleClass().add("empty-textfield");
        }

        try {
            String loc = location.substring(1, location.length() - 1);
            String[] numbers = loc.split(";");
            location1 = new Location(numberFormat.parse(numbers[0]).longValue(), numberFormat.parse(numbers[1]).doubleValue(), numberFormat.parse(numbers[2]).doubleValue());
            location_insertion_label.getStyleClass().remove("empty-textfield");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            location_insertion_label.getStyleClass().add("empty-textfield");
        }

        try {
            Movie movie = new Movie(Long.parseLong(id), date, name, coordinates1, Long.parseLong(oscarsCount), genre, rating, new Person(directorName, directorBirthdate, Integer.parseInt(height), hairColor, location1));
            short opID = ConnectionManager.getInstance().newOperation(new Command(CommandType.UPDATE, movie));
            Response response = ConnectionManager.getInstance().waitForResponse(opID);
            System.out.println(new String(response.getMessage()));
            id_insertion_label.getStyleClass().remove("empty-textfield");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
