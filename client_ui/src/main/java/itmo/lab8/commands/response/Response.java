package itmo.lab8.commands.response;

import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * This class is used to represent server response.
 */
public class Response implements Serializable {

    @Serial
    private static final long serialVersionUID = 1111185098267757690L;
    private final byte[] responseMessage;
    private final ResponseType responseType;

    /**
     * Constructor for Response object
     *
     * @param responseMessage the message to be sent in the response
     * @param responseType    the type of response to be sent
     */
    public Response(String responseMessage, ResponseType responseType) {
        this(responseMessage.getBytes(), responseType);
    }

    /**
     * Constructor for Response class.
     *
     * @param objects      An array of objects to be included in the response.
     * @param responseType The type of response.
     */
    public Response(byte[] objects, ResponseType responseType) {
        this.responseMessage = objects;
        this.responseType = responseType;
    }

    /**
     * Returns the response message with the appropriate color based on the response type.
     *
     * @return the response message with the appropriate color
     */
    public byte[] getMessage() {
        return responseMessage;
    }

    public String getStringMessage() {
        return new String(responseMessage, StandardCharsets.UTF_8);
    }

    /**
     * Gets the response type of the response
     *
     * @return the response type
     */
    public ResponseType getType() {
        return responseType;
    }
}
