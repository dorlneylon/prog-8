package itmo.lab8.client_ui.basic.utils.parser.exceptions;

import java.lang.reflect.Type;

public class ObjectParsingException extends RuntimeException {
    public ObjectParsingException(Type type) {
        super("Unable to parse object of type " + type.getTypeName());
    }
}
