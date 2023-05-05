package itmo.lab8.ui.controllers;

import itmo.lab8.ClientMain;
import itmo.lab8.basic.baseclasses.Coordinates;
import itmo.lab8.basic.baseclasses.Location;
import itmo.lab8.basic.baseclasses.Movie;
import itmo.lab8.basic.baseclasses.Person;
import itmo.lab8.basic.baseenums.Color;
import itmo.lab8.basic.baseenums.MovieGenre;
import itmo.lab8.basic.baseenums.MpaaRating;
import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandType;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.core.AppCore;
import itmo.lab8.shared.Response;
import itmo.lab8.ui.SceneManager;
import itmo.lab8.ui.Variable;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
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
    @Variable
    private Label account;
    @FXML
    private Label cur_lang;
    @FXML
    @Variable
    private Label lang;
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
            if (field.isAnnotationPresent(FXML.class) && field.getType() == Label.class && !field.isAnnotationPresent(Variable.class)) {
                try {
                    Label label = (Label) field.get(this);
                    label.setText(resources.getString(field.getName()));
                } catch (IllegalAccessException e) {
                    ClientMain.log.log(System.Logger.Level.ERROR, "Error while initializing labels: " + e.getMessage());
                }
            }
        }
        account.setText(AppCore.getInstance().getName());
        commandList.setItems(items);
    }

    private void initListView() {
        commandList.setCellFactory(param -> {
            ListCell<CommandButton> cell = new ListCell<>() {
                @Override
                protected void updateItem(CommandButton item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!(empty || item == null)) {
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
                    case INSERT -> insertHandler();
                    case EXIT -> System.exit(0);
                }
            }
            commandList.getSelectionModel().clearSelection();
        });
    }

    private void showHandler() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("showpage.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Show");
            ShowController controller = new ShowController(sceneManager);
            fxmlLoader.setController(controller);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/showpage.css")).toExternalForm());
            stage.setOnCloseRequest(event -> {
                controller.getMainThread().interrupt();
            });
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertHandler() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("insert.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Insert");
            fxmlLoader.setController(new InsertController(sceneManager));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/insert.css")).toExternalForm());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

