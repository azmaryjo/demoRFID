package com.example.demoRFID.Service;

import com.example.demoRFID.Utils.ConversionUtils;
import com.example.demoRFID.ErrorCode;
import com.example.demoRFID.ErrorMessage;
import com.example.demoRFID.Model.Exceptions.InvalidInputException;
import com.example.demoRFID.Model.Exceptions.ResourceNotFoundException;
import com.example.demoRFID.Model.Site;
import com.example.demoRFID.Repository.SiteRepository;
import com.example.demoRFID.Utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class SiteService {

    private final SiteRepository siteRepository;

    public SiteService(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }
    private static final Logger logger = LoggerFactory.getLogger(SiteService.class);

    /**
     * Creates a new Site in the system.
     * Validates the site ID and site name before saving the site to the database.
     * If the site already exists, a DuplicateKeyException is thrown.
     *
     * @param site The Site object to be created.
     * @return The saved Site object.
     * @throws InvalidInputException if the site ID or site name is invalid.
     * @throws DuplicateKeyException if a site with the same ID already exists.
     */
    public Site createSite(Site site) {
        StringBuilder message=new StringBuilder();
        if(ValidationUtils.isNullOrEmpty(site.getSiteId().toString())){
            message.append(ErrorMessage.SITE_ID_FORMAT.getMessage());
        }
        if(ValidationUtils.isNullOrEmpty(site.getSiteName())){
            message.append(ErrorMessage.SITE_NAME_FORMAT.getMessage());
        }
        if(!message.isEmpty()){
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.SITE_INV_IN.getMessage(), message);
            throw new InvalidInputException(message.toString());
        }
        //return error if site already exists
        if(siteRepository.findById(site.getSiteId()).isPresent()){
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.SITE_DATA_INT.getMessage(), message);
            throw new DuplicateKeyException(ErrorMessage.SITE_ALREADY_EXISTS.format(site.getSiteId()));
        }
        String convertedSiteName= ConversionUtils.convertStringCompositeNames(site.getSiteName(),"..");
        site.setSiteName(convertedSiteName);
        logger.info("{}|{}",LocalDateTime.now(),ErrorMessage.SITE_SUCCESS.format(site.getSiteId()));
        return siteRepository.save(site);
    }

    /**
     * Retrieves a Site by its ID.
     * Validates the site ID before fetching the site from the database.
     * If the site does not exist, a ResourceNotFoundException is thrown.
     *
     * @param siteId The ID of the site to retrieve.
     * @return The Site object with the specified ID.
     * @throws InvalidInputException if the site ID is invalid.
     * @throws ResourceNotFoundException if no site with the specified ID is found.
     */
    public Site getSite(Long siteId) {
        StringBuilder message=new StringBuilder();
        if(ValidationUtils.isNullOrEmpty(siteId.toString())){
            message.append(ErrorMessage.SITE_ID_FORMAT.getMessage());
        }
        if(!message.isEmpty()){
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.SITE_INV_IN.getMessage(), message);
            throw new InvalidInputException(message.toString());
        }
        Optional<Site> site = siteRepository.findById(siteId);
        if (site.isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.SITE_RES_NOT_FOUND.getMessage(), ErrorMessage.SITE_NOT_FOUND.format(siteId));
            throw new ResourceNotFoundException(ErrorMessage.SITE_NOT_FOUND.format(siteId));
        }
        logger.info("{}|{}",LocalDateTime.now(),ErrorMessage.SITE_FETCH_SUCCESS.format(siteId));
        return site.get();
    }

    /**
     * Retrieves all Sites from the database.
     * If no sites are found, a ResourceNotFoundException is thrown.
     *
     * @return A list of all Site objects in the database.
     * @throws ResourceNotFoundException if no sites are found.
     */
    public List<Site> getAllSites() {

        List<Site> sitesFromDb = siteRepository.findAll();
        if (sitesFromDb.isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.SITE_RES_NOT_FOUND.getMessage(), ErrorMessage.NO_SITES.getMessage());
            throw new ResourceNotFoundException(ErrorMessage.NO_SITES.getMessage());
        }
        logger.info("{}|{}",LocalDateTime.now(),ErrorMessage.SITE_FETCH_ALL_SUCCESS.format(sitesFromDb.size()));
        return sitesFromDb;
    }

    /**
     * Updates an existing Site in the system.
     * Validates the site ID and site name before updating the site in the database.
     * If the site does not exist, a ResourceNotFoundException is thrown.
     *
     * @param site The Site object to be updated.
     * @return The updated Site object.
     * @throws InvalidInputException if the site ID or site name is invalid.
     * @throws ResourceNotFoundException if no site with the specified ID is found.
     */
    public Site updateSite(Site site) {

        StringBuilder message=new StringBuilder();
        if(ValidationUtils.isNullOrEmpty(site.getSiteId().toString())){
            message.append(ErrorMessage.SITE_ID_FORMAT).append("\n");
        }
        if(ValidationUtils.isNullOrEmpty(site.getSiteName())){
            message.append(ErrorMessage.SITE_NAME_FORMAT);
        }
        if(!message.isEmpty()){
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.SITE_INV_IN.getMessage(), message);
            throw new InvalidInputException(message.toString());
        }
        //return error if site does not exist
        if (siteRepository.findById(site.getSiteId()).isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.SITE_RES_NOT_FOUND.getMessage(), ErrorMessage.SITE_NOT_FOUND.format(site.getSiteId()));
            throw new ResourceNotFoundException(ErrorMessage.SITE_NOT_FOUND.format(site.getSiteId()));
        }
        String convertedSiteName= ConversionUtils.convertStringCompositeNames(site.getSiteName(),"..");
        site.setSiteName(convertedSiteName);
        logger.info("{}|{}",LocalDateTime.now(),ErrorMessage.SITE_UPDATE_SUCCESS.format(site.getSiteId()));
        return siteRepository.save(site);
    }

    /**
     * Deletes a Site by its ID.
     * Validates the site ID before deleting the site from the database.
     * If the site does not exist, a ResourceNotFoundException is thrown.
     * If the site is attached to a location, a DataIntegrityViolationException will be automatically thrown.
     *
     * @param siteId The ID of the site to delete.
     * @throws InvalidInputException if the site ID is invalid.
     * @throws ResourceNotFoundException if no site with the specified ID is found.
     */
    public void deleteSite(Long siteId) {
        if(ValidationUtils.isNullOrEmpty(siteId.toString())){
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.SITE_INV_IN.getMessage(), ErrorMessage.SITE_ID_FORMAT);
            throw new InvalidInputException(ErrorMessage.SITE_ID_FORMAT.getMessage());
        }
        //return error if site does not exist
        if (siteRepository.findById(siteId).isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.SITE_RES_NOT_FOUND.getMessage(), ErrorMessage.SITE_NOT_FOUND.format(siteId));
            throw new ResourceNotFoundException(ErrorMessage.SITE_NOT_FOUND.format(siteId));
        }
        //if site is already attached to a location a DataIntegrityViolationException will be automatically thrown
        logger.info("{}|{}",LocalDateTime.now(),ErrorMessage.SITE_DEL_SUCCESS.format(siteId));
        siteRepository.deleteById(siteId);
    }

    /**
     * Checks if a site with the specified name exists in the database.
     * This method is used internally after the site name has been converted to the appropriate format.
     *
     * @param siteName The name of the site to check for existence.
     * @return true if a site with the specified name exists, false otherwise.
     */
    public boolean siteNameExists(String siteName) {
        return siteRepository.existsBySiteName(siteName);
    }

    /**
     * Finds a Site by its name.
     * This method is used internally after the site name has been converted to the appropriate format.
     *
     * @param siteName The name of the site to find.
     * @return An Optional containing the Site object if found, or an empty Optional if no site with the specified name exists.
     */
    public Optional<Site> findBySiteName(String siteName){ return siteRepository.findBySiteName(siteName); }

}
