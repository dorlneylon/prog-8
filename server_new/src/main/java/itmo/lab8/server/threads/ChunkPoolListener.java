package itmo.lab8.server.threads;

import itmo.lab8.server.ServerLogger;
import itmo.lab8.server.data.ChunkPool;
import itmo.lab8.server.handlers.ExecuteHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

public class ChunkPoolListener extends Thread {

    public ChunkPoolListener() {
        super(() -> {
            ExecutorService threadPool = Executors.newCachedThreadPool();
            while (!Thread.currentThread().isInterrupted()) {
                var poolMap = ChunkPool.getInstance().getChunkPoolMap();
                for (var user : poolMap.keySet()) {
                    for (var operationId : poolMap.get(user).keySet()) {
                        // Checking that the chunk array isn't empty
                        try {
                            if (poolMap.get(user).get(operationId) == null || poolMap.get(user).get(operationId).isEmpty()) {
                                continue;
                            }
                        } catch (InterruptedException e) {
                            ServerLogger.getInstance().log(Level.WARNING, e.getMessage());
                            continue;
                        }
                        // if all chunks are received
                        try {
                            if (poolMap.get(user).get(operationId) != null && poolMap.get(user).get(operationId).allReceived()) {
                                byte[] packet = poolMap.get(user).get(operationId).summarizeChunks();
                                poolMap.get(user).remove(operationId);
                                threadPool.submit(new ExecuteHandler(user, packet, operationId));
                            }
                        } catch (InterruptedException e) {
                            ServerLogger.getInstance().log(Level.WARNING, e.getMessage());
                        }
                    }
                }
            }
        });
    }
}
