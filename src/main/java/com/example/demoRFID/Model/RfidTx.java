package com.example.demoRFID.Model;

import com.example.demoRFID.Utils.ConversionUtils;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing an RFID transaction in the system.
 * This class is mapped to the "RFID_Tx" table in the database and uses a composite primary key defined by the RfidTxId class.
 * It uses Lombok's @Data annotation to automatically generate getter, setter, toString, equals,
 * and hashCode methods.
 *
 * Fields:
 * - tagId: The unique identifier of the RFID tag. It is part of the composite primary key.
 * - epc: The Electronic Product Code (EPC) associated with the RFID tag. It is also part of the composite primary key.
 * - scanDate: The date and time when the RFID tag was scanned. It is part of the composite primary key.
 * - rfid: A many-to-one relationship with the Rfid entity, representing the RFID tag associated with this transaction.
 *   The join columns tagId and epc are mapped to the corresponding fields in the Rfid entity with insertable and updatable set to false.
 * - location: A many-to-one relationship with the Location entity, representing the location where the RFID tag was scanned.
 * - rssi: The Received Signal Strength Indicator (RSSI) value for the RFID tag at the time of the transaction.
 *
 * Annotations:
 * - @Entity: Specifies that this class is an entity and is mapped to a database table.
 * - @Table(name = "RFID_Tx"): Specifies the name of the table in the database that this entity is mapped to.
 * - @IdClass(RfidTxId.class): Specifies that this entity has a composite primary key defined by the RfidTxId class.
 * - @Id: Marks the tagId, epc, and scanDate fields as part of the composite primary key.
 * - @Column(name = "tagId"): Maps the tagId field to the "tagId" column in the database.
 * - @Column(name = "epc"): Maps the epc field to the "epc" column in the database.
 * - @Column(name = "scanDate"): Maps the scanDate field to the "scanDate" column in the database.
 * - @ManyToOne: Indicates a many-to-one relationship between RfidTx and another entity (Rfid or Location).
 * - @JoinColumns: Specifies multiple join columns for the relationship with the Rfid entity.
 * - @JoinColumn(name = "locationId"): Specifies the foreign key column for the relationship with the Location entity.
 *
 * Overrides:
 * - toString(): Provides a string representation of the RfidTx object, including tagId, epc, scanDate, refCode (from the associated Product),
 *   Site and Location (formatted using a utility), and rssi.
 */


@Entity
@Table(name = "RFID_Tx")
@IdClass(RfidTxId.class)
@Data
public class RfidTx {
    @Id
    @Column(name = "tagId")
    private String tagId;

    @Id
    @Column(name = "epc")
    private String epc;

    @Id
    @Column(name = "scanDate")
    private LocalDateTime scanDate;


    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "tagId", referencedColumnName = "tagId", insertable = false, updatable = false),
            @JoinColumn(name = "epc", referencedColumnName = "epc", insertable = false, updatable = false)
    })
    private Rfid rfid;

    @ManyToOne
    @JoinColumn(name = "locationId")
    private Location location;

    private BigDecimal rssi;

    @Override
    public String toString() {
        return "RfidTx{" +
                "tagId='" + tagId + '\'' +
                ", epc='" + epc + '\'' +
                ", scanDate=" + scanDate +
                ", refCode=" + rfid.getProduct().getRefCode() +
                ",Site and Location=" + ConversionUtils.convertStringCompositeNamesToShow(location.getLocationName()+" - "+location.getSite().getSiteName()) +
                ", rssi=" + rssi +
                "} \n";
    }
}
