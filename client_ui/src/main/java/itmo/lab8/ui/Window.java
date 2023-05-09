package itmo.lab8.ui;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class Window {
    private final Stage stage;
    private final Scene scene;

    private final Controller controller;

    public Window(Stage stage, Scene scene, Controller controller) {
        this.stage = stage;
        this.scene = scene;
        this.controller = controller;
    }

    public Stage getStage() {
        return stage;
    }

    public Scene getScene() {
        return scene;
    }

    public Controller getController() {
        return controller;
    }

    public void updateLocale() {
        this.controller.updateUi();
    }

    public void close() {
        controller.close();
        stage.close();
    }
}
