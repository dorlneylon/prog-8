package itmo.lab8.commands.implemented;

import itmo.lab8.basic.baseclasses.Movie;
import itmo.lab8.commands.Action;
import itmo.lab8.server.Server;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;

/**
 * This class implements the Action interface and is used to update a movie in the collection.
 */
public class UpdateCommand implements Action {
    private final Movie movie;

    /**
     * Constructor for UpdateCommand
     *
     * @param movie The movie to be updated
     */
    public UpdateCommand(Movie movie) {
        this.movie = movie;
    }

    /**
     * Updates the movie in the collection.
     *
     * @return A {@link Response} object with the appropriate message and {@link ResponseType}
     */
    @Override
    public Response run(String username) {
        if (!Server.getInstance().getCollection().isKeyPresented(movie.getId()))
            return new Response("Collection does not contain such a key", ResponseType.ERROR);

        Server.getInstance().getCollection().update(movie);
        Server.getInstance().getDatabase().updateById(username, Math.toIntExact(movie.getId()), movie);

        return new Response("Update was completed successfully", ResponseType.OK);
    }
}
