package itmo.lab8.ui.controllers;

import itmo.lab8.basic.utils.files.FileUtils;
import itmo.lab8.basic.utils.files.ScriptExecutor;
import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandFactory;
import itmo.lab8.commands.CommandType;
import itmo.lab8.commands.CommandUtils;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;
import itmo.lab8.ui.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class ExecuteScriptController {
    private final SceneManager sceneManager;
    private ArrayDeque<File> filesMemory = new ArrayDeque<>();
    private final ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");
    private final File file;
    private final Stage currentStage;
    @FXML
    private Label script_execution_label;
    @FXML
    private ProgressBar script_progressbar;

    public ExecuteScriptController(SceneManager sceneManager, File file, Stage currentStage) {
        this.sceneManager = sceneManager;
        this.file = file;
        this.currentStage = currentStage;
    }

    @FXML
    private void initialize() {
        script_execution_label.setText(resources.getString("script_execution_label"));
        script_progressbar.setProgress(0);

        ScriptExecutor scriptExecutor = new ScriptExecutor(file);
        ScriptExecutor scrExc = scriptExecutor.readScript();
        ArrayList<Command> commandList = scrExc.getCommandList();

        try {
            short opId = ConnectionManager.getInstance().newOperation(new Command(CommandType.EXECUTE_SCRIPT, commandList));
            Response response = ConnectionManager.getInstance().waitForResponse(opId);
            if (response.getType().equals(ResponseType.ERROR)) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(response.getMessage().toString());
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("OK");
                alert.setHeaderText(null);
                alert.setContentText("OK");
                alert.showAndWait();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
