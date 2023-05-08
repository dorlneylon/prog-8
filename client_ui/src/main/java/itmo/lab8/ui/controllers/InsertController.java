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
import itmo.lab8.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.Set;

import static itmo.lab8.commands.CollectionValidator.checkIfExists;

public class InsertController {
    private final ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");
    private final SceneManager sceneManager;

    @FXML
    private TextField id_insertion_label;
    @FXML
    private Button insert_button_label;
    @FXML
    private Label insert_upper_label;
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

    public InsertController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        initChoiceBoxes();
        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(FXML.class)) continue;
            if (field.getType().equals(Label.class)) {
                try {
                    Label label = (Label) field.get(this);
                    label.setText(resources.getString(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (field.getType().equals(TextField.class)) {
                try {
                    TextField textField = (TextField) field.get(this);
                    textField.setPromptText(resources.getString(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (field.getType().equals(Button.class)) {
                try {
                    Button button = (Button) field.get(this);
                    button.setText(resources.getString(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (field.getType().equals(ComboBox.class)) {
                try {
                    ComboBox comboBox = (ComboBox) field.get(this);
                    comboBox.setPromptText(resources.getString(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initChoiceBoxes() {
        genre_choicebox.getItems().addAll(MovieGenre.values());
        rating_choicebox.getItems().addAll(MpaaRating.values());
        haircolor_choicebox.getItems().addAll(Color.values());
    }

    @FXML
    protected void onInsertButtonClick() {
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
        Coordinates coordinates1 = null;
        Location location1 = null;

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
            if (checkIfExists(Long.parseLong(id))) {
                id_insertion_label.getStyleClass().add("empty-textfield");
            } else id_insertion_label.getStyleClass().remove("empty-textfield");
        } catch (Exception e) {
            id_insertion_label.getStyleClass().add("empty-textfield");
        }

        for (TextField f : Set.of(creation_date_insertion_label, birthdate_insertion_label)) {
            try {
                new SimpleDateFormat("dd-MM-yyyy").parse(f.getText());
                f.getStyleClass().remove("empty-textfield");
            } catch (Exception e) {
                f.getStyleClass().add("empty-textfield");
            }
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
            Movie movie = new Movie(Long.parseLong(id), name, coordinates1, Long.parseLong(oscarsCount), genre, rating, new Person(directorName, new SimpleDateFormat("dd-mm-yyyy").parse(birthdate), Integer.parseInt(height), hairColor, location1));
            short opID = ConnectionManager.getInstance().newOperation(new Command(CommandType.INSERT, movie));
            Response response = ConnectionManager.getInstance().waitForResponse(opID);
            System.out.println(new String(response.getMessage()));
            id_insertion_label.getStyleClass().remove("empty-textfield");
            insert_button_label.setText(resources.getString("success"));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
