package exception;

public class InputParsingException extends RuntimeException {
    public InputParsingException(String message, Exception cause) {
        super(message, cause);
    }
}
