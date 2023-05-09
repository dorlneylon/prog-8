package itmo.lab8.commands.implemented;

import itmo.lab8.commands.Action;
import itmo.lab8.server.Server;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;

/**
 * This class implements the {@link Action} interface and is used to remove all elements from the collection that are greater than the given key.
 */
public final class RemoveGreaterCommand implements Action {

    private final Long key;

    /**
     * Constructor for RemoveGreaterCommand.
     *
     * @param key - the key to be used for comparison
     */
    public RemoveGreaterCommand(Long key) {
        this.key = key;
    }

    /**
     * Removes elements from the collection that are greater than the given key
     *
     * @return Response object with the result of the operation
     */
    @Override
    public Response run(String username) {
        if (Server.getInstance().getCollection().removeGreater(key)) {
            Server.getInstance().getDatabase().removeGreater(Math.toIntExact(key), username);
            return new Response("Greater elements have been successfully deleted", ResponseType.OK);
        }
        return new Response("There are no items in the collection larger than a given", ResponseType.ERROR);
    }
}
