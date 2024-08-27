package com.example.demoRFID.Model.Request;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) representing a request to update RFID (Radio Frequency Identification) data.
 * This class uses Lombok's @Data annotation to automatically generate getter, setter, toString, equals,
 * and hashCode methods.
 *
 * Fields:
 * - siteName: The name of the site where the RFID tag is located.
 * - locationName: The name of the new location where the RFID tag is being updated.
 * - rssi: The Received Signal Strength Indicator (RSSI) value for the RFID tag, indicating the updated signal strength.
 */
@Data
public class RfidTxUpdateRequest {

    private String siteName;
    private String locationName;
    private BigDecimal rssi;


}
