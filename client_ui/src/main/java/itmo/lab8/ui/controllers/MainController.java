package itmo.lab8.ui.controllers;

import itmo.lab8.ClientMain;
import itmo.lab8.basic.utils.files.ScriptExecutor;
import itmo.lab8.basic.utils.serializer.Serializer;
import itmo.lab8.commands.Command;
import itmo.lab8.commands.CommandType;
import itmo.lab8.connection.Authenticator;
import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.core.AppCore;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;
import itmo.lab8.ui.SceneManager;
import itmo.lab8.ui.Variable;
import itmo.lab8.ui.types.CommandButton;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import java.util.*;

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
                    case EXECUTE_SCRIPT -> {
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Execute Script");
                        File file = fileChooser.showOpenDialog(currentStage);

                        ScriptExecutor scriptExecutor = new ScriptExecutor(file);
                        ScriptExecutor scrExc = scriptExecutor.readScript();
                        ArrayList<Command> commandsList = scrExc.getCommandList();

                        try {
                            short opId = ConnectionManager.getInstance().newOperation(new Command(CommandType.EXECUTE_SCRIPT, commandsList));
                            Response response = ConnectionManager.getInstance().waitForResponse(opId);
                            if (response.getType().equals(ResponseType.ERROR)) {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Error");
                                alert.setHeaderText(null);
                                alert.setContentText(response.getMessage().toString());
                                alert.showAndWait();
                            } else {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("OK");
                                alert.setHeaderText(null);
                                alert.setContentText("OK");
                                alert.showAndWait();
                            }
                        } catch (Exception e) {

                        }
//                        if (isControllerNotPresented(ExecuteScriptController.class) && file != null) executeScriptHandler(file);
                    }
                    case INSERT -> {
                        if (isControllerNotPresented(InsertController.class)) insertHandler();
                    }
                    case LANGUAGE -> {
                        if (isControllerNotPresented(LanguageController.class)) languageHandler();
                    }
                    case INFO -> {
                        if (isControllerNotPresented(InfoController.class)) infoHandler();
                    }
                    case CLEAR -> {
                        try {
                            short opId = ConnectionManager.getInstance().newOperation(new Command(CommandType.CLEAR));

                            Response response = ConnectionManager.getInstance().waitForResponse(opId);
                            Platform.runLater(() -> {
                                if (response.getType().equals(ResponseType.ERROR)) {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Error");
                                    alert.setHeaderText(null);
                                    alert.setContentText(response.getMessage().toString());
                                    alert.showAndWait();
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("OK");
                                    alert.setHeaderText(null);
                                    alert.setContentText("OK");
                                    alert.showAndWait();
                                }
                            });
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                    case REMOVE_ALL_BY_MPAA_RATING -> {
                        if (isControllerNotPresented(RemoveByMpaaController.class)) removeByMpaaHandler();
                    }
                    case ACCOUNT -> accountHandler();
                    case REMOVE_GREATER -> {
                        if (isControllerNotPresented(RemoveGreaterController.class)) removeGreaterHandler();
                    }
                    case REMOVE_KEY -> {
                        if (isControllerNotPresented(RemoveByKeyController.class)) removeKeyHandler();
                    }
                    case UPDATE -> {
                        if (isControllerNotPresented(UpdateController.class)) updateHandler();
                    }
                    case REPLACE_IF_LOWER -> {
                        if (isControllerNotPresented(ReplaceIfLowerController.class)) replaceIfLowerHandler();
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

    private void removeByMpaaHandler() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("remove_mpaa.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Remove by MPAA rating");
            RemoveByMpaaController controller = new RemoveByMpaaController(sceneManager);
            fxmlLoader.setController(controller);
            controllers.add(controller);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            scene.getStylesheets().add(ClientMain.class.getResource("css/remove_mpaa.css").toExternalForm());
            stage.setOnCloseRequest(event -> {
                controllers.removeIf(c -> c.getClass().equals(RemoveByMpaaController.class));
            });
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeScriptHandler(File file) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("execute_script.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Execute Script");
            ExecuteScriptController controller = new ExecuteScriptController(sceneManager, file, stage);
            fxmlLoader.setController(controller);
            controllers.add(controller);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            scene.getStylesheets().add(ClientMain.class.getResource("css/execute_script.css").toExternalForm());
            stage.setOnCloseRequest(event -> {
                controllers.removeIf(c -> c.getClass().equals(ExecuteScriptController.class));
            });
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeGreaterHandler() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("remove_greater.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Remove Greater");
            RemoveGreaterController controller = new RemoveGreaterController(sceneManager);
            fxmlLoader.setController(controller);
            controllers.add(controller);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            scene.getStylesheets().add(ClientMain.class.getResource("css/remove_greater.css").toExternalForm());
            stage.setOnCloseRequest(event -> {
                controllers.removeIf(c -> c.getClass().equals(RemoveGreaterController.class));
            });
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void accountHandler() {
        try {
            currentStage.close();
            Stage stage = new Stage();
            ConnectionManager.getInstance().close();
            ConnectionManager.getInstance().start();
            SceneManager sceneManager = new SceneManager();
            sceneManager.setStage(stage);
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("loginpage.fxml"));
            AuthController authController = new AuthController(sceneManager);
            fxmlLoader.setController(authController);
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/loginpage.css")).toExternalForm());
            sceneManager.showLoginScene();
            stage.setTitle("Authentication");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void replaceIfLowerHandler() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("replacelower.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Replace if lower");
            ReplaceIfLowerController controller = new ReplaceIfLowerController(sceneManager);
            fxmlLoader.setController(controller);
            controllers.add(controller);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                controllers.removeIf(c -> c.getClass().equals(ReplaceIfLowerController.class));
            });
            stage.getScene().getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/replacelower.css")).toExternalForm());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void infoHandler() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("info.fxml"));
            Stage stage = new Stage();
            stage.setTitle("info");
            InfoController controller = new InfoController(sceneManager);
            fxmlLoader.setController(controller);
            controllers.add(controller);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setOnCloseRequest(event -> {
                controllers.removeIf(c -> c.getClass().equals(InfoController.class));
            });
            scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/info.css")).toExternalForm());
            stage.show();
        } catch (Exception e) {
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

    private void updateHandler() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("update.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Update");
            UpdateController controller = new UpdateController(sceneManager);
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
