package itmo.lab8.commands.implemented;

import itmo.lab8.commands.Action;
import itmo.lab8.server.Server;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;
import itmo.lab8.utils.Serializer;

public final class HistoryCommand implements Action {

    @Override
    public Response run(String username) {
        String[] history = Server.getInstance().getDatabase().getCommandHistory(username);
        return new Response(Serializer.serialize(history), ResponseType.OK);
    }
}
