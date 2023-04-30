package itmo.lab8.connection;

import itmo.chunker.ChuckReceiver;
import itmo.chunker.Chunker;
import itmo.lab8.basic.utils.serializer.Serializer;
import itmo.lab8.basic.utils.terminal.Colors;
import itmo.lab8.commands.response.Response;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Connector is used to connect to a remote server
 */
public class ConnectorSingleton {

    private static ConnectorSingleton instance;
    private final static int socketTimeout = 16000;
    /**
     * Socket for UDP connection
     */
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private int chunkSize;


    /**
     * Connector constructor with specified server port
     *
     * @param port server port
     * @throws Exception Socket exception
     */
    public void setConnectorValues(InetAddress address, int port) throws Exception {
        socket = new DatagramSocket();
        socket.setSoTimeout(socketTimeout);
        this.address = address;
        this.port = port;
    }

    public static ConnectorSingleton newInstance(InetAddress address, int port) throws Exception {
        instance = new ConnectorSingleton();
        instance.setConnectorValues(address, port);
        return instance;
    }

    public static ConnectorSingleton getInstance() {
        if (instance == null) {
            instance = new ConnectorSingleton();
        }
        return instance;
    }

    /**
     * Return port of client
     *
     * @return localhost port
     */
    public int getPort() {
        return socket.getLocalPort();
    }

    public void setBufferSize(int size) throws SocketException {
        socket.setReceiveBufferSize(size);
        socket.setSendBufferSize(size);
        chunkSize = 1024;
    }

    /**
     * Sends string to remote server
     *
     * @param message string message
     * @throws Exception sending exception
     */
    public void send(String message) throws Exception {
        this.send(message.getBytes());
    }

    /**
     * Sends dataBytes to remote server
     *
     * @throws Exception sending exceptions
     */
    public void send(byte[] dataBytes) throws Exception {
        Chunker dataChunker = new Chunker(dataBytes, chunkSize);
        var chunkIterator = dataChunker.newIterator();
        short totalChunks = (short) ((int) Math.ceil((double) dataBytes.length / (double) this.chunkSize));
        int c = 0;
        while (chunkIterator.hasNext()) {
            if (++c % 50 == 0) {
                System.out.printf("%sSending chunks:%s %d/%d kb\r", Colors.AsciiPurple, Colors.AsciiReset, c, totalChunks);
                Thread.sleep(100);
            }
            byte[] chunk = chunkIterator.next();
            DatagramPacket dataPacket = new DatagramPacket(chunk, chunk.length, this.address, port);
            socket.send(dataPacket);
        }
    }

    /**
     * Receives bytes from remote server and transforms them into string
     *
     * @return string message
     * @throws IOException Receiving exception
     */
    public Response receive() throws IOException {
        ChuckReceiver receiver = new ChuckReceiver();
        byte[] buffer = new byte[chunkSize + 4];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        do {
            socket.receive(packet);
            receiver.add(Arrays.copyOf(packet.getData(), packet.getLength()));
        } while (!receiver.isReceived());
        return (Response) Serializer.deserialize(receiver.getAllChunks());
    }
}
