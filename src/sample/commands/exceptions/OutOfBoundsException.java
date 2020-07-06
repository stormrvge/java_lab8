package sample.commands.exceptions;

/**
 * Exception class which uses for coordinates and locations.
 */
public class OutOfBoundsException extends Exception {
    public OutOfBoundsException() {
        super("Out of bounds");
    }
}
