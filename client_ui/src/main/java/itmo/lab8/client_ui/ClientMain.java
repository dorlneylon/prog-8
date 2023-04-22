package itmo.lab8.client_ui;

import itmo.lab8.client_ui.core.ClientCore;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;

public class ClientMain extends Application {
    private static final InetAddress serverAddress;
    private static final int serverPort = 5050;

    static {
        try {
            serverAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch();
        ClientCore core = new ClientCore(serverAddress, serverPort);
        try {
            core.run();
        } catch (NoSuchElementException e) {
            System.out.println("Ctrl-D detected.\n Shutting the client down...");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ClientMain.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
