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
import javafx.scene.layout.VBox;
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
    private VBox mainBox;
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

    private HistoryThread thread;

    @FXML
    public void initialize() {
        initListView();
        thread = new HistoryThread();
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

    @Override
    public void updateUi() {
        super.updateUi();
        for (CommandButton button : commandList.getItems()) {
            button.setText(LocaleManager.getInstance().getResource(button.getType().name()));
        }
        commandList.refresh();
        lang.setText(Locale.getDefault().getDisplayLanguage());
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
                        getStyleClass().add("history-item");
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
                            System.out.println(response.getStringMessage());
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
                        try {
                            WindowManager.getInstance().newRemoveByKeyWindow();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    case UPDATE -> {}
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

    private void setHistoryList(String[] historyArray) {
        historyList.getItems().clear();

        for (String historyItem : historyArray) {
            historyList.getItems().add(historyItem);
        }
    }

//    @Deprecated
//    private void updateHandler() {
//        try {
//            FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("update.fxml"));
//            Stage stage = new Stage();
//            stage.setTitle("Update");
//            UpdateController controller = new UpdateController();
//            fxmlLoader.setController(controller);
//            Scene scene = new Scene(fxmlLoader.load());
//            stage.setScene(scene);
//            stage.show();
//            scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/update.css")).toExternalForm());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void close() {
        thread.interrupt();
    }

    public class HistoryThread extends Thread {
        public HistoryThread() {
            super(() -> {
                System.out.println("History thread started");
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
                    var responseArray = Serializer.deserialize(response.getMessage());
                    String[] clientHistory = (String[]) responseArray;
                    Platform.runLater(() -> {
                        setHistoryList(clientHistory);
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("History thread stopped");
            });
        }
    }
}
