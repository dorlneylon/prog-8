package itmo.lab8.ui.controllers;

import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandType;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.shared.Response;
import itmo.lab8.ui.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class InfoController extends Controller {
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


    @FXML
    public void initialize() {
        super.initialize();
        try {
            short opId = ConnectionManager.getInstance().newOperation(new Command(CommandType.SERVICE, "info"));
            Response response = ConnectionManager.getInstance().waitForResponse(opId);
            if (response == null) return;
            String info = new String(response.getMessage());
            String[] infoParts = info.substring(1, info.length() - 1).split(", ");
            key_info.setText(infoParts[0]);
            element_info.setText(infoParts[1]);
            amount_info.setText(infoParts[2]);
            date_info.setText(infoParts[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
