package itmo.lab8.server.threads;

import itmo.lab8.server.data.Chunk;
import itmo.lab8.server.data.ChunkPool;
import itmo.lab8.server.handlers.ExecuteHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
                            if (poolMap.get(user).get(operationId).isEmpty()) {
                                continue;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            continue;
                        }
                        // Getting first chunk for getting packet info
                        Chunk chunk;
                        try {
                            chunk = poolMap.get(user).get(operationId).get(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            continue;
                        }
                        // if all chunks are received
                        try {
                            if (chunk.getTotal() == poolMap.get(user).get(operationId).size()) {
                                byte[] packet = poolMap.get(user).get(operationId).summarizeChunks();
                                threadPool.submit(new ExecuteHandler(user, packet));
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
