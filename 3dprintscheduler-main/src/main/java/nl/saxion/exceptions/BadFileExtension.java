package nl.saxion.exceptions;

public class BadFileExtension extends RuntimeException {
    public BadFileExtension(String message) {
        super(message);
    }
}
