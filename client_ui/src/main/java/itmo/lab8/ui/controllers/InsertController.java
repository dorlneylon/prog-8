package itmo.lab8.ui.controllers;

import itmo.lab8.basic.baseenums.Color;
import itmo.lab8.basic.baseenums.MovieGenre;
import itmo.lab8.basic.baseenums.MpaaRating;
import itmo.lab8.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

public class InsertController {
    private final ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");
    private final SceneManager sceneManager;

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
        }
    }

    private void initChoiceBoxes() {
        genre_choicebox.getItems().addAll(MovieGenre.values());
        genre_choicebox.setValue(MovieGenre.ACTION);
        rating_choicebox.getItems().addAll(MpaaRating.values());
        rating_choicebox.setValue(MpaaRating.PG);
        haircolor_choicebox.getItems().addAll(Color.values());
        haircolor_choicebox.setValue(Color.BLACK);
    }
}
