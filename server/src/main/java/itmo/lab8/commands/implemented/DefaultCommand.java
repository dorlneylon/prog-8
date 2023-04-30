package itmo.lab8.commands.implemented;

import itmo.lab8.commands.Action;
import itmo.lab8.commands.response.Response;
import itmo.lab8.commands.response.ResponseType;

public final class DefaultCommand implements Action {

    @Override
    public Response run(String username) {
        return new Response("Unknown command. To view command list use command 'help'", ResponseType.INFO);
    }
}
