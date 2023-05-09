package itmo.lab8.commands.implemented;

import itmo.lab8.commands.Action;
import itmo.lab8.server.Server;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;

/**
 * InfoCommand class implements Action interface and is used to return information about the collection.
 */
public final class InfoCommand implements Action {

    /**
     * @return a {@link Response} containing the information about the collection
     */
    @Override
    public Response run(String username) {
        return new Response(Server.getInstance().getCollection().info(), ResponseType.OK);
    }
}
