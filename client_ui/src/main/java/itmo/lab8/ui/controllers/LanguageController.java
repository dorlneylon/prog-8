package itmo.lab8.ui.controllers;

import itmo.lab8.ui.Controller;
import itmo.lab8.ui.LocaleManager;
import itmo.lab8.ui.Window;
import itmo.lab8.ui.WindowManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.Locale;
import java.util.Set;

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
    private Button leetButton;


    @FXML
    public void initialize() {
        updateUi();
        setActive();
    }

    @FXML
    public void onRuButtonClick() {
        LocaleManager.getInstance().updateLocale("ru");
        setActive();
    }

    @FXML
    public void onFinButtonClick() {
        LocaleManager.getInstance().updateLocale("fi");
        setActive();

    }

    @FXML
    public void onEspButtonClick() {
        LocaleManager.getInstance().updateLocale("es");
        setActive();
    }

    @FXML
    public void onCatButtonClick() {
        LocaleManager.getInstance().updateLocale("ca");
        setActive();
    }

    @FXML
    public void onGoBackButtonClick() {
        WindowManager.getInstance().closeWindow(this.getClass());
    }

    @FXML
    public void onLeetButtonClick() {
        LocaleManager.getInstance().updateLocale("le");
        WindowManager.getInstance().getWindowList().forEach(Window::setLeet);
        setActive();
    }

    private void removeActiveButton() {
        for (Button btn : Set.of(ru_lang, fin_lang, esp_lang, cat_lang)) {
            btn.getStyleClass().remove("active-button");
        }
    }

    private void setActive() {
        removeActiveButton();
        switch (Locale.getDefault().getLanguage()) {
            case "ru" -> ru_lang.getStyleClass().add("active-button");
            case "fi" -> fin_lang.getStyleClass().add("active-button");
            case "es" -> esp_lang.getStyleClass().add("active-button");
            case "ca" -> cat_lang.getStyleClass().add("active-button");
        }
    }
}
