package com.example.demoRFID.Service;

import com.example.demoRFID.Utils.ConversionUtils;
import com.example.demoRFID.ErrorCode;
import com.example.demoRFID.ErrorMessage;
import com.example.demoRFID.Model.*;
import com.example.demoRFID.Model.Exceptions.InvalidInputException;
import com.example.demoRFID.Model.Exceptions.ResourceNotFoundException;
import com.example.demoRFID.Model.Request.RfidTxRequest;
import com.example.demoRFID.Model.Request.RfidTxUpdateRequest;
import com.example.demoRFID.Repository.RfidTxRepository;
import com.example.demoRFID.Utils.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.demoRFID.Constants.REFCODE_LENGTH;

@Service
public class RfidTxService {
    @Autowired
    private RfidTxRepository rfidTxRepository;
    private final LocationService locationService;
    private final RfidService rfidService;
    private final SiteService siteService;
    private static final Logger logger = LoggerFactory.getLogger(RfidTxService.class);

    @Autowired
    public RfidTxService(RfidTxRepository rfidTxRepository,LocationService locationService, RfidService rfidService, SiteService siteService) {
        this.locationService = locationService;
        this.rfidService = rfidService;
        this.siteService = siteService;
        this.rfidTxRepository=rfidTxRepository;
    }

