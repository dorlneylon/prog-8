package itmo.lab8.ui.controllers;

import itmo.lab8.ClientMain;
import itmo.lab8.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageController {
    private final ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");
    private final SceneManager sceneManager;
    private final Stage stage;
    @FXML
    private Label LANGUAGE; // не кнопка, а лейбл над кнопками.
    @FXML
    private Button ru_lang;
    @FXML
    private Button fin_lang;
    @FXML
    private Button esp_lang;
    @FXML
    private Button cat_lang;
    @FXML
    private Button go_back;

    public LanguageController(SceneManager sceneManager, Stage stage) {
        this.sceneManager = sceneManager;
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        setActive();
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
            if (field.getType().equals(Button.class)) {
                try {
                    Button button = (Button) field.get(this);
                    button.setText(resources.getString(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void onRuButtonClick() {
        removeActiveButton();
        Locale.setDefault(new Locale("ru"));
        setActive();
    }

    @FXML
    public void onFinButtonClick() {
        removeActiveButton();
        Locale.setDefault(new Locale("fi"));
        setActive();

    }

    @FXML
    public void onEspButtonClick() {
        removeActiveButton();
        Locale.setDefault(new Locale("es"));
        setActive();
    }

    @FXML
    public void onCatButtonClick() {
        removeActiveButton();
        Locale.setDefault(new Locale("ca"));
        setActive();
    }

    @FXML
    public void onGoBackButtonClick() {
        try {
            Stage currentStage = (Stage) stage.getScene().getWindow();
            currentStage.close();
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("mainpage.fxml"));
            Stage newStage = new Stage();
            fxmlLoader.setController(new MainController(sceneManager, newStage));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(ClientMain.class.getResource("css/mainpage.css").toExternalForm());
            newStage.setTitle("Main page");
            newStage.setScene(scene);
            newStage.setHeight(538);
            newStage.setWidth(635);
            newStage.show();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void removeActiveButton() {
        var currentActiveButton = switch (Locale.getDefault().getLanguage()) {
            case "ru" -> ru_lang;
            case "fi" -> fin_lang;
            case "es" -> esp_lang;
            case "ca" -> cat_lang;
            default -> throw new IllegalStateException("Unexpected value: " + Locale.getDefault().getLanguage());
        };
        currentActiveButton.getStyleClass().remove("active-button");
    }

    private void setActive() {
        switch (Locale.getDefault().getLanguage()) {
            case "ru" -> ru_lang.getStyleClass().add("active-button");
            case "fi" -> fin_lang.getStyleClass().add("active-button");
            case "es" -> esp_lang.getStyleClass().add("active-button");
            case "ca" -> cat_lang.getStyleClass().add("active-button");
        }
    }
}
