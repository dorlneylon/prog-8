package itmo.lab8.client_ui.basic.utils.types;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SubArrayIterator<T> implements Iterator<T[]> {
    private final T[] array;
    private final int batchSize;
    private int index = 0;

    public SubArrayIterator(T[] array, int batchSize) {
        this.array = array;
        this.batchSize = batchSize;
    }

    @Override
    public boolean hasNext() {
        return index < array.length;
    }

    @Override
    public T[] next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        int endIndex = Math.min(index + batchSize, array.length);
        T[] batch = Arrays.copyOfRange(array, index, endIndex);
        index += batchSize;

        return batch;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
