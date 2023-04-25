package itmo.lab8.ui.types;

import itmo.lab8.commands.CommandType;
import javafx.scene.control.Label;

public class CommandButton extends Label {

    private final CommandType type;

    public CommandButton(CommandType type) {
        super(type.name());
        this.type = type;
    }

    public CommandType getType() {
        return type;
    }
}
