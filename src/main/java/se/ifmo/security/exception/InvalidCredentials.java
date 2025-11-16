package se.ifmo.security.exception;

public class InvalidCredentials extends Exception {
    public InvalidCredentials(String message) {
        super(message);
    }
}
