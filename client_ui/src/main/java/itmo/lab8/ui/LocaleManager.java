package itmo.lab8.ui;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocaleManager {
    private static LocaleManager instance;
    private final HashMap<String, Locale> localeMap = new HashMap<>();
    private ResourceBundle resources = ResourceBundle.getBundle("itmo.lab8.locale");


    public static LocaleManager getInstance() {
        if (instance == null) {
            instance = new LocaleManager();
        }
        return instance;
    }

    public void addLocale(String key, Locale locale) {
        localeMap.put(key, locale);
    }

    public String getResource(String key) {
        try {
            return resources.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    /**
     * Sets the default locale based on the given key.
     *
     * @param key The key of the locale to set as the default.
     */
    public void setDefaultLocale(String key) {
        Locale.setDefault(localeMap.get(key));
        resources = ResourceBundle.getBundle("itmo.lab8.locale", localeMap.get(key));
    }

    /**
     * Updates the locale of the application.
     * <p>
     * USE THIS FOR UPDATING THE LANGUAGE OF THE APPLICATION
     *
     * @param key The key of the locale to be updated.
     */
    public void updateLocale(String key) {
        this.setDefaultLocale(key);
        WindowManager.getInstance().getWindowList().forEach(Window::updateLocale);
    }
}
