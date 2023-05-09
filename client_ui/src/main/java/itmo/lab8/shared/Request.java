package itmo.lab8.shared;

import itmo.lab8.commands.Command;

import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = -7403418586909795322L;
    private final Command command;
    private final String login;
    private final long operationId;

    public Request(Command command, String login, long operationId) {
        this.command = command;
        this.login = login;
        this.operationId = operationId;
    }

    public Command getCommand() {
        return command;
    }

    public String getUserName() {
        return login;
    }

    public long getOperationId() {
        return operationId;
    }
}
