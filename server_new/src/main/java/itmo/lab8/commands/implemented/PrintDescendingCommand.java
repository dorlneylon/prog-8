package itmo.lab8.commands.implemented;

import itmo.lab8.commands.Action;
import itmo.lab8.server.Server;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;

import java.util.Arrays;
import java.util.List;

import static java.lang.Math.min;


/**
 * PrintDescendingCommand class implements {@link Action} interface.
 * This class is used to print the collection in descending order.
 */
public final class PrintDescendingCommand implements Action {
    private final int index;
    private boolean isScript = false;

    /**
     * Constructor for the {@link PrintDescendingCommand} class.
     *
     * @param index the index of the number to be printed
     */
    public PrintDescendingCommand(Integer index) {
        this.index = index;
    }

    public PrintDescendingCommand(Boolean isScript) {
        this();
        this.isScript = isScript;
    }

    /**
     * Constructor for the {@link PrintDescendingCommand} class.
     * Sets the index to 0.
     */
    public PrintDescendingCommand() {
        this(0);
    }

    /**
     * Runs the  command.
     *
     * @return A response with the result of the command.
     */
    @Override
    public Response run(String username) {
        if (Server.getInstance().getCollection().size() == 0) {
            return new Response("Collection is empty", ResponseType.OK);
        }
        List<String> movieStrings = Arrays.stream(Server.getInstance().getCollection().getSortedMovies(true)).map(movie -> movie.toString(username)).toList();

        if (isScript) {
            String test = movieStrings.toString().replace("., ", ",\n");
            return new Response(test.substring(1, test.length() - 1), ResponseType.OK);
        }

        String message = movieStrings.subList(index, min(index + 20, Server.getInstance().getCollection().size())).toString().replace("., ", ",\n");

        return new Response(message.substring(1, message.length() - 1), ResponseType.OK);
    }
}
