package itmo.lab8.ui.controllers;

import itmo.lab8.ClientMain;
import itmo.lab8.ui.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    private SceneManager sceneManager;

    public MainController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void onShowButtonClick(ActionEvent event) {
        Parent root;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("showpage.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Show");
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            scene.getStylesheets().add(ClientMain.class.getResource("css/showpage.css").toExternalForm());
            stage.show();
//            ((Node)(event.getSource())).getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
