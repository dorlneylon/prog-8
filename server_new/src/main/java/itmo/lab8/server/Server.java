package itmo.lab8.server;

import itmo.lab8.basic.moviecollection.MovieCollection;
import itmo.lab8.database.Database;
import itmo.lab8.server.handlers.ReceiveHandler;
import itmo.lab8.server.threads.ChunkPoolListener;
import itmo.lab8.server.threads.ExitThread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * The {@code Server} class is responsible for handling incoming requests from clients.
 * It is a singleton class, meaning that only one instance of it can exist at any given time.
 */
public class Server {
    private static Server instance;
    private final Database database;
    private final MovieCollection collection;
    private final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final ChunkPoolListener chunkPoolListener = new ChunkPoolListener();
    private DatagramChannel channel;


    /**
     * Gets the singleton instance of the {@link Server} class.
     *
     * @param database The {@link Database} to use for the {@link Server} instance.
     * @return The singleton instance of the {@link Server} class.
     */
    public static Server getInstance(Database database) {
        if (instance == null) {
            instance = new Server(database);
        }
        return instance;
    }

    /**
     * Returns the singleton instance of the {@link Server} class.
     *
     * @return The singleton instance of the {@link Server} class.
     */
    public static Server getInstance() {
        return instance;
    }

    /**
     * Constructor for Server class.
     *
     * @param database The database to be used by the server.
     */
    public Server(Database database) {
        this.database = database;
        collection = database.getCollection();
    }

    /**
     * Returns the Database object associated with this instance.
     *
     * @return the Database object associated with this instance
     */
    public Database getDatabase() {
        synchronized (this.database) {
            return database;
        }
    }

    // TODO: Не знаю, будет так работать или нет, но попробовать стоит
    public DatagramChannel getChannel() {
        return channel;
    }

    /**
     * Returns the MovieCollection
     *
     * @return the MovieCollection
     */
    public MovieCollection getCollection() {
        return collection;
    }

    public void run(int port) {
        ExitThread exitThread = new ExitThread();
        exitThread.start();
        try {
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.socket().bind(new InetSocketAddress(port));
            channel.setOption(StandardSocketOptions.SO_RCVBUF, 65536);
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);
            ServerLogger.getInstance().log(Level.INFO, "Server started on port " + port);
            chunkPoolListener.start();
            while (true) {
                selector.select();
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isReadable()) {
                        threadPool.submit(new ReceiveHandler(key));
                    }

                }
            }
        } catch (IOException e) {
            chunkPoolListener.interrupt();
            threadPool.shutdown();
            throw new RuntimeException(e);

        }
    }

}
