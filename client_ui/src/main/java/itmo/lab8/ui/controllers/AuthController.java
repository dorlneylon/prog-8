package itmo.lab8.ui.controllers;

import itmo.lab8.connection.Authenticator;
import itmo.lab8.core.AppCore;
import itmo.lab8.ui.SceneManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ResourceBundle;

public class AuthController {
    private final ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label statusLabel;
    @FXML
    private Label login_upper_label;

    private final SceneManager sceneManager;

    public AuthController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    protected void onSignUpButtonClick(ActionEvent event) {
        String login = loginField.getText();
        String password = passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            statusLabel.setText(resources.getString("login_or_password_is_empty"));
            return;
        }
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return Authenticator.register(login, password);
            }
        };
        task.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                boolean status = task.getValue();
                if (status) {
                    try {
                        sceneManager.showMainScene();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    statusLabel.setText(resources.getString("login_is_already_taken"));
                }
            });
        });
        task.run();
    }

    @FXML
    protected void onSignInButtonClick(ActionEvent event) {
        String login = loginField.getText();
        String password = passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            statusLabel.setText(resources.getString("login_or_password_is_empty"));
            return;
        }
        Thread thread = new Thread(() -> {
            // Run the long-running operation in the background thread
            Boolean status;
            try {
                status = Authenticator.login(login, password);
            } catch (SocketTimeoutException ste) {
                status = null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Update the UI using Platform.runLater()
            Boolean finalStatus = status;
            Platform.runLater(() -> {
                if (finalStatus == null) {
                    statusLabel.setText(resources.getString("server_is_not_available"));
                    return;
                }
                if (finalStatus) {
                    try {
                        AppCore.getInstance().setName(login);
                        sceneManager.showMainScene();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    statusLabel.setText(resources.getString("login_or_password_is_incorrect"));
                }
            });
        });

        thread.start();
    }

    @FXML
    public void initialize() {
        loginField.setPromptText(resources.getString("login_input"));
        passwordField.setPromptText(resources.getString("pass_input"));
        login_upper_label.setText(resources.getString("login_upper_label"));
        loginField.textProperty().addListener((observable, oldValue, newValue) -> statusLabel.setText(""));
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> statusLabel.setText(""));
    }
}
