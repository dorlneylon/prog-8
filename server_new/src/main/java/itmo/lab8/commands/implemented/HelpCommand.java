package itmo.lab8.commands.implemented;

import itmo.lab8.commands.Action;
import itmo.lab8.commands.CommandType;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * HelpCommand class implements the Action interface and is used to print out the description of all the commands
 * available in the game.
 */
public final class HelpCommand implements Action {
    /**
     * Runs the command.
     *
     * @return The response of the command.
     */
    @Override
    public Response run(String username) {
        return new Response(Arrays.stream(CommandType.values()).map(CommandType::getDescription).filter(description -> !description.isEmpty()).collect(Collectors.joining("\n")), ResponseType.OK);
    }
}
