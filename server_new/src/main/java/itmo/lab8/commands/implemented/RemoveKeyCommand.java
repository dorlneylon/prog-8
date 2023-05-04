package itmo.lab8.commands.implemented;

import itmo.lab8.commands.Action;
import itmo.lab8.server.Server;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;

public final class RemoveKeyCommand implements Action {
    private final Long key;

    /**
     * Constructor for the RemoveKeyCommand class.
     *
     * @param key The key to be removed.
     */
    public RemoveKeyCommand(Long key) {
        this.key = key;
    }

    /**
     * This method is used to delete a movie from the collection by its key.
     *
     * @return a {@link Response} object with a message and a {@link ResponseType}
     */
    @Override
    public Response run(String username) {
        if (Server.getInstance().getCollection().removeByKey(key)) {
            Server.getInstance().getDatabase().removeByKey(key, username);
            return new Response("Movie with key %d deleted successfully".formatted(key), ResponseType.OK);
        }
        return new Response("It is not possible to delete a Movie with key=%d, because there is no Movie with this key.".formatted(key), ResponseType.ERROR);
    }
}
