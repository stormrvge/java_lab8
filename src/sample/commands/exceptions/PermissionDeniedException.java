package sample.commands.exceptions;

public class PermissionDeniedException extends Exception {
    public PermissionDeniedException() {
        super("Permission denied.");
    }
}
