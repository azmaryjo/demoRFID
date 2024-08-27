package com.example.demoRFID.Model;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Composite primary key class for the RfidTx entity.
 * This class represents the composite key consisting of tagId, epc, and scanDate fields.
 * It implements Serializable to ensure that instances of this class can be serialized,
 * as required by JPA for composite key classes.
 *
 * Fields:
 * - tagId: The unique identifier of the RFID tag.
 * - epc: The Electronic Product Code (EPC) associated with the RFID tag.
 * - scanDate: The date and time when the RFID tag was scanned.
 *
 * Constructors:
 * - RfidTxId(): Default constructor for creating an empty instance.
 * - RfidTxId(String tagId, String epc, LocalDateTime scanDate): Constructor for initializing all fields.
 *
 * Annotations:
 * - @Data: Generates getter, setter, toString, equals, and hashCode methods automatically.
 */
@Data
public class RfidTxId implements Serializable {
    private String tagId;
    private String epc;
    private LocalDateTime scanDate;

    public RfidTxId() {
    }

    public RfidTxId(String tagId, String epc, LocalDateTime scanDate) {
        this.tagId = tagId;
        this.epc = epc;
        this.scanDate = scanDate;
    }

}
