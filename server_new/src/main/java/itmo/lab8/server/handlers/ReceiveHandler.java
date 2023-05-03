package itmo.lab8.server.handlers;

import itmo.lab8.server.ServerLogger;
import itmo.lab8.server.data.ChunkPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.logging.Level;

public class ReceiveHandler implements Runnable {

    private final SelectionKey key;

    public ReceiveHandler(SelectionKey key) {
        this.key = key;
    }

    @Override
    public void run() {
        DatagramChannel channel = (DatagramChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1030);
        InetSocketAddress address;
        try {
            address = (InetSocketAddress) channel.receive(buffer);
        } catch (IOException e) {
            ServerLogger.getInstance().log(Level.WARNING, "Can't receive data: ", e);
            return;
        }
        ChunkPool.getInstance().addChunk(address, buffer.array());
    }
}
