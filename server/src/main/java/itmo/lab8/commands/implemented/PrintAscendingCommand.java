package itmo.lab8.commands.implemented;

import itmo.lab8.commands.Action;
import itmo.lab8.commands.response.MessagePainter;
import itmo.lab8.commands.response.Response;
import itmo.lab8.commands.response.ResponseType;

import java.util.Arrays;
import java.util.List;

import static itmo.lab8.server.UdpServer.collection;
import static java.lang.Math.min;

/**
 * PrintAscendingCommand class implements Action interface and is used to print movies in the collection in ascending order.
 */
public final class PrintAscendingCommand implements Action {
    private final int index;
    private boolean isScript = false;

    /**
     * Constructor for the {@link PrintAscendingCommand} class.
     *
     * @param index The index of the command.
     */
    public PrintAscendingCommand(Integer index) {
        this.index = index;
    }

    public PrintAscendingCommand(Boolean isScript) {
        this();
        this.isScript = isScript;
    }

    /**
     * Constructor for the {@link PrintAscendingCommand} class.
     * Sets the index to 0.
     */
    public PrintAscendingCommand() {
        this(0);
    }

    /**
     * Runs the command and returns the response.
     *
     * @return the response of the command
     */
    @Override
    // Override the run method from the Command interface
    public Response run(String username) {
        // If the collection size is 0, return a success response
        if (collection.size() == 0)
            return new Response("Collection is empty", ResponseType.SUCCESS);

        List<String> movieStrings = Arrays.stream(collection.getSortedMovies(false)).
                map(movie -> movie.toString(username))
                .toList();

        if (isScript) {
            String test = MessagePainter.ColoredInfoMessage(movieStrings.toString().replace("., ", ",\n"));
            return new Response(test.substring(1, test.length() - 1), ResponseType.INFO);
        }

        String message = MessagePainter.ColoredInfoMessage(movieStrings
                .subList(index, min(index + 20, collection.size())).toString()
                .replace("., ", ",\n"));

        return new Response(message.substring(1, message.length() - 1), ResponseType.INFO);
    }
}
