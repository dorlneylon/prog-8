package itmo.lab8.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Config class is used to read and store configuration from a file.
 */
public class Config {
    private static Config instance = null;
    private HashMap<String, String> configMap;
    private File configFile;

    /**
     * Sets the configuration file to the given filename.
     *
     * @param filename The name of the configuration file to be used.
     */
    public static void setConfig(String filename) {
        instance = new Config();
        instance.configFile = new File(filename);
    }

    /**
     * Retrieves the value associated with the given key from the configuration map.
     *
     * @param key The key associated with the value to be retrieved.
     * @return The value associated with the given key, or null if the key is not found.
     */
    public static String get(String key) {
        if (instance == null) {
            return null;
        }
        return instance.configMap.get(key);
    }

    /**
     * Loads the configuration file.
     *
     * @throws FileNotFoundException if the configuration file does not exist
     */
    public static void load() throws FileNotFoundException {
        // Create a new file object with the given fileName
        if (instance == null) {
            return;
        }
        // Check if the file exists
        if (instance.configFile.exists()) {
            // If the file exists, read the file and store the contents in the configMap
            instance.configMap = readFile(instance.configFile);
            // Return from the method
            return;
        }
        // If the file does not exist, throw a FileNotFoundException
        throw new FileNotFoundException("File not found: " + instance.configFile.getName());
    }

    private static HashMap<String, String> readFile(File config) {
        HashMap<String, String> configMap = new HashMap<>();
        try (Scanner scanner = new Scanner(config)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // Skipping comments and lines without "Key=Value"
                if (line.startsWith("#") || !line.contains("=")) continue;
                String[] split = line.split("=", 2);
                if (split.length == 2) configMap.put(split[0], split[1]);
            }
        } catch (FileNotFoundException ignored) {
            // Ignoring this, because file was existence checked
        }
        return configMap;
    }
}
