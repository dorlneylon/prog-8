package itmo.lab8.ui.controllers;

import itmo.lab8.connection.Authenticator;
import itmo.lab8.core.AppCore;
import itmo.lab8.ui.Controller;
import itmo.lab8.ui.LocaleManager;
import itmo.lab8.ui.WindowManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class AuthController extends Controller {
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label statusLabel;
    @FXML
    private Label login_upper_label;

    @FXML
    protected void onSignUpButtonClick(ActionEvent event) {
        String login = loginField.getText();
        String password = passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            statusLabel.setText(LocaleManager.getInstance().getResource("login_or_password_is_empty"));
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
            });
        });
        task.run();
    }

    @FXML
    protected void onSignInButtonClick(ActionEvent event) {
        String login = loginField.getText();
        String password = passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            statusLabel.setText(LocaleManager.getInstance().getResource("login_or_password_is_empty"));
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
                    statusLabel.setText(LocaleManager.getInstance().getResource("server_is_not_available"));
                    return;
                }
                if (finalStatus) {
                    AppCore.getInstance().setName(login);
                    WindowManager.getInstance().closeWindow(this.getClass());
                    try {
                        WindowManager.getInstance().newMainWindow();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    statusLabel.setText(LocaleManager.getInstance().getResource("login_or_password_is_incorrect"));
                }
            });
        });
        thread.start();
    }

    @FXML
    public void initialize() {
        super.updateUi();
        loginField.setPromptText(LocaleManager.getInstance().getResource("login_input"));
        passwordField.setPromptText(LocaleManager.getInstance().getResource("pass_input"));
        statusLabel.setText("");
        loginField.textProperty().addListener((observable, oldValue, newValue) -> statusLabel.setText(""));
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> statusLabel.setText(""));
    }
}
