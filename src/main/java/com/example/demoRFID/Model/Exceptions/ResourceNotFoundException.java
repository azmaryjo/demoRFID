package com.example.demoRFID.Model.Exceptions;
/**
 * Custom exception class that is thrown when a requested resource is not found.
 * This exception extends the RuntimeException class, allowing it to be thrown
 * without requiring explicit declaration in the method signature.
 *
 * @param message A descriptive message providing details about the missing resource.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

