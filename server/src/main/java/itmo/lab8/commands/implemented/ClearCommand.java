package itmo.lab8.commands.implemented;

import itmo.lab8.commands.Action;
import itmo.lab8.server.UdpServer;
import itmo.lab8.server.response.Response;
import itmo.lab8.server.response.ResponseType;

/**
 * ClearCommand class implements Action interface and is used to clear the collection.
 */
public final class ClearCommand implements Action {
    /**
     * Clears the collection.
     *
     * @return A {@link Response} object with a message indicating the success of the operation.
     */
    @Override
    public Response run(String username) {
        UdpServer.collection.clear(username);
        UdpServer.getDatabase().clearCollection(username);
        return new Response("Collection cleaned successfully", ResponseType.SUCCESS);
    }
}
