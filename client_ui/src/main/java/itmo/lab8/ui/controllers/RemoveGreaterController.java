package itmo.lab8.ui.controllers;

import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandType;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.ui.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RemoveGreaterController extends Controller {
    @FXML
    private Label remove_greater_label;
    @FXML
    private TextField movie_id;
    @FXML
    private Button remove_button;

    public void onRemoveButtonClick() {
        try {
            Long oscars = Long.parseLong(movie_id.getText());
            ConnectionManager.getInstance().newOperation(new Command(CommandType.REMOVE_GREATER, oscars));
        } catch (Exception ignored) {
        }
    }
}
