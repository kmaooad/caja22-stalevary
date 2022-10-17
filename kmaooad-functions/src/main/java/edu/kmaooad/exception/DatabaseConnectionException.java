package edu.kmaooad.exception;

public class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException(String message) {
        super("Connection exception: \n" + message);
    }
}
