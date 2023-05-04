package itmo.lab8.shared;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Chunker class used to split a byte array into chunks of a specified size.
 *
 * @author John Doe
 */
public class Chunker {
    private final byte[] data;
    private final List<byte[]> chunked = new ArrayList<>();
    private final int chunkSize;

    /**
     * Constructs a new Chunker object with the given data and a default chunk size of 1024.
     *
     * @param data The data to be chunked.
     */
    public Chunker(byte[] data, short id) {
        this(data, id, 1024);
    }

    /**
     * Constructor for Chunker class.
     *
     * @param data      The data to be chunked.
     * @param chunkSize The size of each chunk.
     */
    public Chunker(byte[] data, short id, int chunkSize) {
        this.data = data;
        this.chunkSize = chunkSize;
        createChunkedData(id);
    }

    /**
     * Creates a list of byte arrays, each containing a chunk of the dataBytes.
     */
    private void createChunkedData(short id) {
        // Calculate the total number of chunks needed to send the dataBytes
        short totalChunks = (short) Math.ceil((double) data.length / chunkSize);
        // Create a byte array to store the total number of chunks
        byte[] totalChunksBytes = new byte[2];
        totalChunksBytes[1] = (byte) (totalChunks & 0xff);
        totalChunksBytes[0] = (byte) ((totalChunks >> 8) & 0xff);
        byte[] idChumkBytes = new byte[2];
        idChumkBytes[1] = (byte) (id & 0xff);
        idChumkBytes[0] = (byte) ((id >> 8) & 0xff);
        // Iterate through each chunk
        for (short chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
            // Create a byte array to store the current chunk index
            byte[] currentChunkIndexBytes = new byte[2];
            currentChunkIndexBytes[1] = (byte) ((chunkIndex + 1) & 0xff);
            currentChunkIndexBytes[0] = (byte) (((chunkIndex + 1) >> 8) & 0xff);
            // Calculate the offset of the current chunk
            int chunkOffset = chunkIndex * chunkSize;
            // Calculate the length of the current chunk
            int chunkLength = Math.min(data.length - chunkOffset, chunkSize);
            // Create a byte array to store the current chunk
            byte[] chunkBytes = new byte[chunkLength + 6]; // 4 dataBytes for short index and short chunkLength
            // Store the current chunk index in the chunkBytes array
            chunkBytes[chunkLength] = currentChunkIndexBytes[0];
            chunkBytes[chunkLength + 1] = currentChunkIndexBytes[1];
            // Store the total number of chunks in the chunkBytes array
            chunkBytes[chunkLength + 2] = totalChunksBytes[0];
            chunkBytes[chunkLength + 3] = totalChunksBytes[1];
            // Store the id of the current chunk in the chunkBytes array
            chunkBytes[chunkLength + 4] = idChumkBytes[0];
            chunkBytes[chunkLength + 5] = idChumkBytes[1];
            // Copy the dataBytes into the chunkBytes array
            System.arraycopy(data, chunkOffset, chunkBytes, 0, chunkLength);
            chunked.add(chunkBytes);
        }
    }

    /**
     * Returns an {@link Iterator} over the chunks;
     *
     * @return an {@link Iterator} over the chunks
     */
    public Iterator<byte[]> newIterator() {
        return chunked.iterator();
    }
}
