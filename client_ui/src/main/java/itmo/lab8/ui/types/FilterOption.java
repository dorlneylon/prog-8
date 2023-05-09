package itmo.lab8.ui.types;

import itmo.lab8.ui.LocaleManager;

public class FilterOption {
    private final String typeName;
    private String stringValue;

    public FilterOption(String typeName) {
        this.typeName = typeName;
        refresh();
    }

    public String toString() {
        return stringValue;
    }

    public void refresh() {
        stringValue = LocaleManager.getInstance().getResource(typeName);
    }

    public String getTypeName() {
        return typeName;
    }
}
