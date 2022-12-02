package edu.kmaooad.exception;

public class IncorrectIdException extends Exception {

    public static final String MESSAGE = "Incorrect entity id";

    public IncorrectIdException() {
        super(MESSAGE);
    }
}

