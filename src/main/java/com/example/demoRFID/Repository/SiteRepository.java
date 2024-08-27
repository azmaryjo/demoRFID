package com.example.demoRFID.Repository;

import com.example.demoRFID.Model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    /**
     * Method to check if a site with the specified name exists in the database.
     * This method queries the database to determine whether there is an entry in the
     * Site entity with the given site name.
     *
     * @param siteName The name of the site to check for existence.
     * @return true if a site with the specified name exists, false otherwise.
     */
    boolean existsBySiteName(String siteName);

    /**
     * Method to find a site by its name.
     * This method retrieves the Site entity that matches the specified site name from the database.
     *
     * @param siteName The name of the site to find.
     * @return An Optional containing the Site entity if a match is found, or an empty Optional if no site with the specified name exists.
     */
    Optional<Site> findBySiteName(String siteName);

}
