package itmo.lab8.ui.controllers;

import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandFactory;
import itmo.lab8.commands.CommandType;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.ui.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import static itmo.lab8.commands.CollectionValidator.checkIfExists;
import static itmo.lab8.commands.CollectionValidator.isUserCreator;

public class RemoveByKeyController extends Controller {
    @FXML
    private Label remove_label;
    @FXML
    private TextField movie_id;
    @FXML
    private Button remove_button;


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
