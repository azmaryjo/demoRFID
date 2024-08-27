package com.example.demoRFID.Model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
/**
 * Entity class representing a Product in the system.
 * This class is mapped to the "PRODUCT" table in the database.
 * It uses Lombok's @Data annotation to automatically generate getter, setter, toString, equals,
 * and hashCode methods.
 *
 * Fields:
 * - refCode: The unique identifier for the product, annotated with @Id to mark it as the primary key.
 * - name: The name of the product.
 *
 * Annotations:
 * - @Entity: Specifies that this class is an entity and is mapped to a database table.
 * - @Table(name = "PRODUCT"): Specifies the name of the table in the database that this entity is mapped to.
 * - @Id: Marks the refCode field as the primary key of the entity.
 *
 * Overrides:
 * - toString(): Overrides the default toString() method to return a JSON representation of the Product object
 *   using the Gson library.
 */
@Entity
@Table(name = "PRODUCT")
@Data
public class Product {
    @Id
    private Long refCode;
    private String name;

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }
}
