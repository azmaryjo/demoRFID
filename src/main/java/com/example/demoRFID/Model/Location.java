package com.example.demoRFID.Model;

import jakarta.persistence.*;
import lombok.*;
/**
 * Entity class representing a Location in the system.
 * This class is mapped to the "LOCATION" table in the database.
 * It uses Lombok's @Data annotation to automatically generate getter, setter, toString, equals,
 * and hashCode methods.
 *
 * Fields:
 * - locationId: The unique identifier for the location, annotated with @Id to mark it as the primary key.
 * - locationName: The name of the location.
 * - site: A many-to-one relationship with the Site entity, representing the site associated with this location.
 *   This is mapped using the @ManyToOne annotation, and the foreign key in the "LOCATION" table is the "siteId".
 *
 * Annotations:
 * - @Entity: Specifies that this class is an entity and is mapped to a database table.
 * - @Table(name = "LOCATION"): Specifies the name of the table in the database that this entity is mapped to.
 * - @Id: Marks the locationId field as the primary key of the entity.
 * - @ManyToOne: Indicates a many-to-one relationship between Location and Site.
 * - @JoinColumn(name = "siteId", referencedColumnName = "siteId"): Specifies the foreign key column and the column it references.
 */
@Entity
@Table(name = "LOCATION")
@Data
public class Location {
    @Id
    private Long locationId;
    private String locationName;
    @ManyToOne
    @JoinColumn(name = "siteId", referencedColumnName = "siteId")
    private Site site;


}
