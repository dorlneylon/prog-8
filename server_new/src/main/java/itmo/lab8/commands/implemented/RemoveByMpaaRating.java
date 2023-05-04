package itmo.lab8.commands.implemented;

import itmo.lab8.basic.baseenums.MpaaRating;
import itmo.lab8.commands.Action;
import itmo.lab8.server.Server;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;

/**
 * This class implements the Action interface and is used to remove elements from the collection by their MPAA rating.
 */
public final class RemoveByMpaaRating implements Action {
    private final MpaaRating mpaaRating;

    /**
     * Constructor for RemoveByMpaaRating
     *
     * @param mpaaRating The MpaaRating to be removed
     */
    public RemoveByMpaaRating(MpaaRating mpaaRating) {
        this.mpaaRating = mpaaRating;
    }

    /**
     * Removes a movie from the collection based on its MPAA rating
     *
     * @return A {@link Response} object with a message and a {@link ResponseType}
     */
    @Override
    public Response run(String username) {
        if (Server.getInstance().getCollection().removeByRating(mpaaRating)) {
            Server.getInstance().getDatabase().removeByMpaaRating(username, mpaaRating);
            return new Response("Successfully deleted", ResponseType.OK);
        }
        return new Response("No such element(s)", ResponseType.ERROR);
    }
}
