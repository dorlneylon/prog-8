package itmo.lab8;

import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.core.AppCore;
import itmo.lab8.ui.SceneManager;
import itmo.lab8.ui.controllers.AuthController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Objects;

public class ClientMain extends Application {
    private static final InetAddress serverAddress;
    private static final int serverPort = 5050;
    public static System.Logger log = System.getLogger(ClientMain.class.getName());

    static {
        try {
            serverAddress = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Locale.setDefault(new Locale("ru"));
        AppCore.newInstance(serverAddress, serverPort);
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
    }

}
