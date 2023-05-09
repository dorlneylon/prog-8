package itmo.lab8.commands.implemented;

import itmo.lab8.basic.baseclasses.Movie;
import itmo.lab8.commands.Action;
import itmo.lab8.server.Server;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;

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
        if (Server.getInstance().getCollection().insert(movie)) {
            Server.getInstance().getDatabase().insertToCollection(username, movie);
            return new Response("Insert was completed successfully", ResponseType.OK);
        }
        return new Response("Insertion failed due to indices collision", ResponseType.ERROR);
    }
}