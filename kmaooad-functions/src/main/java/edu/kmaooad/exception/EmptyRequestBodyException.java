package edu.kmaooad.exception;

public class EmptyRequestBodyException extends Exception {

    public static final String MESSAGE = "Incorrect request body";

    public EmptyRequestBodyException() {
        super(MESSAGE);
    }
}
