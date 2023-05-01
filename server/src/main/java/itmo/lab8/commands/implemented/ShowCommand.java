package itmo.lab8.commands.implemented;

import itmo.lab8.basic.baseclasses.Movie;
import itmo.lab8.commands.Action;
import itmo.lab8.commands.response.Response;
import itmo.lab8.commands.response.ResponseType;
import itmo.lab8.utils.serializer.Serializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static itmo.lab8.server.UdpServer.collection;

/**
 * Class that implements the {@link Action} interface and is used to show the elements of the collection.
 */
public class ShowCommand implements Action {

    private final int offset;

    /**
     * Constructor for ShowCommand
     *
     * @param offset the offset of the item to be shown
     */
    public ShowCommand(Integer offset) {
        this.offset = offset;
    }

    /**
     * Constructs a new {@code ShowCommand} with the given {@code id}.
     * Sets index to 0;
     */
    public ShowCommand() {
        this(0);
    }

    /**
     * Constructor for ShowCommand.
     */
    public ShowCommand(Boolean isScript) {
        this();
    }

    /**
     * Runs the command.
     *
     * @return The response of the command.
     */
    @Override
    public Response run(String username) {
        ArrayList<Movie> lm = Arrays.stream(collection.values()).skip(offset).limit(20).collect(Collectors.toCollection(ArrayList::new));
        return new Response(Serializer.serialize(lm), ResponseType.INFO);
    }
}