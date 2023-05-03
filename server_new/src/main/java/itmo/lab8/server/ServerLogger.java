package itmo.lab8.server;

import java.util.logging.Logger;

public class ServerLogger {
    private final static Logger logger = Logger.getLogger(ServerLogger.class.getName());

    public static Logger getInstance() {
        return logger;
    }
}
