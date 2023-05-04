package itmo.lab8.connection;

import itmo.lab8.basic.utils.serializer.Serializer;
import itmo.lab8.commands.Command;
import itmo.lab8.core.AppCore;
import itmo.lab8.shared.BlockingChunkList;
import itmo.lab8.shared.Chunk;
import itmo.lab8.shared.Request;
import itmo.lab8.shared.Response;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class ConnectionManager {
    private static ConnectionManager instance;

    private final AtomicInteger operationId = new AtomicInteger(Short.MIN_VALUE);

    private final ConcurrentHashMap<Short, BlockingChunkList> chunkMap = new ConcurrentHashMap<>();


    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    private short generateOperationId() {
        if (operationId.get() >= Short.MAX_VALUE) {
            operationId.set(Short.MIN_VALUE);
        }
        return (short) operationId.getAndIncrement();
    }

    public void start() {
        ReceiverThread receiverThread = new ReceiverThread();
        System.out.println("Starting receiver thread");
        receiverThread.start();
    }

    public short newOperation(Command command) throws Exception {
        short operationId = generateOperationId();
        Request request = new Request(command, AppCore.getInstance().getName(), operationId);
        ConnectorSingleton.getInstance().send(Serializer.serialize(request), operationId);
        return operationId;
    }

    public Response waitForResponse(short operationId) {
        while (!chunkMap.containsKey(operationId) || chunkMap.get(operationId) == null || !chunkMap.get(operationId).allReceived()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        BlockingChunkList chunks;
        try {
            chunks = chunkMap.get(operationId);
        } catch (Exception e) {
            // Return null if waited more than 10 sec
            return null;
        }
        try {
            byte[] bytes = chunks.summarizeChunks();
            chunkMap.remove(operationId);
            if (bytes == null) {
                System.out.println("WTF");
            }
            return (Response) Serializer.deserialize(bytes);
        } catch (InterruptedException e) {
            return null;
        }
    }

    private class ReceiverThread extends Thread {
        public ReceiverThread() {
            super(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Chunk chunk = ConnectorSingleton.getInstance().receive();
                        if (chunk == null) {
                            continue;
                        }
                        if (chunkMap.containsKey(chunk.getId())) {
                            chunkMap.get(chunk.getId()).add(chunk);
                        } else {
                            chunkMap.put(chunk.getId(), new BlockingChunkList(chunk));
                        }
                        Thread.sleep(100);
                    } catch (IOException | InterruptedException ignored) {
                        // ignore
                    }
                }
            });
        }
    }
}
