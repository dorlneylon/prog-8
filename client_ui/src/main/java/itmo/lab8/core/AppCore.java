package itmo.lab8.core;


import itmo.lab8.commands.CollectionValidator;
import itmo.lab8.connection.ConnectorSingleton;

import java.net.InetAddress;

public class AppCore {

    private static AppCore instance;

    private String name = null;

    public static AppCore getInstance() throws RuntimeException {
        if (instance == null) {
            throw new RuntimeException("AppCore is not initialized");
        }
        return instance;
    }

    public static AppCore newInstance(InetAddress address, int port) {
        try {
            ConnectorSingleton.newInstance(address, port).setBufferSize(8192 * 8192);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        CollectionValidator.updateConnector();
        instance = new AppCore();
        return instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
