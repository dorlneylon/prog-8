package itmo.lab8.commands.implemented;

import itmo.lab8.commands.Action;
import itmo.lab8.server.Server;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;

import java.util.ArrayList;
import java.util.Arrays;

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
        ArrayList<Long> keys = Server.getInstance().getDatabase().getUsersMovies(username);
        System.out.println(Arrays.toString(keys.toArray()));
        for (Long key : keys) {
            Server.getInstance().getCollection().removeByKey(key);
        }
        Server.getInstance().getDatabase().clearCollection(username);
        return new Response("Collection cleaned successfully", ResponseType.OK);
    }
}
