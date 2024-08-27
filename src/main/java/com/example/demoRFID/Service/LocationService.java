package com.example.demoRFID.Service;

import com.example.demoRFID.Repository.LocationRepository;
import com.example.demoRFID.Model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * Method to check if a location name exists in the database.
     * This method utilizes the LocationRepository to determine whether a location
     * with the specified name is present in the database.
     *
     * @param locationName The name of the location to check for existence.
     * @return true if a location with the specified name exists, false otherwise.
     */
    public boolean locationNameExists(String locationName) {
        return locationRepository.existsByLocationName(locationName);
    }

    /**
     * Method to check if a location name matches the given site name.
     * This method utilizes the LocationRepository to verify whether there is a match
     * between the specified location name and site name in the database.
     *
     * @param locationName The name of the location to check.
     * @param siteName The name of the site to check.
     * @return An Optional containing the Location entity if a match is found, or an empty Optional if no match exists.
     */
    public Optional<Location> locationNameMatchesSite(String locationName, String siteId) {
        return locationRepository.existsByLocationNameAndSiteName(locationName, siteId);
    }
//TODO:implement following methods if there is time
//    public Location createLocation(Location location) {
//        return locationRepository.save(location);
//    }
//    public Optional<Location> getLocation(Long locationId) {
//        return locationRepository.findById(locationId);
//    }
//    public List<Location> getAllLocations() {
//        return locationRepository.findAll();
//    }
//    public void deleteLocation(Long locationId) {
//        locationRepository.deleteById(locationId);
//    }
//    public Long getLocationIdByName(String locationName) {
//        return locationRepository.findLocationIdByLocationName(locationName);
//    }

}
