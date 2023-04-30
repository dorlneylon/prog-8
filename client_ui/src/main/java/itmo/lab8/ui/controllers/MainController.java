package itmo.lab8.ui.controllers;

import itmo.lab8.ClientMain;
import itmo.lab8.commands.CommandType;
import itmo.lab8.ui.SceneManager;
import itmo.lab8.ui.types.CommandButton;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController {

    private final SceneManager sceneManager;
    @FXML
    private ListView<CommandButton> commandList;
    @FXML
    private Label details;
    @FXML
    private Label commands_list;
    @FXML
    private Label cur_account;
    @FXML
    private Label cur_lang;
    @FXML
    private Label history;

    public MainController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    public void initialize() {
        initListView();
        ObservableList<CommandButton> items = commandList.getItems();
        ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainpage.fxml"), resources);
        for (CommandType type : CommandType.getCommands()) {
            CommandButton label = new CommandButton(type);
            label.setText(resources.getString(type.name()));
            items.add(label);
        }
        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(FXML.class) && field.getType() == Label.class) {
                try {
                    Label label = (Label) field.get(this);
                    label.setText(resources.getString(field.getName()));
                    String labelText = resources.getString(field.getName());
                    label.setText(labelText);
                } catch (IllegalAccessException e) {
                    ClientMain.log.log(System.Logger.Level.ERROR, "Error while initializing labels: " + e.getMessage());
                }
            }
        }
        commandList.setItems(items);
    }

    private void initListView() {
        commandList.setCellFactory(param -> {
            ListCell<CommandButton> cell = new ListCell<>() {
                @Override
                protected void updateItem(CommandButton item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item.getText());
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });

        commandList.setOnMouseClicked(mouseEvent -> {
            int index = commandList.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                CommandButton clickedBtn = commandList.getItems().get(index);
                switch (clickedBtn.getType()) {
                    case SHOW -> showHandler();
                    case INSERT -> Locale.setDefault(new Locale("en"));
                    case EXIT -> System.exit(0);
                }
            }
        });
    }

    private void showHandler() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("showpage.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Show");
            fxmlLoader.setController(new ShowController(sceneManager));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            scene.getStylesheets().add(ClientMain.class.getResource("css/showpage.css").toExternalForm());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

