package itmo.lab8.server.data;

import itmo.lab8.server.ServerLogger;
import itmo.lab8.shared.BlockingChunkList;
import itmo.lab8.shared.Chunk;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * ChunkPool class is responsible for storing chunks in a concurrent hash map.
 */
public class ChunkPool {
    private static ChunkPool instance;
    private final ConcurrentHashMap<InetSocketAddress, ConcurrentHashMap<Short, BlockingChunkList>> chunkPoolMap;


    public ChunkPool() {
        chunkPoolMap = new ConcurrentHashMap<>();
    }

    /**
     * Returns the singleton instance of the {@link ChunkPool}.
     *
     * @return The singleton instance of the {@link ChunkPool}.
     */
    public static ChunkPool getInstance() {
        if (instance == null) {
            instance = new ChunkPool();
        }
        return instance;
    }

    public ConcurrentHashMap<InetSocketAddress, ConcurrentHashMap<Short, BlockingChunkList>> getChunkPoolMap() {
        return chunkPoolMap;
    }

    /**
     * Adds a new chunk to the chunk pool.
     *
     * @param address The address of the node that the chunk belongs to.
     * @param chunk   The chunk to be added.
     */
    public void addChunk(InetSocketAddress address, byte[] chunk) {
        Chunk chunkObject = new Chunk(chunk);
        synchronized (chunkPoolMap) {
            if (!chunkPoolMap.containsKey(address)) {
                chunkPoolMap.put(address, new ConcurrentHashMap<>());
            }
            if (chunkPoolMap.get(address).containsKey(chunkObject.getId())) {
                try {
                    chunkPoolMap.get(address).get(chunkObject.getId()).add(chunkObject);
                } catch (InterruptedException e) {
                    ServerLogger.getInstance().log(Level.INFO, "ChunkPool", e.getMessage());
                }
            } else {
                chunkPoolMap.get(address).put(chunkObject.getId(), new BlockingChunkList(chunkObject));
            }
        }
    }
}
