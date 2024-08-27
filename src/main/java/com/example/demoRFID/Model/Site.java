package com.example.demoRFID.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

/**
 * Entity class representing a Site in the system.
 * This class is mapped to the "SITE" table in the database.
 * It uses Lombok's @Data annotation to automatically generate getter, setter, toString, equals,
 * and hashCode methods.
 *
 * Fields:
 * - siteId: The unique identifier for the site, annotated with @Id to mark it as the primary key.
 * - siteName: The name of the site.
 *
 * Annotations:
 * - @Entity: Specifies that this class is an entity and is mapped to a database table.
 * - @Table(name = "SITE"): Specifies the name of the table in the database that this entity is mapped to.
 * - @Id: Marks the siteId field as the primary key of the entity.
 */
@Entity
@Table(name = "SITE")
@Data
public class Site {
    @Id
    private Long siteId;
    private String siteName;

}
