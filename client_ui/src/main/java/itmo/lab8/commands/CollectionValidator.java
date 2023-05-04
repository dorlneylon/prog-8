package itmo.lab8.commands;


import itmo.lab8.connection.ConnectionManager;
import itmo.lab8.connection.ConnectorSingleton;
import itmo.lab8.shared.Response;

/**
 * This class is used to store collection data validators
 */
public final class CollectionValidator {

    private static ConnectorSingleton connector;

    public static void updateConnector() {
        CollectionValidator.connector = ConnectorSingleton.getInstance();
    }

    /**
     * Метод проверяет:
     * 1. Если команда INSERT, то проверяет, не существует ли ключ в коллекции. Если существует, то возвращает false.
     * 2. Если команда UPDATE или REPLACE_IF_LOWER, то проверяет, существует ли ключ в коллекции. Если не существует, то возвращает false.
     */
    public static Boolean checkIfExists(Long key) throws Exception {
        short opId = ConnectionManager.getInstance().newOperation(new Command(CommandType.SERVICE, "check_id %d".formatted(key)));
        Response response = ConnectionManager.getInstance().waitForResponse(opId);
        return Boolean.parseBoolean(response.getStringMessage());
    }

    public static int getCollectionSize() throws Exception {
        short opId = ConnectionManager.getInstance().newOperation(new Command(CommandType.SERVICE, "get_collection_size"));
        Response response = ConnectionManager.getInstance().waitForResponse(opId);
        return Integer.parseInt(response.getStringMessage());
    }

    private static boolean isCommandLegit(String name, CommandType commandType, Long key) throws Exception {
        if (commandType.equals(CommandType.INSERT)) {
            return !checkIfExists(key);
        } else if (commandType.equals(CommandType.UPDATE) || commandType.equals(CommandType.REPLACE_IF_LOWER)) {
            return checkIfExists(key) && isUserCreator(name, key);
        }
        return true;
    }

    public static Boolean isMovieValid(CommandType type, String name, String[] args) {
        if (args.length < 1) {
            System.err.println("Not enough arguments for command " + type.name());
            return null;
        }
        long key;
        try {
            key = Long.parseLong(args[0]);
            if (!isCommandLegit(name, type, key)) {
                System.err.println("Key " + key + " is not compatible with the command " + type.name() + " for you.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Invalid argument for command " + type.name());
            return false;
        }
        return true;
    }

    public static boolean isUserCreator(String name, long key) throws Exception {
        short opId = ConnectionManager.getInstance().newOperation(new Command(CommandType.SERVICE, "is_user_creator %d %s".formatted(key, name)));
        Response response = ConnectionManager.getInstance().waitForResponse(opId);
        return Boolean.parseBoolean(response.getStringMessage());
    }


}

