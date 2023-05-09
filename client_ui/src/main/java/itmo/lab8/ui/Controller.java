package itmo.lab8.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.lang.reflect.Field;

public abstract class Controller {
    public void updateUi() {
        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(FXML.class) || field.isAnnotationPresent(Variable.class)) continue;
            if (field.getType().equals(Label.class)) {
                field.setAccessible(true);
                try {
                    Label label = (Label) field.get(this);
                    label.setText(LocaleManager.getInstance().getResource(field.getName()));
                    label.getStyleClass().remove("leet-labels-panes");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (field.getType().equals(Button.class)) {
                field.setAccessible(true);
                try {
                    Button button = (Button) field.get(this);
                    button.setText(LocaleManager.getInstance().getResource(field.getName()));
                    button.getStyleClass().remove("leet-lists-btns");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (field.getType().equals(ListView.class)) {
                field.setAccessible(true);
                try {
                    ListView listView = (ListView) field.get(this);
                    listView.getStyleClass().remove("leet-lists-btns");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (field.getType().equals(VBox.class)) {
                field.setAccessible(true);
                try {
                    VBox vBox = (VBox) field.get(this);
                    vBox.getStyleClass().remove("leet-labels-panes");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setLeet() {
        for (Field field : getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(FXML.class) || field.isAnnotationPresent(Variable.class)) continue;
            if (field.getType().equals(Label.class)) {
                field.setAccessible(true);
                try {
                    Label label = (Label) field.get(this);
                    label.setText(LocaleManager.getInstance().getResource(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (field.getType().equals(Button.class)) {
                field.setAccessible(true);
                try {
                    Button button = (Button) field.get(this);
                    button.setText(LocaleManager.getInstance().getResource(field.getName()));
                    button.getStyleClass().add("leet-lists-btns");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (field.getType().equals(ListView.class)) {
                field.setAccessible(true);
                try {
                    ListView listView = (ListView) field.get(this);
                    listView.getStyleClass().add("leet-lists-btns");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (field.getType().equals(VBox.class)) {
                field.setAccessible(true);
                try {
                    VBox vBox = (VBox) field.get(this);
                    vBox.getStyleClass().add("leet-labels-panes");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void close() {
    }
}
