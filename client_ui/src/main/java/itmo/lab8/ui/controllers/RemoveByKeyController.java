package itmo.lab8.ui.controllers;

import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandFactory;
import itmo.lab8.commands.CommandType;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.core.AppCore;
import itmo.lab8.shared.Response;
import itmo.lab8.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

import static itmo.lab8.commands.CollectionValidator.checkIfExists;
import static itmo.lab8.commands.CollectionValidator.isUserCreator;

public class RemoveByKeyController {
    private final ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");
    private final SceneManager sceneManager;
    @FXML
    private Label remove_label;
    @FXML
    private TextField movie_id;
    @FXML
    private Button remove_button;


    public RemoveByKeyController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
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

    public void onRemoveButtonClick() {
        try {
            Long id = Long.parseLong(movie_id.getText());
            movie_id.getStyleClass().remove("empty-textfield");
            if (!checkIfExists(id) || !isUserCreator(CommandFactory.getName(), id)) throw new Exception();
            ConnectionManager.getInstance().newOperation(new Command(CommandType.REMOVE_KEY, id));
        } catch (Exception e) {
            movie_id.getStyleClass().add("empty-textfield");
        }
    }
}