    /**
     * Retrieves the latest RFID scans based on the specified criteria.
     * Validates the input parameters, ensuring correct date formats, EPC format, and site name format.
     * If the inputs are valid, retrieves the latest scans and processes the location names for display.
     *
     * @param startDateTime The start date and time for the scan search.
     * @param endDateTime The end date and time for the scan search.
     * @param epc The EPC (Electronic Product Code) to filter by, if any.
     * @param siteName The site name to filter by, if any.
     * @return A list of LatestEPC objects representing the latest scans for each EPC.
     * @throws InvalidInputException If the input data is invalid.
     * @throws ResourceNotFoundException If no scans are found matching the criteria.
     */
    public List<LatestEPC> getLatestScans(String startDateTime, String endDateTime, String epc, String siteName) {
        StringBuilder message = new StringBuilder();
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        //make sure all fields are correctly formatted to be converted and convert needed fields
        if (ValidationUtils.isValidDateFormat(startDateTime)) {
            startDate = ConversionUtils.convertStringToDate(startDateTime);
        } else {
            message.append(ErrorMessage.DATE_FORMAT.getMessage()+startDateTime);
        }
        if (ValidationUtils.isValidDateFormat(endDateTime)) {
            endDate = ConversionUtils.convertStringToDate(endDateTime);
        } else {
            message.append(ErrorMessage.DATE_FORMAT.getMessage()+endDateTime);
        }
        if (startDate != null && endDate != null && !ValidationUtils.areDatesInOrder(startDate, endDate)) {
            message.append(ErrorMessage.DATE_ORDER_ERROR.getMessage());
        }

        if (!ValidationUtils.isNullOrEmpty(epc) && ValidationUtils.isValidEPCFormat(epc)) {
            epc = epc.toUpperCase();
        } else if (!ValidationUtils.isNullOrEmpty(epc)) {
            message.append(ErrorMessage.EPC_FORMAT.getMessage());
        }
        //make sure sitename is not empty
        if (!ValidationUtils.isNullOrEmpty(siteName)) {
            siteName = ConversionUtils.convertStringCompositeNames(siteName, "..");
        }
        if (!message.isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), message);
            throw new InvalidInputException(message.toString());
        }
        List<LatestEPC> latestEpcList = rfidTxRepository.findLatestScans(startDate, endDate, epc, siteName);
        if (latestEpcList.isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_RES_NOT_FOUND.getMessage(), ErrorMessage.NO_TRANSACTIONS.getMessage());
            throw new ResourceNotFoundException(ErrorMessage.NO_TRANSACTIONS.getMessage());
        }
        latestEpcList.forEach(item -> {
            String convertedLocation = ConversionUtils.convertStringCompositeNamesToShow(item.getMostRecentLocation());
            item.setMostRecentLocation(convertedLocation);
        });
        logger.info("{}|{}",LocalDateTime.now(),"Successfully retrieved latest scans");
        return latestEpcList;
    }

    /**
     * Retrieves the top N EPCs based on the number of reads within the specified date range.
     * Validates the input parameters, including date formats and the value of N.
     * If the inputs are valid, retrieves and returns the top EPCs by read count.
     *
     * @param N The number of top EPCs to retrieve.
     * @param startDateTime The start date and time for the scan search.
     * @param endDateTime The end date and time for the scan search.
     * @return A list of TopEPC objects representing the top EPCs by read count.
     * @throws InvalidInputException If the input data is invalid.
     * @throws ResourceNotFoundException If no EPCs are found within the specified date range.
     */
    public List<TopEPC> getTopReads(int N, String startDateTime, String endDateTime) {
        StringBuilder message = new StringBuilder();
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        //make sure all fields are correctly formatted to be converted and convert needed fields
        if (ValidationUtils.isValidDateFormat(startDateTime)) {
            startDate = ConversionUtils.convertStringToDate(startDateTime);
        } else {
            message.append(ErrorMessage.DATE_FORMAT.getMessage());
        }
        if (ValidationUtils.isValidDateFormat(endDateTime)) {
            endDate = ConversionUtils.convertStringToDate(endDateTime);
        } else {
            message.append(ErrorMessage.DATE_FORMAT.getMessage());
        }
        if (startDate != null && endDate != null && !ValidationUtils.areDatesInOrder(startDate, endDate)) {
            message.append(ErrorMessage.DATE_ORDER_ERROR.getMessage());
        }
        if (!ValidationUtils.isPositiveInteger(N)) {
            message.append(ErrorMessage.N_FORMAT.getMessage());
        }
        if (!message.isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), message);
            throw new InvalidInputException(message.toString());
        }
        List<TopEPC> topReadsList = rfidTxRepository.findTopReads(N, startDate, endDate);
        if (topReadsList.isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_RES_NOT_FOUND.getMessage(), ErrorMessage.NO_TRANSACTIONS.getMessage());
            throw new ResourceNotFoundException(ErrorMessage.NO_TRANSACTIONS.getMessage());
        }
        logger.info("{}|{}",LocalDateTime.now(),"Successfully retrieved top reads list");
        return topReadsList;
    }

    /**
     * Adds a new RFID transaction to the system.
     * Validates the input fields, ensuring correct formats for date, EPC, TagID, and other fields.
     * Checks for the existence of the location, site, and RFID tag before saving the transaction.
     *
     * @param rfidTxRequest The request object containing the RFID transaction details.
     * @return The saved RfidTx object.
     * @throws InvalidInputException If the input data is invalid or required fields are missing.
     * @throws DataIntegrityViolationException If the transaction already exists.
     */
    public RfidTx addRfidTx(RfidTxRequest rfidTxRequest) {
        LocalDateTime scanDate = null;
        String epc = "";
        String siteName;
        String locationName;
        Long locationId;
        String refCode = "";
        String tagId = "";
        BigDecimal rssi;
        StringBuilder message = new StringBuilder();

        //make sure all fields are filled
        List<String> emptyFieldNames = ValidationUtils.getEmptyFieldNames(rfidTxRequest);
        if (!emptyFieldNames.isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.EMPTY_FIELDS.format(emptyFieldNames));
            throw new InvalidInputException(ErrorMessage.EMPTY_FIELDS.format(emptyFieldNames));
        }
        //make sure all fields are correctly formatted to be converted and convert needed fields
        if (ValidationUtils.isValidDateFormat(rfidTxRequest.getScanDate())) {
            scanDate = ConversionUtils.convertStringToDate(rfidTxRequest.getScanDate());
        } else {
            message.append(ErrorMessage.DATE_FORMAT.getMessage());
        }
        if (ValidationUtils.isValidEPCFormat(rfidTxRequest.getEpc())) {
            epc = rfidTxRequest.getEpc().toUpperCase();
        } else {
            message.append(ErrorMessage.EPC_FORMAT.getMessage());
        }
        if (ValidationUtils.isValidRefCode(rfidTxRequest.getRefCode())) {
            refCode = String.valueOf(ConversionUtils.convertStringToInt(rfidTxRequest.getRefCode(), -1));

        } else {
            message.append(ErrorMessage.INVALID_REFCODE.format(REFCODE_LENGTH));
        }
        if (ValidationUtils.isValidTagFormat(rfidTxRequest.getTagId())) {
            tagId = rfidTxRequest.getTagId().toUpperCase();
        } else {
            message.append(ErrorMessage.TAG_ID_FORMAT.getMessage());
        }
        siteName = ConversionUtils.convertStringCompositeNames(rfidTxRequest.getSiteName(), "..");
        locationName = ConversionUtils.convertStringCompositeNames(rfidTxRequest.getLocationName(), "..");
        rssi = rfidTxRequest.getRssi();

        if (!message.isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), message);
            throw new InvalidInputException(message.toString());
        }
        //check if location exists
        if (!locationService.locationNameExists(locationName)) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.LOCATION_NAME_DOESNT_EXIST.getMessage());
            throw new InvalidInputException(ErrorMessage.LOCATION_NAME_DOESNT_EXIST.getMessage());
        }
        //check if site exists
        if (!siteService.siteNameExists(siteName)) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.SITE_NAME_DOESNT_EXIST.getMessage());
            throw new InvalidInputException(ErrorMessage.SITE_NAME_DOESNT_EXIST.getMessage());
        }
        //check if location belongs to site and get its ID
        Optional<Location> existingLocation = locationService.locationNameMatchesSite(locationName, siteName);
        if (existingLocation.isPresent()) {
            locationId = existingLocation.get().getLocationId();
        } else {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.LOCATION_NOT_IN_SITE.getMessage());
            throw new InvalidInputException(ErrorMessage.LOCATION_NOT_IN_SITE.getMessage());
        }
        //check if tagid and epc combination exists
        if (!rfidService.checkTagIdMatchesEpc(tagId, epc)) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.TAG_ID_EPC_NO_MATCH.getMessage());
            throw new InvalidInputException(ErrorMessage.TAG_ID_EPC_NO_MATCH.getMessage());
        }
        //check if refcode belongs to same tagid and epc
        if (!rfidService.isRefCodeValidForTagIdAndEpc(tagId, epc, refCode)) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.REF_CODE_TAG_ID_EPC_NO_MATCH.getMessage());
            throw new InvalidInputException(ErrorMessage.REF_CODE_TAG_ID_EPC_NO_MATCH.getMessage());
        }
        RfidTxId id = new RfidTxId(tagId, epc, scanDate);
        //check if rfidtx dowsnt already exist
        Optional<RfidTx> existingRfidTx = rfidTxRepository.findById(id);
        if (existingRfidTx.isPresent()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_DATA_INT.getMessage(), ErrorMessage.RFID_TX_ADD_FAILURE.getMessage());
            throw new DataIntegrityViolationException(ErrorMessage.RFID_TX_ADD_FAILURE.getMessage());
        }


        Location location = new Location();
        location.setLocationId(locationId);

        RfidTx rfidTx = new RfidTx();
        rfidTx.setTagId(tagId);
        rfidTx.setEpc(epc);
        rfidTx.setScanDate(scanDate);
        rfidTx.setLocation(location);
        rfidTx.setRssi(rssi);
        logger.info("{}|{}",LocalDateTime.now(),"RFID transaction was added successfully");
        return rfidTxRepository.save(rfidTx);
    }

    /**
     * Retrieves RFID transactions by EPC.
     * Validates the EPC format and checks for transactions associated with the given EPC.
     * If found, returns the list of transactions.
     *
     * @param epc The EPC to search for.
     * @return A list of RfidTx objects associated with the given EPC.
     * @throws InvalidInputException If the EPC format is invalid.
     * @throws ResourceNotFoundException If no transactions are found for the given EPC.
     */
    public List<RfidTx> getRfidTxByEpc(String epc) {
        if (ValidationUtils.isValidEPCFormat(epc)) {
            epc = epc.toUpperCase();
            Optional<List<RfidTx>> optionalRfidTxList = rfidTxRepository.findByEpc(epc);
            if (optionalRfidTxList.isPresent() && !optionalRfidTxList.get().isEmpty()) {
                logger.info("{}|{}",LocalDateTime.now(),"Successfully retrieved RfidTx By Epc");
                return optionalRfidTxList.get();
            } else {
                logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_RES_NOT_FOUND.getMessage(), ErrorMessage.RFIDTX_EPC_NOT_FOUND.format(epc));
                throw new ResourceNotFoundException(ErrorMessage.RFIDTX_EPC_NOT_FOUND.format(epc));
            }
        } else {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.EPC_FORMAT.getMessage());
            throw new InvalidInputException(ErrorMessage.EPC_FORMAT.getMessage());
        }
    }

    /**
     * Retrieves RFID transactions by TagID.
     * Validates the TagID format and checks for transactions associated with the given TagID.
     * If found, returns the list of transactions.
     *
     * @param tagId The TagID to search for.
     * @return A list of RfidTx objects associated with the given TagID.
     * @throws InvalidInputException If the TagID format is invalid.
     * @throws ResourceNotFoundException If no transactions are found for the given TagID.
     */
    public List<RfidTx> getRfidTxByTagId(String tagId) {
        if (ValidationUtils.isValidTagFormat(tagId)) {
            tagId = tagId.toUpperCase();
            Optional<List<RfidTx>> optionalRfidTxList = rfidTxRepository.findByTagId(tagId);
            if (optionalRfidTxList.isPresent() && !optionalRfidTxList.get().isEmpty()) {
                logger.info("{}|{}",LocalDateTime.now(),"Successfully retrieved RfidTx By TagId");
                return optionalRfidTxList.get();
            } else {
                logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_RES_NOT_FOUND.getMessage(), ErrorMessage.RFIDTX_TAG_ID_NOT_FOUND.format(tagId));
                throw new ResourceNotFoundException(ErrorMessage.RFIDTX_TAG_ID_NOT_FOUND.format(tagId));
            }

        } else {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.TAG_ID_FORMAT.getMessage());
            throw new InvalidInputException(ErrorMessage.TAG_ID_FORMAT.getMessage());
        }
    }

    /**
     * Retrieves RFID transactions by both EPC and TagID.
     * Validates the formats of the EPC and TagID, and checks for transactions matching both fields.
     * If found, returns the list of transactions.
     *
     * @param epc The EPC to search for.
     * @param tagId The TagID to search for.
     * @return A list of RfidTx objects matching the EPC and TagID.
     * @throws InvalidInputException If the EPC or TagID format is invalid.
     * @throws ResourceNotFoundException If no transactions are found for the given EPC and TagID.
     */
    public List<RfidTx> getRfidTxByEpcAndTagId(String epc, String tagId) {
        if (ValidationUtils.isValidTagFormat(tagId) && ValidationUtils.isValidEPCFormat(epc)) {
            tagId = tagId.toUpperCase();
            epc = epc.toUpperCase();
            Optional<List<RfidTx>> optionalRfidTxList = rfidTxRepository.findByEpcAndTagId(epc, tagId);
            if (optionalRfidTxList.isPresent() && !optionalRfidTxList.get().isEmpty()) {
                logger.info("{}|{}",LocalDateTime.now(),"Successfully retrieved RfidTx By Epc and TagId");
                return optionalRfidTxList.get();
            } else {
                logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_RES_NOT_FOUND.getMessage(), ErrorMessage.RFIDTX_TAG_ID_EPC_NOT_FOUND.format(tagId, epc));
                throw new ResourceNotFoundException(ErrorMessage.RFIDTX_TAG_ID_EPC_NOT_FOUND.format(tagId, epc));
            }

        } else {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.TAG_ID_FORMAT.getMessage());
            throw new InvalidInputException(ErrorMessage.TAG_ID_FORMAT.getMessage() + ErrorMessage.EPC_FORMAT);
        }
    }

    /**
     * Retrieves RFID transactions within a specified date range.
     * Validates the date formats and ensures that the dates are in chronological order.
     * If valid, returns the list of transactions within the date range.
     *
     * @param startDate The start date for the search.
     * @param endDate The end date for the search.
     * @return A list of RfidTx objects within the specified date range.
     * @throws InvalidInputException If the date formats are invalid or the dates are out of order.
     * @throws ResourceNotFoundException If no transactions are found within the date range.
     */
    public List<RfidTx> getRfidTxByScanDateRange(String startDate, String endDate) {
        if (ValidationUtils.isValidDateFormat(startDate) && ValidationUtils.isValidDateFormat(endDate)) {
            LocalDateTime startDateDateTime = ConversionUtils.convertStringToDate(startDate);
            LocalDateTime endDateDateTime = ConversionUtils.convertStringToDate(endDate);
            if (!ValidationUtils.areDatesInOrder(startDateDateTime, endDateDateTime)) {
                logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.DATE_ORDER_ERROR.getMessage());
                throw new InvalidInputException(ErrorMessage.DATE_ORDER_ERROR.getMessage());
            }
            Optional<List<RfidTx>> optionalRfidTxList = rfidTxRepository.findByScanDateBetween(startDateDateTime, endDateDateTime);
            if (optionalRfidTxList.isPresent() && !optionalRfidTxList.get().isEmpty()) {
                logger.info("{}|{}",LocalDateTime.now(),"Successfully retrieved RfidTx By Date Range");
                return optionalRfidTxList.get();
            } else {
                logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_RES_NOT_FOUND.getMessage(), ErrorMessage.RFIDTX_DATE_NOT_FOUND.format(startDate, endDate));
                throw new ResourceNotFoundException(ErrorMessage.RFIDTX_DATE_NOT_FOUND.format(startDate, endDate));
            }

        } else {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.DATE_FORMAT.getMessage());
            throw new InvalidInputException(ErrorMessage.DATE_FORMAT.getMessage());
        }
    }

    /**
     * Retrieves RFID transactions based on various criteria, including EPC, TagID, and date range.
     * Validates all input parameters, ensuring correct formats and logical order of dates.
     * If valid, returns the list of transactions matching the criteria.
     *
     * @param epc The EPC to search for, if any.
     * @param tagId The TagID to search for, if any.
     * @param startDate The start date for the search, if any.
     * @param endDate The end date for the search, if any.
     * @return A list of RfidTx objects matching the criteria.
     * @throws InvalidInputException If any of the input data is invalid.
     * @throws ResourceNotFoundException If no transactions are found matching the criteria.
     */
    public List<RfidTx> getRfidTxByCriteria(String epc, String tagId, String startDate, String endDate) {
        StringBuilder message = new StringBuilder();
        LocalDateTime startDateDateTime = null;
        LocalDateTime endDateDateTime = null;

        if (!ValidationUtils.isNullOrEmpty(epc) && !ValidationUtils.isValidEPCFormat(epc)) {
            message.append(ErrorMessage.EPC_FORMAT.getMessage());
        }
        if (!ValidationUtils.isNullOrEmpty(tagId) && !ValidationUtils.isValidTagFormat(tagId)) {
            message.append(ErrorMessage.TAG_ID_FORMAT.getMessage());
        }
        if ((!ValidationUtils.isNullOrEmpty(startDate) && !ValidationUtils.isValidDateFormat(startDate))
                || (!ValidationUtils.isNullOrEmpty(endDate) && !ValidationUtils.isValidDateFormat(endDate))) {
            message.append(ErrorMessage.DATE_FORMAT);
        }

        if (!message.isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), message);
            throw new InvalidInputException(message.toString());
        }
        if (!ValidationUtils.isNullOrEmpty(startDate)) {
            startDateDateTime = ConversionUtils.convertStringToDate(startDate);
        }
        if (!ValidationUtils.isNullOrEmpty(endDate)) {
            endDateDateTime = ConversionUtils.convertStringToDate(endDate);
        }
        if (startDateDateTime != null && endDateDateTime != null && !ValidationUtils.areDatesInOrder(startDateDateTime, endDateDateTime)) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.DATE_ORDER_ERROR.getMessage());
            throw new InvalidInputException(ErrorMessage.DATE_ORDER_ERROR.getMessage());
        }

        List<RfidTx> rfidTxList = rfidTxRepository.findByCriteria(epc, tagId, startDateDateTime, endDateDateTime);
        if (rfidTxList.isEmpty()) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_RES_NOT_FOUND.getMessage(), ErrorMessage.NO_TRANSACTIONS.getMessage());
            throw new ResourceNotFoundException(ErrorMessage.NO_TRANSACTIONS.getMessage());
        } else {
            rfidTxList.forEach(item -> {
                String convertedLocation = ConversionUtils.convertStringCompositeNamesToShow(item.getLocation().getLocationName());
                item.getLocation().setLocationName(convertedLocation);
            });
            logger.info("{}|{}",LocalDateTime.now(),ErrorMessage.RFIDTX_SUCCESS.getMessage());
            return rfidTxList;
        }

    }

    /**
     * Deletes an RFID transaction based on its composite key (TagID, EPC, and scan date).
     * Validates the input fields and checks for the existence of the transaction before deletion.
     *
     * @param tagId The TagID of the RFID transaction.
     * @param epc The EPC of the RFID transaction.
     * @param scanDate The scan date of the RFID transaction.
     * @throws InvalidInputException If any of the input fields are null or empty.
     * @throws ResourceNotFoundException If the transaction does not exist.
     */
    public void deleteRfidTx(String tagId,String epc,String scanDate) {
        //if one of ID keys is null throw exception
        if(ValidationUtils.isNullOrEmpty(tagId) || ValidationUtils.isNullOrEmpty(epc) || ValidationUtils.isNullOrEmpty(scanDate)){
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.EMPTY_RFIDTX_FIELDS.getMessage());
            throw new InvalidInputException(ErrorMessage.EMPTY_RFIDTX_FIELDS.getMessage());
        }
        LocalDateTime scanD=ConversionUtils.convertStringToDate(scanDate);
        RfidTxId id=new RfidTxId(tagId,epc,scanD);
        if (!rfidTxRepository.existsById(id)) {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_RES_NOT_FOUND.getMessage(), ErrorMessage.RFID_TX_DELETE_FAILURE.getMessage());
            throw new ResourceNotFoundException(ErrorMessage.RFID_TX_DELETE_FAILURE.getMessage());
        }
        logger.info("{}|{}",LocalDateTime.now(),ErrorMessage.RFID_DEL_SUCCESS.getMessage());
        rfidTxRepository.deleteById(id);
    }

    /**
     * Updates an existing RFID transaction based on the provided ID fields and update request.
     * Validates the input fields, checks for the existence of the transaction, and updates the fields as needed.
     *
     * @param tagId The TagID of the RFID transaction.
     * @param epc The EPC of the RFID transaction.
     * @param scanDate The scan date of the RFID transaction.
     * @param updatedRfidTx The request object containing the updated RFID transaction details.
     * @return The updated RfidTx object.
     * @throws InvalidInputException If any of the input fields are null or empty, or if validation fails.
     * @throws ResourceNotFoundException If the transaction does not exist.
     */
    public RfidTx updateRfidTx(String tagId,String epc,String scanDate, RfidTxUpdateRequest updatedRfidTx) {
        String site;
        String location;
        String refCode;
        Long locationId;

        //if one of ID keys is null throw exception
        if(ValidationUtils.isNullOrEmpty(tagId) || ValidationUtils.isNullOrEmpty(epc) || ValidationUtils.isNullOrEmpty(scanDate)){
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.EMPTY_RFIDTX_FIELDS.getMessage());
            throw new InvalidInputException(ErrorMessage.EMPTY_RFIDTX_FIELDS.getMessage());
        }
        LocalDateTime scanD=ConversionUtils.convertStringToDate(scanDate);
        RfidTxId id=new RfidTxId(tagId,epc,scanD);

        Optional<RfidTx> existingRfidTx = rfidTxRepository.findById(id);
        if (existingRfidTx.isPresent()) {
            RfidTx rfidTx = existingRfidTx.get();
            if(!ValidationUtils.isNullOrZero(updatedRfidTx.getRssi()))
            {
                rfidTx.setRssi(updatedRfidTx.getRssi());
            }

            //if site is provided in request ,use it
            if (!ValidationUtils.isNullOrEmpty(updatedRfidTx.getSiteName())) {
                site = ConversionUtils.convertStringCompositeNames(updatedRfidTx.getSiteName(), "..");
            } else { //else use the one that already exists
                site = rfidTx.getLocation().getSite().getSiteName();
            }
            //if location is provided in request, use it
            if (!ValidationUtils.isNullOrEmpty(updatedRfidTx.getLocationName())) {
                location = ConversionUtils.convertStringCompositeNames(updatedRfidTx.getLocationName(), "..");
            }else{
                location=rfidTx.getLocation().getLocationName();
            }

            //check if site exists
            Optional<Site> existingSite = siteService.findBySiteName(site);
            if (existingSite.isEmpty()) {
                logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.SITE_NAME_DOESNT_EXIST.getMessage());

                throw new InvalidInputException(ErrorMessage.SITE_NAME_DOESNT_EXIST.getMessage());
            }
            //check if location belongs to site and update rfidtx if true
            Optional<Location> existingLocation = locationService.locationNameMatchesSite(location, site);
            if (existingLocation.isEmpty()) {
                logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_INV_IN.getMessage(), ErrorMessage.LOCATION_NOT_IN_SITE.getMessage());
                throw new InvalidInputException(ErrorMessage.LOCATION_NOT_IN_SITE.getMessage());
            } else {
                rfidTx.setLocation(existingLocation.get());
            }
            logger.info("{}|{}",LocalDateTime.now(),"Successfully updated RfidTx");
            return rfidTxRepository.save(rfidTx);
        } else {
            logger.error("{}|{}|{}", LocalDateTime.now(), ErrorCode.RFIDTX_RES_NOT_FOUND.getMessage(), ErrorMessage.RFIDTX_NOT_FOUND.getMessage());
            throw new ResourceNotFoundException(ErrorMessage.RFIDTX_NOT_FOUND.getMessage());
        }
    }

}
