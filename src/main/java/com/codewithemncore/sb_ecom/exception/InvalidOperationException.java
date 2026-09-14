package com.codewithemncore.sb_ecom.exception;

/**
 * Thrown when a business operation is attempted that would violate domain integrity rules,
 * such as deleting a resource that still has active dependents.
 */
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
