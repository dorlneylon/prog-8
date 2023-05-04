package itmo.lab8.commands.implemented;

import itmo.lab8.commands.Action;
import itmo.lab8.server.Server;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to execute commands from the service.
 */
public final class ServiceCommand implements Action {
    private final String command;

    /**
     * Constructor for ServiceCommand
     *
     * @param command The command to be executed
     */
    public ServiceCommand(String command) {
        this.command = command;
    }

    /**
     * Executes the command given by the user.
     *
     * @return Response object with the result of the command execution
     */
    @Override
    public Response run(String username) {
        String[] splitCommand = command.split(" ");
        String commandPart = splitCommand[0];
        String arg = null;
        if (splitCommand.length > 1) {
            arg = splitCommand[1];
        }
        return switch (commandPart) {
            case "check_id" -> {
                assert arg != null;
                Long id = Long.parseLong(arg);
                boolean isPresented = Server.getInstance().getCollection().isKeyPresented(id);
                yield new Response(Boolean.toString(isPresented), ResponseType.OK);
            }
            case "get_collection_size" ->
                    new Response(Integer.toString(Server.getInstance().getCollection().size()), ResponseType.OK);
            case "sign_up" -> {
                assert arg != null;
                Matcher matcher = Pattern.compile("(.*):(.*)$").matcher(arg);
                if (!matcher.find()) {
                    yield new Response("Wrong format of the command", ResponseType.OK);
                }

                yield Server.getInstance().getDatabase().addNewUser(matcher.group(1), matcher.group(2)) ?
                        new Response("OK", ResponseType.OK) :
                        new Response("Something happened during signing. Try again", ResponseType.ERROR);
            }
            case "sign_in" -> {
                assert arg != null;
                Matcher matcher = Pattern.compile("(.*):(.*)$").matcher(arg);
                if (!matcher.find()) {
                    yield new Response("Wrong format of the command", ResponseType.ERROR);
                }

                yield Server.getInstance().getDatabase().userSignIn(matcher.group(1), matcher.group(2)) ?
                        new Response("OK", ResponseType.OK) :
                        new Response("Something happened during signing. Try again", ResponseType.ERROR);
            }
            case "is_user_creator" ->
                    new Response(Boolean.toString(Server.getInstance().getDatabase().isUserEditor(splitCommand[2], Integer.parseInt(arg))), ResponseType.OK);
            default -> new Response("", ResponseType.ERROR);
        };
    }
}
