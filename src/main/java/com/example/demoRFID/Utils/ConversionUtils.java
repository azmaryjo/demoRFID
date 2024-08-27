package com.example.demoRFID.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.example.demoRFID.Constants.DATETIME_PATTERN;

public class ConversionUtils {
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /**
     * Converts a string representation of a date and time to a LocalDateTime object.
     * The input string must match the expected date and time format as specified by the formatter.
     *
     * @param dateTimeStr The string representation of the date and time to be converted.
     * @return A LocalDateTime object representing the parsed date and time.
     * @throws DateTimeParseException if the input string cannot be parsed due to incorrect format.
     */
    public static LocalDateTime convertStringToDate(String dateTimeStr){
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    /**
     * Converts a string by trimming it, converting it to uppercase,
     * and replacing spaces with the specified symbol.format for uniform db storage for location and site names.
     *
     * @param input the string to convert
     * @param replacement the symbol to replace spaces with
     * @return the converted string
     */
    public static String convertStringCompositeNames(String input, String replacement) {
        if (input == null) {
            return null;
        }

        // Trim the input, convert to uppercase, and replace spaces
        return input.trim().toUpperCase().replace(" ", replacement);
    }

    /**
     * Converts a composite string with specific separators into a more readable format for display.
     * This method trims the input string, replaces the ".." separator with a space,
     * and returns the modified string. If the input is null, the method returns null.
     *
     * @param input The composite string to be converted, where sections are separated by "..".
     * @return The formatted string with ".." replaced by a space, or null if the input is null.
     */
    public static String convertStringCompositeNamesToShow(String input) {
        if (input == null) {
            return null;
        }

        // Trim the input, convert to uppercase, and replace spaces
        return input.trim().replace("..", " ");
    }


    /**
     * Converts a string to an integer.
     *
     * @param str the string to convert
     * @param defaultValue the value to return if the string cannot be converted
     * @return the integer value of the string, or the default value if conversion fails
     */
    public static int convertStringToInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            System.out.println("Invalid string format for conversion to int: " + str);
            return defaultValue;
        }
    }
}
