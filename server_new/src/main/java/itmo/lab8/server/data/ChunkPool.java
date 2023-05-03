package itmo.lab8.server.data;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ChunkPool class is responsible for storing chunks in a concurrent hash map.
 */
public class ChunkPool {
    private static ChunkPool instance;
    private final ConcurrentHashMap<InetSocketAddress, ConcurrentHashMap<Short, BlockingChunkList<Chunk>>> chunkPoolMap;


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

    public ConcurrentHashMap<InetSocketAddress, ConcurrentHashMap<Short, BlockingChunkList<Chunk>>> getChunkPoolMap() {
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
        chunkPoolMap.put(address, new ConcurrentHashMap<>());
        if (chunkPoolMap.get(address).containsKey(chunkObject.getId())) {
            try {
                chunkPoolMap.get(address).get(chunkObject.getId()).add(chunkObject);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            chunkPoolMap.get(address).put(chunkObject.getId(), new BlockingChunkList<>());
            try {
                chunkPoolMap.get(address).get(chunkObject.getId()).add(chunkObject);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
