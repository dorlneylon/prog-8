package itmo.lab8.ui.controllers;

import itmo.lab8.connection.Authenticator;
import itmo.lab8.ui.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AuthController {
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;

    private SceneManager sceneManager;

    public AuthController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    protected void onSignUpButtonClick(ActionEvent event) throws Exception {
        boolean success = Authenticator.register(loginField.getText(), passwordField.getText(), sceneManager.getCore().getConnector());
        if (success) sceneManager.showMainScene();
        // TODO: glow red the fields if not success
    }

    @FXML
    protected void onSignInButtonClick(ActionEvent event) throws Exception {
        boolean success = Authenticator.login(loginField.getText(), passwordField.getText(), sceneManager.getCore().getConnector());
        if (success) sceneManager.showMainScene();
        // TODO: glow red the fields if not success
    }
}
