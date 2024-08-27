package com.example.demoRFID;
/**
 * The `Constants` class is a centralized container for defining constant values
 * that are used across the application. It primarily contains the maximum length
 * of various input fields to ensure consistency in validation and data handling
 * throughout the application.
 *
 * By using these constants, the application can maintain uniformity in input
 * validation, reduce the risk of hardcoding errors, and simplify updates to
 * input constraints by centralizing the definitions in one place.
 *
 * Example fields might include:
 *
 * - MAX_SITE_NAME_LENGTH: The maximum allowed length for a site name.
 * - MAX_LOCATION_NAME_LENGTH: The maximum allowed length for a location name.
 * - MAX_REF_CODE_LENGTH: The maximum allowed length for a reference code.
 * - MAX_TAG_ID_LENGTH: The maximum allowed length for an RFID tag ID.
 * - MAX_EPC_LENGTH: The maximum allowed length for an Electronic Product Code (EPC).
 *
 * Usage:
 * - These constants can be referenced in validation methods, ensuring that input
 *   data adheres to the specified constraints before being processed or stored.
 * - They help maintain consistent input validation rules across different components
 *   of the application, including service classes, controllers, and repository queries.
 */
public final class Constants {

    // Prevent instantiation
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }


    public static final int REFCODE_LENGTH = 5;
    public static final String DATETIME_PATTERN="yyyy-MM-dd HH:mm:ss";
    public static final int EPC_LENGTH=3;
    public static final int TAG_MIN_LENGTH=1;
    public static final int TAG_MAX_LENGTH=10;


}

