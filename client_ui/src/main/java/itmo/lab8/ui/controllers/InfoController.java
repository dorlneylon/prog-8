package itmo.lab8.ui.controllers;

import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandType;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.shared.Response;
import itmo.lab8.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

public class InfoController {
    private final SceneManager sceneManager;
    private ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");
    @FXML
    private Label key_info_label;
    @FXML
    private Label element_info_label;
    @FXML
    private Label amount_info_label;
    @FXML
    private Label date_info_label;
    @FXML
    private Label key_info;
    @FXML
    private Label element_info;
    @FXML
    private Label amount_info;
    @FXML
    private Label date_info;


    public InfoController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(FXML.class) || !field.getName().contains("label")) continue;
            try {
                Label label = (Label) field.get(this);
                label.setText(resources.getString(field.getName()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            short opId = ConnectionManager.getInstance().newOperation(new Command(CommandType.SERVICE, "info"));
            Response response = ConnectionManager.getInstance().waitForResponse(opId);
            String info = new String(response.getMessage());
            String[] infoParts = info.substring(1, info.length()-1).split(", ");
            key_info.setText(infoParts[0]);
            element_info.setText(infoParts[1]);
            amount_info.setText(infoParts[2]);
            date_info.setText(infoParts[3]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
