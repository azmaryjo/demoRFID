package com.example.demoRFID.Repository;

import com.example.demoRFID.Model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * Method to check if a location with the specified name exists in the database.
     * This method queries the database to determine whether there is an entry in the
     * Location entity with the given location name.
     *
     * @param locationName The name of the location to check for existence.
     * @return true if a location with the specified name exists, false otherwise.
     */
    boolean existsByLocationName(String locationName);

    /**
     * Custom query method to find the location ID based on the location name.
     * This method uses a JPQL query to retrieve the ID of a location entity that matches
     * the specified location name.
     *
     * @param locationName The name of the location for which to find the ID.
     * @return The ID of the location if found, or null if no location with the specified name exists.
     */
    @Query("SELECT l.locationId FROM Location l WHERE l.locationName = :locationName")
    Long findLocationIdByLocationName(@Param("locationName") String locationName);


    /**
     * Custom query method to check if a location with a specified name exists and is associated
     * with a given site name. This method uses a JPQL query to determine whether there is an entry
     * in the Location entity that matches both the location name and the site name.
     *
     * @param locationName The name of the location to check.
     * @param siteName The name of the site to check.
     * @return An Optional containing the Location entity if a match is found, or an empty Optional if no match exists.
     */
    @Query(value="SELECT l FROM Location l " +
            "JOIN Site s ON l.site.siteId = s.id " +
            "WHERE l.locationName = :locationName AND s.siteName = :siteName")
    Optional<Location> existsByLocationNameAndSiteName(@Param("locationName") String locationName,
                                                       @Param("siteName") String siteName);
}
