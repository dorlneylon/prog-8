package itmo.lab8;

import itmo.lab8.core.ClientCore;
import itmo.lab8.ui.SceneManager;
import itmo.lab8.ui.controllers.AuthController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public class ClientMain extends Application {
    public static System.Logger log = System.getLogger(ClientMain.class.getName());
    private static final InetAddress serverAddress;
    private static final int serverPort = 5050;

    static {
        try {
            serverAddress = InetAddress.getByName("192.168.137.234");
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch();
//        ClientCore core = new ClientCore(serverAddress, serverPort);
//        try {
//            core.run();
//        } catch (NoSuchElementException e) {
//            System.out.println("Ctrl-D detected.\n Shutting the client down...");
//        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        ClientCore core = new ClientCore(serverAddress, serverPort);
        SceneManager sceneManager = new SceneManager(core);
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
