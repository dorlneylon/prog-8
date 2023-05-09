package itmo.lab8.shared;


import java.nio.ByteBuffer;
import java.util.Arrays;

public class Chunk implements Comparable<Chunk> {
    private final byte[] chunk;
    private final short id;
    private final short index;
    private final short total;

    /**
     * Constructor for Chunk.
     *
     * @param chunk The byte array containing the chunk data.
     */
    public Chunk(byte[] chunk) {
        this.chunk = Arrays.copyOfRange(chunk, 0, chunk.length - 6);
        byte[] chunkInfoBytes = Arrays.copyOfRange(chunk, chunk.length - 6, chunk.length);
        // Parsing meta-data of the chunk
        ByteBuffer chunkInfoBuffer = ByteBuffer.wrap(chunkInfoBytes);
        chunkInfoBuffer.position(0);
        index = chunkInfoBuffer.getShort();
        total = chunkInfoBuffer.getShort();
        id = chunkInfoBuffer.getShort();
    }

    public byte[] getChunk() {
        return chunk;
    }

    public short getId() {
        return id;
    }

    public short getIndex() {
        return index;
    }

    public short getTotal() {
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chunk chunk = (Chunk) o;
        return this.id == chunk.id && this.index == chunk.index && this.total == chunk.total && Arrays.equals(this.chunk, chunk.chunk);
    }

    @Override
    public int compareTo(Chunk chunk) throws IllegalArgumentException {
        if (this.id != chunk.id) {
            throw new IllegalArgumentException("Chunk id mismatch");
        }
        if (this.total != chunk.total) {
            throw new IllegalArgumentException("Chunk total mismatch");
        }
        return Short.compare(this.index, chunk.index);
    }
}
