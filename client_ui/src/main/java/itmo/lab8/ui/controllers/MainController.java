package itmo.lab8.ui.controllers;

import itmo.lab8.ClientMain;
import itmo.lab8.basic.utils.files.ScriptExecutor;
import itmo.lab8.basic.utils.serializer.Serializer;
import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandType;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.core.AppCore;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;
import itmo.lab8.ui.Controller;
import itmo.lab8.ui.LocaleManager;
import itmo.lab8.ui.Variable;
import itmo.lab8.ui.WindowManager;
import itmo.lab8.ui.types.CommandButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class MainController extends Controller {
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

    // todo: в команде надо добавить параметр "добавлять в историю команд или нет, т. к. иначе SHOW каждую секунду обновляет список"
    @FXML
    public void initialize() {
        initListView();
        HistoryThread thread = new HistoryThread();
        thread.start();
        ObservableList<CommandButton> items = commandList.getItems();
        for (CommandType type : CommandType.getCommands()) {
            CommandButton label = new CommandButton(type);
            label.setText(LocaleManager.getInstance().getResource(type.name()));
            items.add(label);
        }

        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(FXML.class) && field.getType() == Label.class && !field.isAnnotationPresent(Variable.class)) {
                try {
                    Label label = (Label) field.get(this);
                    label.setText(LocaleManager.getInstance().getResource(field.getName()));
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
                        try {
                            WindowManager.getInstance().newShowWindow();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    case EXECUTE_SCRIPT -> {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Execute Script");
                        File file = fileChooser.showOpenDialog(WindowManager.getInstance().getWindowList().get(0).getStage());
                        ScriptExecutor scriptExecutor = new ScriptExecutor(file);
                        if (file == null) return;
                        ScriptExecutor scrExc = scriptExecutor.readScript();
                        ArrayList<Command> commandsList = scrExc.getCommandList();

                        try {
                            ConnectionManager.getInstance().newOperation(new Command(CommandType.EXECUTE_SCRIPT, commandsList));
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("OK");
                            alert.setHeaderText(null);
                            alert.setContentText("OK");
                            alert.showAndWait();
                        } catch (Exception e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("Connection error...");
                            alert.showAndWait();
                        }
                    }
                    case INSERT -> {
                        try {
                            WindowManager.getInstance().newInsertWindow();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    case LANGUAGE -> {
                        try {
                            WindowManager.getInstance().newLanguageWindow();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    case INFO -> {
                        try {
                            WindowManager.getInstance().newInfoWindow();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    case CLEAR -> {
                        try {
                            short opId = ConnectionManager.getInstance().newOperation(new Command(CommandType.CLEAR));
                            Response response = ConnectionManager.getInstance().waitForResponse(opId);
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                if (response.getType().equals(ResponseType.ERROR)) {
                                    alert.setTitle("Error");
                                    alert.setHeaderText(null);
                                    alert.setContentText(response.getStringMessage());
                                } else {
                                    alert.setTitle("OK");
                                    alert.setHeaderText(null);
                                    alert.setContentText("OK");
                                }
                                alert.showAndWait();
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    case REMOVE_ALL_BY_MPAA_RATING -> {
                        try {
                            WindowManager.getInstance().newRemoveByMpaaWindow();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    case ACCOUNT -> {
                        WindowManager.getInstance().closeAllWindows();
                        AppCore.getInstance().setName(null);
                        try {
                            WindowManager.getInstance().newAuthWindow();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    case REMOVE_GREATER -> {
                        try {
                            WindowManager.getInstance().newRemoveGreaterWindow();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    case REMOVE_KEY -> {
                    }
                    case UPDATE -> {
                        if (isControllerNotPresented(UpdateController.class)) updateHandler();
                    }
                    case REPLACE_IF_LOWER -> {
                        try {
                            WindowManager.getInstance().newReplaceIfLowerWindow();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

//    private void executeScriptHandler(File file) {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("execute_script.fxml"));
//            Stage stage = new Stage();
//            stage.setTitle("Execute Script");
//            ExecuteScriptController controller = new ExecuteScriptController(sceneManager, file, stage);
//            fxmlLoader.setController(controller);
//            controllers.add(controller);
//            Scene scene = new Scene(fxmlLoader.load());
//            stage.setScene(scene);
//            scene.getStylesheets().add(ClientMain.class.getResource("css/execute_script.css").toExternalForm());
//            stage.setOnCloseRequest(event -> {
//                controllers.removeIf(c -> c.getClass().equals(ExecuteScriptController.class));
//            });
//            stage.show();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }


    private void setHistoryList(String[] historyArray) {
        historyList.getItems().clear();

        for (String historyItem : historyArray) {
            historyList.getItems().add(historyItem);
        }
    }

    @Deprecated
    private void updateHandler() {
        // TODO: REMOVE!
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("update.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Update");
            UpdateController controller = new UpdateController();
            fxmlLoader.setController(controller);
            controllers.add(controller);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                controllers.removeIf(c -> c.getClass().equals(UpdateController.class));
            });
            stage.show();
            scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/update.css")).toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class HistoryThread extends Thread {
        public HistoryThread() {
            super(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    short id;
                    try {
                        Command command = new Command(CommandType.HISTORY);
                        id = ConnectionManager.getInstance().newOperation(command);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    Response response = ConnectionManager.getInstance().waitForResponse(id);
                    if (response == null) {
                        continue;
                    }
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
