package itmo.lab8.ui.controllers;

import itmo.lab8.ui.Controller;
import itmo.lab8.ui.LocaleManager;
import itmo.lab8.ui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.Locale;

public class LanguageController extends Controller {
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


    @FXML
    public void initialize() {
        super.initialize();
        setActive();

    }

    @FXML
    public void onRuButtonClick() {
        removeActiveButton();
        LocaleManager.getInstance().updateLocale("ru");
        setActive();
    }

    @FXML
    public void onFinButtonClick() {
        removeActiveButton();
        LocaleManager.getInstance().updateLocale("fi");
        setActive();

    }

    @FXML
    public void onEspButtonClick() {
        removeActiveButton();
        LocaleManager.getInstance().updateLocale("es");
        setActive();
    }

    @FXML
    public void onCatButtonClick() {
        removeActiveButton();
        LocaleManager.getInstance().updateLocale("ca");
        setActive();
    }

    @FXML
    public void onGoBackButtonClick() {
        WindowManager.getInstance().closeWindow(this.getClass());
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
