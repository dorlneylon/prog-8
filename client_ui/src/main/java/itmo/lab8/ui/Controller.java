package itmo.lab8.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.lang.reflect.Field;

public abstract class Controller {
    @FXML
    public void initialize() {
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
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
