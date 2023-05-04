package itmo.lab8.connection;

import itmo.lab8.basic.utils.serializer.Serializer;
import itmo.lab8.commands.Command;
import itmo.lab8.core.AppCore;
import itmo.lab8.shared.BlockingChunkList;
import itmo.lab8.shared.Chunk;
import itmo.lab8.shared.Request;
import itmo.lab8.shared.Response;

import java.util.concurrent.ConcurrentHashMap;


public class ConnectionManager {
    private static ConnectionManager instance;

    private final ConcurrentHashMap<Short, BlockingChunkList> chunkMap = new ConcurrentHashMap<>();


    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    private long generateOperationId() {
        return System.currentTimeMillis();
    }

    public void start() {
        ReceiverThread receiverThread = new ReceiverThread();
        receiverThread.start();
    }

    public short newOperation(Command command) throws Exception {
        short operationId = generateOperationId();
        Request request = new Request(command, AppCore.getInstance().getName(), operationId);
        ConnectorSingleton.getInstance().send(Serializer.serialize(request));
        return operationId;
    }

    public Response waitForResponse(short operationId) {
        while (!chunkMap.containsKey(operationId)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        BlockingChunkList chunks = chunkMap.get(operationId);
        try {
            byte[] bytes = chunks.summarizeChunks();
            chunkMap.remove(operationId);
            return (Response) Serializer.deserialize(bytes);
        } catch (InterruptedException e) {
            return null;
        }
    }

    public Response getResponse(long operationId) {
        return null;
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
                            chunkMap.put(chunk.getId(), new BlockingChunkList());
                            chunkMap.get(chunk.getId()).add(chunk);
                        }
                        Thread.sleep(100);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
    }
}
