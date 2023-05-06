package itmo.lab8.ui;

import itmo.lab8.ClientMain;
import itmo.lab8.ui.controllers.AuthController;
import itmo.lab8.ui.controllers.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    public void showLoginScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("loginpage.fxml"));
        fxmlLoader.setController(new AuthController(this));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(ClientMain.class.getResource("css/loginpage.css").toExternalForm());
        stage.setScene(scene);
    }

    public void showMainScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("mainpage.fxml"));
        fxmlLoader.setController(new MainController(this, stage));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(ClientMain.class.getResource("css/mainpage.css").toExternalForm());
        stage.setTitle("Main page");
        stage.setScene(scene);
        stage.setHeight(538);
        stage.setWidth(635);
    }
}
