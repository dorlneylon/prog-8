package itmo.lab8.server.handlers;

import itmo.lab8.commands.CommandType;
import itmo.lab8.server.Server;
import itmo.lab8.server.ServerLogger;
import itmo.lab8.shared.Chunker;
import itmo.lab8.shared.Request;
import itmo.lab8.shared.Response;
import itmo.lab8.shared.ResponseType;
import itmo.lab8.utils.CollectionValidator;
import itmo.lab8.utils.Serializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.logging.Level;

public class ExecuteHandler implements Runnable {

    private final byte[] packet;
    private final InetSocketAddress sender;
    private final short operationId;

    public ExecuteHandler(InetSocketAddress sender, byte[] packet, short operationId) {
        this.sender = sender;
        this.packet = packet;
        this.operationId = operationId;
    }

    @Override
    public void run() {
        Request request = (Request) Serializer.deserialize(packet);
        if (request == null) {
            Response response = new Response("Unable to get request.", ResponseType.ERROR);
            ServerLogger.getInstance().log(Level.WARNING, "Request from " + sender + "is null");
            send(Serializer.serialize(response));
            return;
        }
        boolean authorized = Server.getInstance().getDatabase().isUserExist(request.getUserName());
        if (!authorized && request.getCommand().getCommandType() != CommandType.SERVICE) {
            Response response = new Response("You are not authorized to use this command.", ResponseType.ERROR);
            send(Serializer.serialize(response));
            return;
        }
        ServerLogger.getInstance().log(Level.INFO, "Received command %s from %s".formatted(request.getCommand().getCommandType(), request.getUserName()));
        Response response = request.getCommand().execute(request.getUserName());

        byte[] serializedResponse = Serializer.serialize(response);
        boolean sentSuccessfully = send(serializedResponse);
        if (sentSuccessfully && CollectionValidator.isValidHistoryInput(request.getCommand()) && !request.getCommand().getCommandType().equals(CommandType.HISTORY) && !request.getCommand().getCommandType().equals(CommandType.SHOW)) {
            Server.getInstance().getDatabase().addCommandToHistory(request.getUserName(), request.getCommand().getCommandType().name());
        }
    }

    private boolean send(byte[] packet) {
        Chunker chunker = new Chunker(packet, operationId);
        var iterator = chunker.newIterator();
        byte c = 0;
        while (iterator.hasNext()) {
            byte[] chunk = iterator.next();
            if (++c % 50 == 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            try {
                Server.getInstance().getChannel().send(ByteBuffer.wrap(chunk), sender);
            } catch (IOException e) {
                ServerLogger.getInstance().log(Level.WARNING, "Unable to send chunk to " + sender);
                return false;
            }
        }
        return true;
    }
}
