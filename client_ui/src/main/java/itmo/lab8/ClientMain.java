package itmo.lab8;

import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.core.AppCore;
import itmo.lab8.ui.LocaleManager;
import itmo.lab8.ui.WindowManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

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
    public void start(Stage stage) throws IOException {
        LocaleManager.getInstance().addLocale("ru", new Locale("ru"));
        LocaleManager.getInstance().addLocale("fi", new Locale("fi"));
        LocaleManager.getInstance().addLocale("ca", new Locale("ca"));
        LocaleManager.getInstance().addLocale("es", new Locale("es"));
        LocaleManager.getInstance().setDefaultLocale("ru");
        AppCore.newInstance(serverAddress, serverPort);
        ConnectionManager.getInstance().start();
        WindowManager.getInstance().newAuthWindow();
    }
}
