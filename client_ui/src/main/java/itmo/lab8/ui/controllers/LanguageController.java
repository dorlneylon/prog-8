package itmo.lab8.ui.controllers;

import itmo.lab8.ClientMain;
import itmo.lab8.ui.SceneManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageController {
    private ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");
    private final SceneManager sceneManager;
    private Stage stage;
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
        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(FXML.class)) continue;

            if (field.getType().equals(Label.class)) {
                try {
                    Label label = (Label)field.get(this);
                    label.setText(resources.getString(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (field.getType().equals(Button.class)) {
                try {
                    Button button = (Button)field.get(this);
                    button.setText(resources.getString(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void onRuButtonClick() {
        Locale.setDefault(new Locale("ru"));
    }

    @FXML
    public void onFinButtonClick() {
        Locale newLocale = new Locale("fi");
        Locale.setDefault(newLocale);
    }

    @FXML
    public void onEspButtonClick() {
        Locale.setDefault(new Locale("es"));
    }

    @FXML
    public void onCatButtonClick() {
        Locale.setDefault(new Locale("ca"));
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
            throw new RuntimeException(e);
        }
    }
}
