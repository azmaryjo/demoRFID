package com.example.demoRFID.Model.Request;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) representing a request to transmit RFID data.
 * This class uses Lombok's @Data annotation to automatically generate getter, setter, toString, equals,
 * and hashCode methods.
 *
 * Fields:
 * - siteName: The name of the site where the RFID tag was scanned.
 * - epc: The Electronic Product Code (EPC) of the RFID tag.
 * - refCode: A reference code associated with the scanned RFID tag.
 * - tagId: The unique identifier of the RFID tag.
 * - locationName: The name of the location where the RFID tag was scanned.
 * - rssi: The Received Signal Strength Indicator (RSSI) value for the RFID tag, indicating signal strength.
 * - scanDate: The date and time when the RFID tag was scanned, typically in a string format.
 */
@Data
public class RfidTxRequest {

    private String siteName;
    private String epc;
    private String refCode;
    private String tagId;
    private String locationName;
    private BigDecimal rssi;
    private String scanDate;

}
