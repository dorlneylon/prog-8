package itmo.lab8.ui;

import itmo.lab8.ClientMain;
import itmo.lab8.ui.controllers.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class WindowManager {
    private static WindowManager instance;
    private final ArrayList<Window> windowList = new ArrayList<>();


    public static WindowManager getInstance() {
        if (instance == null) {
            instance = new WindowManager();
        }
        return instance;
    }

    public ArrayList<Window> getWindowList() {
        return windowList;
    }

    /**
     * Creates a new main window.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public void newMainWindow() throws IOException {
        if (isWindowOpen(MainController.class)) {
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("mainpage.fxml"));
        MainController mainController = new MainController();
        fxmlLoader.setController(mainController);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/mainpage.css")).toExternalForm());
        Stage stage = new Stage();
        Window window = new Window(stage, scene, mainController);
        stage.setTitle("Main page");
        stage.setScene(scene);
        stage.setHeight(538);
        stage.setWidth(635);
        windowList.add(window);
        stage.setOnCloseRequest(event -> {
            closeAllWindows();
            System.exit(0);
        });
        stage.show();
    }

    /**
     * Creates a new authentication window.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public void newAuthWindow() throws IOException {
        if (isWindowOpen(AuthController.class)) {
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("loginpage.fxml"));
        AuthController authController = new AuthController();
        fxmlLoader.setController(authController);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/loginpage.css")).toExternalForm());
        Stage stage = new Stage();
        Window window = new Window(stage, scene, authController);
        stage.setTitle("Authentication");
        stage.setScene(scene);
        stage.setResizable(false);
        windowList.add(window);
        stage.setOnCloseRequest(event -> {
            windowList.remove(window);
        });
        stage.show();
    }

    /**
     * Creates a new window for the show page.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public void newShowWindow() throws IOException {
        if (isWindowOpen(ShowController.class)) {
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("showpage.fxml"));
        ShowController controller = new ShowController();
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/showpage.css")).toExternalForm());
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.N) {
                controller.nextPage();
            }
            if (event.getCode() == KeyCode.P) {
                controller.previousPage();
            }
        });
        Stage stage = new Stage();
        Window window = new Window(stage, scene, controller);
        stage.setTitle("Show");
        stage.setScene(scene);
        stage.setResizable(false);
        windowList.add(window);
        stage.setOnCloseRequest(event -> {
            window.close();
            windowList.remove(window);
        });
        stage.show();
    }

    /**
     * Creates a new window for the InsertController.
     *
     * @throws IOException if the insert.fxml file cannot be found
     */
    public void newInsertWindow() throws IOException {
        if (isWindowOpen(InsertController.class)) {
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("insert.fxml"));
        InsertController controller = new InsertController();
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/insert.css")).toExternalForm());
        Stage stage = new Stage();
        Window window = new Window(stage, scene, controller);
        stage.setTitle("Insert");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            windowList.remove(window);
        });
        windowList.add(window);
        stage.show();
    }

    /**
     * Opens a new window for the user to select a language.
     *
     * @throws IOException if the language.fxml file cannot be found
     */
    public void newLanguageWindow() throws IOException {
        if (isWindowOpen(InsertController.class)) {
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("language.fxml"));
        LanguageController controller = new LanguageController();
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/mainpage.css")).toExternalForm());
        Stage stage = new Stage();
        Window window = new Window(stage, scene, controller);
        stage.setTitle("Language");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            windowList.remove(window);
        });
        windowList.add(window);
        stage.show();
    }

    /**
     * Creates a new window for displaying information.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public void newInfoWindow() throws IOException {
        if (isWindowOpen(InfoController.class)) {
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("info.fxml"));
        InfoController controller = new InfoController();
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/info.css")).toExternalForm());
        Stage stage = new Stage();
        stage.setTitle("info");
        Window window = new Window(stage, scene, controller);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            windowList.remove(window);
        });
        windowList.add(window);
        stage.show();
    }

    /**
     * Opens a new window for removing movies by MPAA rating.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public void newRemoveByMpaaWindow() throws IOException {
        if (isWindowOpen(RemoveByMpaaController.class)) {
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("remove_mpaa.fxml"));
        RemoveByMpaaController controller = new RemoveByMpaaController();
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/remove_mpaa.css")).toExternalForm());
        Stage stage = new Stage();
        stage.setTitle("Remove by MPAA rating");
        Window window = new Window(stage, scene, controller);
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            windowList.remove(window);
        });
        windowList.add(window);
        stage.show();
    }

    /**
     * Opens a new window for the Remove Greater command.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public void newRemoveGreaterWindow() throws IOException {
        if (isWindowOpen(RemoveGreaterController.class)) {
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("remove_greater.fxml"));
        RemoveGreaterController controller = new RemoveGreaterController();
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/remove_greater.css")).toExternalForm());
        Stage stage = new Stage();
        Window window = new Window(stage, scene, controller);
        stage.setTitle("Remove Greater");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            windowList.remove(window);
        });
        windowList.add(window);
        stage.show();
    }

    /**
     * Creates a new window for the Remove By Key page.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public void newRemoveByKeyWindow() throws IOException {
        if (isWindowOpen(RemoveByKeyController.class)) {
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("removepage.fxml"));
        RemoveByKeyController controller = new RemoveByKeyController();
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/remove.css")).toExternalForm());
        Stage stage = new Stage();
        Window window = new Window(stage, scene, controller);
        stage.setTitle("Remove By ID");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            windowList.remove(window);
        });
        windowList.add(window);
        stage.show();
    }

    /**
     * Opens a new window for the Replace if lower function.
     *
     * @throws IOException if the FXML file cannot be loaded
     */
    public void newReplaceIfLowerWindow() throws IOException {
        if (isWindowOpen(ReplaceIfLowerController.class)) {
            return;
        }
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("replacelower.fxml"));
        ReplaceIfLowerController controller = new ReplaceIfLowerController();
        fxmlLoader.setController(controller);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(Objects.requireNonNull(ClientMain.class.getResource("css/replacelower.css")).toExternalForm());
        Stage stage = new Stage();
        Window window = new Window(stage, scene, controller);
        stage.setTitle("Replace if lower");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> {
            windowList.remove(window);
        });
        windowList.add(window);
        stage.show();
    }

    /**
     * Closes a window based on the class of its controller.
     *
     * @param clazz The class of the controller associated with the window to be closed.
     */
    public void closeWindow(Class<? extends Controller> clazz) {
        for (Window window : windowList) {
            if (window.getController().getClass().equals(clazz)) {
                window.close();
            }
        }
    }


    /**
     * Checks if a window is open with the given controller class.
     *
     * @param controllerClass The controller class to check for.
     * @return True if a window is open with the given controller class, false otherwise.
     */
    public boolean isWindowOpen(Class<? extends Controller> controllerClass) {
        for (Window window : windowList) {
            if (window.getController().getClass().equals(controllerClass)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Closes all windows in the window list.
     */
    public void closeAllWindows() {
        for (Window window : windowList) {
            window.close();
        }
        windowList.clear();
    }
}
