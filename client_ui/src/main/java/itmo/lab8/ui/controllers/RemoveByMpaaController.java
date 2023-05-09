package itmo.lab8.ui.controllers;

import itmo.lab8.basic.baseenums.MpaaRating;
import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandType;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;
import itmo.lab8.ui.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

public class RemoveByMpaaController extends Controller {
    private final ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");
    @FXML
    private Label choose_mpaa_label;
    @FXML
    private ComboBox<MpaaRating> combobox_mpaa;
    @FXML
    private Button remove_mpaa_button;


    @FXML
    public void initialize() {
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
        combobox_mpaa.getItems().addAll(MpaaRating.values());
    }

    @FXML
    public void onRemoveButtonClick() {
        try {
            MpaaRating rate = combobox_mpaa.getValue();
            short opId = ConnectionManager.getInstance().newOperation(new Command(CommandType.REMOVE_ALL_BY_MPAA_RATING, rate));
            Response response = ConnectionManager.getInstance().waitForResponse(opId);
            if (response.getType().equals(ResponseType.OK)) {
                remove_mpaa_button.setText(resources.getString("success"));
            } else {
                remove_mpaa_button.setText(resources.getString("failure"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
