package itmo.lab8.ui.controllers;

import itmo.lab8.basic.baseclasses.Coordinates;
import itmo.lab8.basic.baseclasses.Location;
import itmo.lab8.basic.baseclasses.Movie;
import itmo.lab8.basic.baseclasses.Person;
import itmo.lab8.basic.baseenums.Color;
import itmo.lab8.basic.baseenums.MovieGenre;
import itmo.lab8.basic.baseenums.MpaaRating;
import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandType;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.shared.Response;
import itmo.lab8.ui.Controller;
import itmo.lab8.ui.LocaleManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;

import static itmo.lab8.commands.CollectionValidator.checkIfExists;
import static itmo.lab8.commands.CollectionValidator.isUserCreator;

public class ReplaceIfLowerController extends Controller {

    @FXML
    private TextField id_insertion_label;
    @FXML
    private Label replace_lower_upper_label;
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

    @FXML
    public void initialize() {
        initBoxes();
        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(FXML.class)) continue;

            if (field.getType().equals(Label.class)) {
                try {
                    Label label = (Label) field.get(this);
                    label.setText(LocaleManager.getInstance().getResource(label.getId()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (field.getType().equals(TextField.class)) {
                try {
                    TextField textField = (TextField) field.get(this);
                    textField.setPromptText(LocaleManager.getInstance().getResource(textField.getId()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (field.getType().equals(ComboBox.class)) {
                try {
                    ComboBox<?> comboBox = (ComboBox<?>) field.get(this);
                    comboBox.setPromptText(LocaleManager.getInstance().getResource(comboBox.getId()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (field.getType().equals(Button.class)) {
                try {
                    Button button = (Button) field.get(this);
                    button.setText(LocaleManager.getInstance().getResource(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        String creationDate = creation_date_insertion_label.getPromptText() + " (" + LocaleManager.getInstance().getResource("date_pattern") + ")";
        String birthDate = birthdate_insertion_label.getPromptText() + " (" + LocaleManager.getInstance().getResource("date_pattern") + ")";
        creation_date_insertion_label.setPromptText(creationDate);
        birthdate_insertion_label.setPromptText(birthDate);
    }

    private void initBoxes() {
        genre_choicebox.getItems().addAll(MovieGenre.values());
        rating_choicebox.getItems().addAll(MpaaRating.values());
        haircolor_choicebox.getItems().addAll(Color.values());
    }

    @FXML
    public void onReplaceButtonClick() {
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
            if (!checkIfExists(Long.parseLong(id)) || !isUserCreator(name, Long.parseLong(id))) {
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
            coordinates1 = new Coordinates(Float.parseFloat(coordinates.split(";")[0]), Integer.parseInt(coordinates.split(";")[1]));
            coords_insertion_label.getStyleClass().remove("empty-textfield");
        } catch (Exception e) {
            coords_insertion_label.getStyleClass().add("empty-textfield");
        }

        try {
            String loc = location.substring(1, location.length() - 1);
            location1 = new Location(Long.parseLong(loc.split(";")[0]), Double.parseDouble(loc.split(";")[1]), Double.parseDouble(loc.split(";")[2]));
            location_insertion_label.getStyleClass().remove("empty-textfield");
        } catch (Exception e) {
            location_insertion_label.getStyleClass().add("empty-textfield");
        }

        try {
            short checkId = ConnectionManager.getInstance().newOperation(new Command(CommandType.SERVICE, "get_oscars %d".formatted(Long.parseLong(id))));
            Response response = ConnectionManager.getInstance().waitForResponse(checkId);
            if (Integer.parseInt(new String(response.getMessage())) <= Integer.parseInt(oscarsCount)) {
                oscars_count_insertion_label.getStyleClass().add("empty-textfield");
                return;
            } else oscars_count_insertion_label.getStyleClass().remove("empty-textfield");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Movie movie = new Movie(Long.parseLong(id), date, name, coordinates1, Long.parseLong(oscarsCount), genre, rating, new Person(directorName, directorBirthdate, Integer.parseInt(height), hairColor, location1));
            short opID = ConnectionManager.getInstance().newOperation(new Command(CommandType.UPDATE, movie));
            Response response = ConnectionManager.getInstance().waitForResponse(opID);
            System.out.println(new String(response.getMessage()));
            id_insertion_label.getStyleClass().remove("empty-textfield");
            update_button_label.setText(LocaleManager.getInstance().getResource("success"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
