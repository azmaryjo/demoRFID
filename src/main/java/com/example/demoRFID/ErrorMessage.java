package com.example.demoRFID;

import lombok.Getter;
/**
 * The `ErrorMessage` enum defines a set of standardized error messages to be used across the application.
 * Each enum constant represents a specific type of error or message, providing a consistent way to handle
 * error reporting, logging, and user notifications.
 *
 * The `ErrorMessage` enum enhances the readability and maintainability of the application by replacing
 * hardcoded error messages with well-defined, reusable message templates.
 *
 * Each enum constant is associated with a specific error message string
 * The `format` method allows for dynamic insertion of arguments into the message templates,
 * making it flexible for various contexts and user scenarios.
 *
 * Usage:
 * - The error messages can be used in exception handling to provide more context-specific messages to the user.
 * - They can be logged to help developers quickly identify the nature of the issues within the application.
 * - Error messages can be returned to the client in API responses, allowing for better error communication and handling on the client side.
 */
@Getter
public enum ErrorMessage {
    DATE_FORMAT("Date should look like yyyy-MM-dd HH:mm:ss \n"),
    EPC_FORMAT("EPC should look like 'EPC' followed by 3 digits \n"),
    SITE_NAME_FORMAT("Site name cannot be empty"),
    SITE_ID_FORMAT("Site id cannot be empty"),
    PRODUCT_NOT_FOUND("Product with refCode: %s not found"),
    PRODUCT_ALREADY_EXISTS("Product with refCode: %s already exists"),
    INVALID_REFCODE("RefCode should be %s digits long"),
    NO_PRODUCTS("Looks like you have no products yet"),
    NO_SITES("Looks like you have no sites yet"),
    PRODUCT_DELETE_SUCCESS("Successfully deleted product with RefCode %s"),
    PRODUCT_DELETE_FAILURE("Cannot delete product because it is referenced by other items"),
    NO_TRANSACTIONS("There are no transactions that match your filter"),
    N_FORMAT("N should be a positive integer greater than 0 \n"),
    TAG_ID_FORMAT("Tag Id should look like 'TAG' followed by 3 digits\n"),
    EMPTY_FIELDS("The following fields are empty: %s"),
    LOCATION_NAME_DOESNT_EXIST("Location Name doesn't exist"),
    SITE_NAME_DOESNT_EXIST("Site Name doesn't exist"),
    LOCATION_NOT_IN_SITE("Location Name doesnt belong in the site"),
    TAG_ID_EPC_NO_MATCH("The provided tag id and epc do not match"),
    REF_CODE_TAG_ID_EPC_NO_MATCH("the provided refcode does not belong to the tagid and epc"),
    RFIDTX_EPC_NOT_FOUND("RFID transactions with epc %s not found"),
    RFIDTX_TAG_ID_NOT_FOUND("RFID transactions with tagId %s not found"),
    RFIDTX_TAG_ID_EPC_NOT_FOUND("RFID transactions not found with tagId: %s and epc: %s"),
    RFIDTX_DATE_NOT_FOUND("RFID transactions not found between : %s and %s"),
    DATE_ORDER_ERROR("Start Date should occur before End date"),
    RFID_TX_DELETE_FAILURE("Cannot delete Transaction because it is does not exist"),
    RFID_TX_ADD_FAILURE("Cannot add Transaction because it already exists"),
    EMPTY_RFIDTX_FIELDS("The following fields cannot be empty tagId, EPC, Scan Date"),
    RFIDTX_DELETE_SUCCESS("Successfully deleted rfitx with tagId: %s, epc: %s and scanDate: %s"),
    RFIDTX_NOT_FOUND("RfidTx not found"),
    RFIDTX_SUCCESS("Successfully retrieved RfidTx By Criteria"),
    RFID_DEL_SUCCESS("Successfully deleted RfidTx"),
    SITE_ALREADY_EXISTS("Site with id: %s already exists"),
    PRODUCT_SUCCESS("Product with refcode %s saved successfully"),
    SITE_SUCCESS("Site with id %s saved successfully"),
    SITE_DEL_SUCCESS("Site with id %s was deleted successfully"),
    SITE_UPDATE_SUCCESS("Site with id %s updated successfully"),
    SITE_FETCH_SUCCESS("Found Site with id: %s"),
    SITE_FETCH_ALL_SUCCESS("Found %s Sites"),
    PRODUCT_FETCH_SUCCESS("Found Product with refCode: %s"),
    SITE_NOT_FOUND("Site with id: %s not found"),
    PRODUCT_FETCH_ALL_SUCCESS("Found %s Products"),
    PRODUCT_UPDATE_SUCCESS("Product with RefCode %s updated successfully"),
    PRODUCT_DEL_SUCCESS("Product with RefCode %s was deleted successfully"),
    INVALID_INPUT("Invalid input provided");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(message, args);
    }
}

