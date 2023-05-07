package itmo.lab8.ui.controllers;

import itmo.lab8.ClientMain;
import itmo.lab8.basic.utils.serializer.Serializer;
import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandType;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.core.AppCore;
import itmo.lab8.shared.Response;
import itmo.lab8.ui.SceneManager;
import itmo.lab8.ui.Variable;
import itmo.lab8.ui.types.CommandButton;
import javafx.application.Platform;
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
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController {

    private final SceneManager sceneManager;
    private final Stage currentStage;
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
    @FXML
    private ListView<String> historyList;

    private final ArrayList<Object> controllers = new ArrayList<>();
    private final ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");

    public MainController(SceneManager sceneManager, Stage currentStage) {
        this.sceneManager = sceneManager;
        this.currentStage = currentStage;
    }

    // todo: в команде надо добавить параметр "добавлять в историю команд или нет, т. к. иначе SHOW каждую секунду обновляет список"
    @FXML
    public void initialize() {
        initListView();
        HistoryThread thread = new HistoryThread();
        thread.start();
        ObservableList<CommandButton> items = commandList.getItems();
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
        lang.setText(Locale.getDefault().getDisplayLanguage());
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

        historyList.setCellFactory(param -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!(empty || item == null)) {
                        setText(item);
                        setStyle("-fx-text-fill: #dadada; -fx-background-color: #363636");
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
                    case SHOW -> {
                        if (isControllerNotPresented(ShowController.class)) showHandler();
                    }
                    case INSERT -> {
                        if (isControllerNotPresented(InsertController.class)) insertHandler();
                    }
                    case LANGUAGE -> {
                        if (isControllerNotPresented(LanguageController.class)) languageHandler();
                    }
                    case ACCOUNT -> {
//                        if (isControllerNotPresented(AccountController.class)) accountHandler();
                    }
                    case REMOVE_KEY -> {
                        if (isControllerNotPresented(RemoveByKeyController.class)) removeKeyHandler();
                    }
                    case EXIT -> System.exit(0);
                }
            }
            commandList.getSelectionModel().clearSelection();
        });
    }

    private boolean isControllerNotPresented(Class<?> instance) {
        for (Object c : controllers) {
            if (c.getClass().equals(instance)) {
                return false;
            }
        }
        return true;
    }


    private void showHandler() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("showpage.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Show");
            ShowController controller = new ShowController(sceneManager);
            fxmlLoader.setController(controller);
            controllers.add(controller);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/showpage.css")).toExternalForm());
            stage.setOnCloseRequest(event -> {
                controllers.stream().filter(c -> c.getClass().equals(ShowController.class)).forEach(c -> ((ShowController) c).getMainThread().interrupt());
                controllers.removeIf(c -> c.getClass().equals(ShowController.class));
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
            InsertController controller = new InsertController(sceneManager);
            fxmlLoader.setController(controller);
            controllers.add(controller);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                controllers.removeIf(c -> c.getClass().equals(InsertController.class));
            });
            scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/insert.css")).toExternalForm());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void languageHandler() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("language.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Language");
            LanguageController controller = new LanguageController(sceneManager, stage);
            fxmlLoader.setController(controller);
            controllers.add(controller);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                controllers.removeIf(c -> c.getClass().equals(LanguageController.class));
            });
            scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/mainpage.css")).toExternalForm());
            stage.show();
            // close the scene
            currentStage.getScene().getWindow().hide();
            controllers.remove(controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setHistoryList(String[] historyArray) {
        historyList.getItems().clear();

        for (String historyItem : historyArray) {
            historyList.getItems().add(historyItem);
        }
    }

    private void removeKeyHandler() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("removepage.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Remove By ID");
            RemoveByKeyController controller = new RemoveByKeyController(sceneManager);
            fxmlLoader.setController(controller);
            controllers.add(controller);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                controllers.removeIf(c -> c.getClass().equals(RemoveByKeyController.class));
            });
            scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/remove.css")).toExternalForm());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class HistoryThread extends Thread {
        public HistoryThread() {
            super(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    short id = 0;
                    try {
                        Command command = new Command(CommandType.HISTORY);
                        id = ConnectionManager.getInstance().newOperation(command);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    Response response = ConnectionManager.getInstance().waitForResponse(id);
                    var otvet = Serializer.deserialize(response.getMessage());
                    String[] clientHistory = (String[]) otvet;
                    Platform.runLater(() -> {
                        setHistoryList(clientHistory);
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
