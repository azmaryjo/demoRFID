package com.example.demoRFID.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key class for the Rfid entity.
 * This class represents the composite key consisting of tagId and epc fields.
 * It implements Serializable to ensure that instances of this class can be serialized,
 * as required by JPA for composite key classes.
 *
 * Fields:
 * - tagId: The unique identifier of the RFID tag, mapped to the "TagID" column in the database.
 * - epc: The Electronic Product Code (EPC) associated with the RFID tag, mapped to the "EPC" column in the database.
 *
 * Annotations:
 * - @Data: Generates getter, setter, toString, equals, and hashCode methods automatically.
 * - @NoArgsConstructor: Generates a no-argument constructor.
 * - @AllArgsConstructor: Generates an all-argument constructor.
 * - @Column(name = "TagID"): Maps the tagId field to the "TagID" column in the database.
 * - @Column(name = "EPC"): Maps the epc field to the "EPC" column in the database.
 *
 * Overrides:
 * - toString(): Provides a string representation of the RfidId object, including the tagId and epc fields.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RfidId implements Serializable {

    @Column(name = "TagID")
    private String tagId;

    @Column(name = "EPC")
    private String epc;

    @Override
    public String toString() {
        return "RfidId{" +
                "tagId='" + tagId + '\'' +
                ", epc='" + epc + '\'' +
                '}';
    }
}
