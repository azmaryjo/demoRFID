package com.example.demoRFID;

import lombok.Getter;
/**
 * The `ErrorCode` enum defines a set of standardized error codes to be used across the application.
 * Each enum constant represents a specific type of error, providing a unique code that can be logged
 * or returned in error responses, ensuring consistent error handling and debugging.
 *
 * The `ErrorCode` enum enhances the readability and maintainability of the application by replacing
 * hardcoded error messages with well-defined, reusable error codes.
 *
 * Each enum constant is associated with a specific error message
 * Each error code is associated with a unique message string that follows a consistent pattern,
 * typically indicating the error category (e.g., RFIDTX, PRODUCT, SITE) and the specific issue (e.g., IN for input errors, RES for resource errors).
 *
 * Usage:
 * - The error codes can be used in exception handling to provide more context-specific error messages.
 * - They can be logged to help developers quickly identify the nature and location of issues within the application.
 * - Error codes can also be returned to the client in API responses, allowing for better error tracking and handling on the client side.
 */
@Getter
public enum ErrorCode {
    RFIDTX_INV_IN("ERR-RFIDTX-IN-001"),
    RFIDTX_RES_NOT_FOUND("ERR-RFIDTX-RES-002"),
    RFIDTX_DATA_INT("ERR-RFIDTX-ID-003"),
    PRODUCT_INV_IN("ERR-PRODUCT-IN-001"),
    PRODUCT_RES_NOT_FOUND("ERR-PRODUCT-RES-002"),
    PRODUCT_DATA_INT("ERR-PRODUCT-RES-003"),
    SITE_INV_IN("ERR-SITE-IN-001"),
    SITE_RES_NOT_FOUND("ERR-SITE-RES-002"),
    SITE_DATA_INT("ERR-SITE-RES-003"),
    RFID_INV_IN("ERR-RFID-IN-001"),;

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }


}

