package itmo.lab8.server.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

public class BlockingChunkList {

    private final List<Chunk> list = new ArrayList<>();
    private final Semaphore semaphore = new Semaphore(1);

    public void add(Chunk element) throws InterruptedException {
        while (!semaphore.tryAcquire()) {
            Thread.sleep(100);
        }
        semaphore.acquire();
        try {
            list.add(element);
        } finally {
            semaphore.release();
        }
    }

    public Chunk get(int index) throws InterruptedException {
        while (!semaphore.tryAcquire()) {
            Thread.sleep(100);
        }
        semaphore.acquire();
        try {
            return list.get(index);
        } finally {
            semaphore.release();
        }
    }

    public boolean isEmpty() throws InterruptedException {
        semaphore.acquire();
        try {
            return list.isEmpty();
        } finally {
            semaphore.release();
        }
    }

    public int size() throws InterruptedException {
        semaphore.acquire();
        try {
            return list.size();
        } finally {
            semaphore.release();
        }
    }

    public byte[] summarizeChunks() throws InterruptedException {
        if (this.isEmpty()) {
            return null;
        }
        if (this.get(0).getTotal() != this.size()) {
            return null;
        }
        semaphore.acquire();
        byte[] result;
        try {
            Collections.sort(list);
            int size = list.stream().mapToInt(o -> o.getChunk().length).sum();
            result = new byte[size];
            int offset = 0;
            for (Chunk chunk : list) {
                System.arraycopy(chunk.getChunk(), 0, result, offset, chunk.getChunk().length);
                offset += chunk.getChunk().length;
            }
        } finally {
            semaphore.release();
        }
        return result;
    }
}
