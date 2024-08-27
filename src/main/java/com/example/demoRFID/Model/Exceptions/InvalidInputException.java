package com.example.demoRFID.Model.Exceptions;
/**
 * Custom exception class that is thrown when an invalid input is encountered.
 * This exception extends the RuntimeException class, allowing it to be thrown
 * without requiring explicit declaration in the method signature.
 *
 * @param message A descriptive message providing details about the invalid input.
 */
public class InvalidInputException extends RuntimeException {
    public InvalidInputException(String message) {
        super(message);
    }
}

