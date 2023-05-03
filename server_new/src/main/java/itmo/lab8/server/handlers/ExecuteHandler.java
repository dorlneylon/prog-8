package itmo.lab8.server.handlers;

import itmo.lab8.commands.Request;
import itmo.lab8.server.ServerLogger;
import itmo.lab8.utils.Serializer;

import java.net.InetSocketAddress;
import java.util.logging.Level;

public class ExecuteHandler implements Runnable {

    private final byte[] packet;
    private final InetSocketAddress sender;

    public ExecuteHandler(InetSocketAddress sender, byte[] packet) {
        this.sender = sender;
        this.packet = packet;
    }

    @Override
    public void run() {
        Request request = (Request) Serializer.deserialize(packet);
        if (request == null) {
            ServerLogger.getInstance().log(Level.WARNING, "Bad request from " + sender);
            return;
        }

    }

    private static void send(byte[] packet, InetSocketAddress sender) {
        // TODO: Сделать чанкование и отправку на клиент
        //Server.getInstance().getChannel().send(packet, sender);
    }
}
