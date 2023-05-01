package itmo.lab8.commands.implemented;

import itmo.lab8.basic.baseclasses.Movie;
import itmo.lab8.commands.Action;
import itmo.lab8.server.UdpServer;
import itmo.lab8.commands.response.Response;
import itmo.lab8.commands.response.ResponseType;

import static itmo.lab8.server.UdpServer.collection;

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
        if (!collection.isKeyPresented(movie.getId()))
            return new Response("Collection does not contain such a key", ResponseType.ERROR);

        collection.update(movie);
        UdpServer.getDatabase().updateById(username, Math.toIntExact(movie.getId()), movie);

        return new Response("Update was completed successfully", ResponseType.SUCCESS);
    }
}
