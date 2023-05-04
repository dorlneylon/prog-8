package itmo.lab8.server.handlers;

import itmo.lab8.server.data.ChunkPool;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ReceiveHandler implements Runnable {

    private final ByteBuffer buffer;
    private final InetSocketAddress address;

    public ReceiveHandler(InetSocketAddress address, ByteBuffer buffer) {
        this.buffer = buffer;
        this.address = address;
    }

    @Override
    public void run() {
        ChunkPool.getInstance().addChunk(address, Arrays.copyOfRange(buffer.array(), 0, buffer.position()));
    }
}
