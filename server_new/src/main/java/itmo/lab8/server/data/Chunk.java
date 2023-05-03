package itmo.lab8.server.data;


import java.nio.ByteBuffer;
import java.util.Arrays;

public class Chunk implements Comparable<Chunk> {
    private final byte[] chunk;
    private short id;
    private short index;
    private short total;

    public Chunk(byte[] chunk) {
        this.chunk = Arrays.copyOfRange(chunk, 0, chunk.length - 6);
        byte[] chunkInfoBytes = Arrays.copyOfRange(chunk, chunk.length - 6, chunk.length);
        ByteBuffer chunkInfoBuffer = ByteBuffer.wrap(chunkInfoBytes);
        chunkInfoBuffer.position(0);
        index = chunkInfoBuffer.getShort(1);
        total = chunkInfoBuffer.getShort(2);
        id = chunkInfoBuffer.getShort(3);
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

    // TODO: DELETE THIS COMMENT SECTION:
//        public static void main(String[] args) {
//            long timestamp = System.currentTimeMillis();
//            short value = (short) ((timestamp - new Date(0).getTime()) / 1000);
//            short value1 = (short) ((timestamp - new Date(0).getTime()) / 1000);
//            System.out.println(value1);
//            System.out.println(value);
//        }
}
