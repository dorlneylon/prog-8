package itmo.lab8.ui.controllers;

import itmo.lab8.ui.Controller;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class ViewController extends Controller {

    @FXML
    private Pane canvasPane;

    public Pane getCanvasPane() {
        return canvasPane;
    }

}
