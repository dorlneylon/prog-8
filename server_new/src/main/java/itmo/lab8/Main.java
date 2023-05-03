package itmo.lab8;

import itmo.lab8.database.Database;
import itmo.lab8.server.Server;
import itmo.lab8.server.ServerLogger;
import itmo.lab8.utils.Config;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

public class Main {
    private static int port;
    private static Database database;

    public static void main(String[] args) throws FileNotFoundException {
        Config.setConfig("server.scfg");
        Config.load();
        try {
            database = new Database(Config.get("db_url"), Config.get("user"), Config.get("password"));
        } catch (SQLException e) {
            ServerLogger.getInstance().log(Level.WARNING, "Can't connect to database", e);
            System.exit(1);
        }
        try {
            port = Integer.parseInt(Objects.requireNonNull(Config.get("port")));
        } catch (NumberFormatException e) {
            ServerLogger.getInstance().log(Level.WARNING, "Bad port", e);
            ServerLogger.getInstance().log(Level.WARNING, "Using default port: 5050");
            port = 5050;
        }
        Server server = Server.getInstance(database);
        server.run(port);
    }
}
