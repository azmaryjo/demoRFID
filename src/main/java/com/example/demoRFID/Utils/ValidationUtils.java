package com.example.demoRFID.Utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.example.demoRFID.Constants.*;

public class ValidationUtils {


    /**
     * Checks if a given string is null or empty.
     *
     * @param input the string to check
     * @return true if the string is null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }
    /**
     * Checks if a given BigDecimal is null or zero.
     *
     * @param value the BigDecimal to check
     * @return true if the BigDecimal is null or zero, false otherwise
     */
    public static boolean isNullOrZero(BigDecimal value) {
        return value == null || BigDecimal.ZERO.compareTo(value) == 0;
    }
    /**
     * Checks if two LocalDateTime objects are in chronological order.
     * This method returns true if the start date is before or equal to the end date.
     *
     * @param startDate The start date and time to check.
     * @param endDate The end date and time to check.
     * @return true if the startDate is before or equal to the endDate, false otherwise.
     */
    public static boolean areDatesInOrder(LocalDateTime startDate, LocalDateTime endDate) {
        return (startDate.isBefore(endDate) || startDate.equals(endDate));
    }

    /**
     * Checks if a given date string is in the valid format yyyy-MM-dd HH:mm:ss.
     *
     * @param dateStr the date string to check
     * @return true if the date string is in the correct format, false otherwise
     */
    public static boolean isValidDateFormat(String dateStr) {
        if (isNullOrEmpty(dateStr)) {
            return false;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
        try {
            LocalDateTime.parse(dateStr, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Checks all string fields in the given object and returns a list of field names that are null or empty.
     *
     * @param obj the object to validate
     * @return a list of field names that are null or empty
     */
    public static List<String> getEmptyFieldNames(Object obj) {
        List<String> emptyFields = new ArrayList<>();

        if (obj == null) {
            emptyFields.add("Object is null");
            return emptyFields;
        }

        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);

                if (value instanceof String) {
                    String strValue = (String) value;
                    if (isNullOrEmpty(strValue)) {
                        emptyFields.add(field.getName());
                    }
                }
                else if (value instanceof BigDecimal) {
                    BigDecimal decimalValue = (BigDecimal) value;
                    if (isNullOrZero(decimalValue)) {
                        emptyFields.add(field.getName());
                    }
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return emptyFields;
    }

    /**
     * Validates if the input string follows the format "EPC" or "epc" followed by exactly EPC_LENGTH digits.
     *
     * @param input the string to validate
     * @return true if the input matches the format, false otherwise
     */
    public static boolean isValidEPCFormat(String input) {
        if (input == null) {
            return false;
        }

        // Regular expression to match "EPC" or "epc" followed by exactly 3 digits
        String regex = "^(EPC|epc)\\d{"+EPC_LENGTH+"}$";
        return input.matches(regex);
    }

    /**
     * Validates if the input string follows the format "TAG" or "tag" case insensitive followed by up to TAG_MAX_LENGTH digits.
     *
     * @param input the string to validate
     * @return true if the input matches the format, false otherwise
     */
    public static boolean isValidTagFormat(String input) {
        // Regular expression pattern to match "TAG" or "tag" followed by up to 10 digits
        String regex = "(?i)^tag\\d{"+TAG_MIN_LENGTH+","+TAG_MAX_LENGTH+"}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input).matches();
    }

    /**
     * Validates if the input string consists of exactly REFCODE_LENGTH numeric digits.
     *
     * @param input the string to validate
     * @return true if the string is exactly REFCODE_LENGTH numeric digits, false otherwise
     */
    public static boolean isValidRefCode(String input) {
        if (input == null) {
            return false;

        }

        // Regular expression to match exactly 5 numeric digits
        String regex = "^\\d{"+REFCODE_LENGTH+"}$";
        return input.matches(regex);
    }

    /**
     * Checks if a given integer is positive.
     * This method returns true if the number is greater than zero.
     *
     * @param number The integer to check.
     * @return true if the number is positive, false otherwise.
     */
    public static boolean isPositiveInteger(int number) {
        return number > 0;
    }



}
