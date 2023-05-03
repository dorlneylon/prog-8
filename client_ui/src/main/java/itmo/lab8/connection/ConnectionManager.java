package itmo.lab8.connection;

import itmo.lab8.basic.utils.serializer.Serializer;
import itmo.lab8.commands.Command;
import itmo.lab8.commands.Request;
import itmo.lab8.commands.response.Response;
import itmo.lab8.core.AppCore;

import java.util.concurrent.ConcurrentHashMap;


public class ConnectionManager {
    private static ConnectionManager instance;

    private ConcurrentHashMap<Long, Response> responseQueue = new ConcurrentHashMap<>();


    public static ConnectionManager getInstance() {
        if (instance == null) {
            instance = new ConnectionManager();
        }
        return instance;
    }

    private long generateOperationId() {
        return System.currentTimeMillis();
    }

    public void start() {
        ReceiverThread receiverThread = new ReceiverThread();
        receiverThread.start();
    }

    public long newOperation(Command command) throws Exception {
        long operationId = generateOperationId();
        Request request = new Request(command, AppCore.getInstance().getName(), operationId);
        ConnectorSingleton.getInstance().send(Serializer.serialize(request));
        return operationId;
    }

    public Response waitForResponse(long operationId) {
        while (!responseQueue.containsKey(operationId)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Response response = responseQueue.get(operationId);
        responseQueue.remove(operationId);
        return response;
    }

    public Response getResponse(long operationId) {
        return null;
    }


    private class ReceiverThread extends Thread {
        public ReceiverThread() {
            super(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Response response = ConnectorSingleton.getInstance().receive();
                        if (response != null) {
                            responseQueue.put(response.getOperationId(), response);
                        }
                        Thread.sleep(100);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
    }
}
