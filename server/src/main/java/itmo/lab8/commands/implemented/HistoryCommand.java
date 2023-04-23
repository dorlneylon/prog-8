package itmo.lab8.commands.implemented;

import itmo.lab8.commands.Action;
import itmo.lab8.server.UdpServer;
import itmo.lab8.server.response.Color;
import itmo.lab8.server.response.Response;
import itmo.lab8.server.response.ResponseType;

import java.util.Arrays;

public final class HistoryCommand implements Action {

    private final String username;

    public HistoryCommand(String username) {
        this.username = username;
    }

    @Override
    public Response run(String username) {
        return new Response(Color.PURPLE + "Command history:\n" + Color.RESET + Arrays.stream(UdpServer.getDatabase().getCommandHistory(username)).reduce("", (a, b) -> a + "\n" + b).substring(1), ResponseType.INFO);
    }
}
