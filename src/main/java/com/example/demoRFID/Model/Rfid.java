package com.example.demoRFID.Model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity class representing an RFID (Radio Frequency Identification) entry in the system.
 * This class is mapped to the "RFID" table in the database and uses a composite primary key defined by the RfidId class.
 * It uses Lombok's @Data annotation to automatically generate getter, setter, toString, equals,
 * and hashCode methods.
 *
 * Fields:
 * - tagId: The unique identifier of the RFID tag. It is part of the composite primary key.
 * - epc: The Electronic Product Code (EPC) associated with the RFID tag. It is also part of the composite primary key.
 * - product: A many-to-one relationship with the Product entity, representing the product associated with this RFID tag.
 *   This is mapped using the @ManyToOne annotation, with the foreign key in the "RFID" table being "refCode".
 *
 * Annotations:
 * - @Entity: Specifies that this class is an entity and is mapped to a database table.
 * - @Table(name = "RFID"): Specifies the name of the table in the database that this entity is mapped to.
 * - @IdClass(RfidId.class): Specifies that this entity has a composite primary key defined by the RfidId class.
 * - @Id: Marks the tagId and epc fields as part of the composite primary key.
 * - @Column(name = "tagId"): Maps the tagId field to the "tagId" column in the "RFID" table.
 * - @Column(name = "epc"): Maps the epc field to the "epc" column in the "RFID" table.
 * - @ManyToOne: Indicates a many-to-one relationship between Rfid and Product.
 * - @JoinColumn(name = "refCode", referencedColumnName = "refCode"): Specifies the foreign key column and the column it references in the Product table.
 */
@Entity
@Table(name = "RFID")
@Data
@IdClass(RfidId.class)
public class Rfid {

    @Id
    @Column(name = "tagId")
    private String tagId;

    @Id
    @Column(name = "epc")
    private String epc;

    @ManyToOne
    @JoinColumn(name = "refCode", referencedColumnName = "refCode")
    private Product product;


}