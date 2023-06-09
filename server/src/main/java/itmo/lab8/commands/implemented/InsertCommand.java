package itmo.lab8.commands.implemented;

import itmo.lab8.basic.baseclasses.Movie;
import itmo.lab8.commands.Action;
import itmo.lab8.server.UdpServer;
import itmo.lab8.commands.response.Response;
import itmo.lab8.commands.response.ResponseType;

import static itmo.lab8.server.UdpServer.collection;

/**
 * InsertCommand class implements Action interface and is used to insert a movie into the collection.
 */
public final class InsertCommand implements Action {
    private final Movie movie;

    /**
     * Constructor for InsertCommand
     *
     * @param movie The movie to be inserted
     */
    public InsertCommand(Movie movie) {
        this.movie = movie;
    }

    /**
     * Runs the insert operation on the collection.
     *
     * @return A {@link Response} object with a message and a {@link ResponseType} indicating the success of the operation.
     */
    @Override
    public Response run(String username) {
        if (collection.insert(movie)) {
            UdpServer.getDatabase().insertToCollection(username, movie);
            return new Response("Insert was completed successfully", ResponseType.INFO);
        }
        return new Response("Insertion failed due to indices collision", ResponseType.INFO);
    }
}