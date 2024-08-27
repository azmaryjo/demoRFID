package com.example.demoRFID.Service;


import com.example.demoRFID.Utils.ConversionUtils;
import com.example.demoRFID.ErrorMessage;
import com.example.demoRFID.Model.*;
import com.example.demoRFID.Model.Exceptions.InvalidInputException;
import com.example.demoRFID.Model.Exceptions.ResourceNotFoundException;
import com.example.demoRFID.Model.Request.RfidTxRequest;
import com.example.demoRFID.Model.Request.RfidTxUpdateRequest;
import com.example.demoRFID.Repository.RfidTxRepository;
import com.example.demoRFID.Utils.ValidationUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RfidTxServiceTest {

    @Mock
    private RfidTxRepository rfidTxRepository;

    @Mock
    private LocationService locationService;

    @Mock
    private RfidService rfidService;

    @Mock
    private SiteService siteService;

    @InjectMocks
    private RfidTxService rfidTxService;

    private RfidTxRequest validRfidTxRequest;
    private RfidTxUpdateRequest validRfidTxUpdateRequest;
    private RfidTx existingRfidTx;
    private Location location;
    private Site site;

    @BeforeEach
    public void setUp() {
        validRfidTxRequest = new RfidTxRequest();
        validRfidTxRequest.setScanDate("2023-08-01 10:00:00");
        validRfidTxRequest.setEpc("EPC123");
        validRfidTxRequest.setRefCode("12345");
        validRfidTxRequest.setTagId("TAG123");
        validRfidTxRequest.setSiteName("SiteName");
        validRfidTxRequest.setLocationName("LocationName");
        validRfidTxRequest.setRssi(new BigDecimal("75.5"));

        validRfidTxUpdateRequest = new RfidTxUpdateRequest();
        validRfidTxUpdateRequest.setRssi(new BigDecimal("80.0"));
        validRfidTxUpdateRequest.setSiteName("UpdatedSiteName");
        validRfidTxUpdateRequest.setLocationName("UpdatedLocationName");

        location = new Location();
        location.setLocationId(1L);
        location.setLocationName("LOCATIONNAME");

        site = new Site();
        site.setSiteName("SITENAME");

        location.setSite(site);

        existingRfidTx = new RfidTx();
        existingRfidTx.setTagId("TAG123");
        existingRfidTx.setEpc("EPC123");
        existingRfidTx.setScanDate(LocalDateTime.of(2023, 8, 1, 10, 0));
        existingRfidTx.setLocation(location);
        existingRfidTx.setRssi(new BigDecimal("75.5"));
    }

//use mockstatic (eature provided by the Mockito library) to mock static methods
// Use argument matcher to avoid capitalization issues or exact string matching problems

    @Test
    public void testGetLatestScans_ValidInput() {

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-01 10:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-01 10:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0,0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-02 12:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-02 12:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 2, 12, 0,0));
            validationUtilsMockedStatic.when(() ->ValidationUtils.areDatesInOrder(LocalDateTime.of(2023, 8, 1, 10, 0,0), LocalDateTime.of(2023, 8, 2, 12, 0,0))).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat("EPC123")).thenReturn(true);

            when(rfidTxRepository.findLatestScans(any(), any(), any(), any())).thenReturn(List.of(new LatestEPC()));

            List<LatestEPC> result = rfidTxService.getLatestScans("2023-08-01 10:00:00", "2023-08-02 12:00:00", "EPC123", "SiteName");

            assertNotNull(result);
            assertFalse(result.isEmpty());
            verify(rfidTxRepository, times(1)).findLatestScans(any(), any(), any(), any());
        }
    }

    @Test
    public void testGetLatestScans_InvalidDate_ThrowsInvalidInputException() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("invalid-date")).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getLatestScans("invalid-date", "2023-08-01T12:00:00", "EPC123", "SiteName");
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.DATE_FORMAT.getMessage()));
            verify(rfidTxRepository, never()).findLatestScans(any(), any(), any(), any());
        }
    }
    @Test
    public void testGetLatestScans_InvalidEPCFormat() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-01 10:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-01 10:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0,0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-02 12:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-02 12:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 2, 12, 0,0));
            validationUtilsMockedStatic.when(() ->ValidationUtils.areDatesInOrder(LocalDateTime.of(2023, 8, 1, 10, 0,0), LocalDateTime.of(2023, 8, 2, 12, 0,0))).thenReturn(true);

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat("invalid-epc")).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getLatestScans("2023-08-01 10:00:00", "2023-08-02 12:00:00", "invalid-epc", "SiteName");});


            assertTrue(exception.getMessage().contains(ErrorMessage.EPC_FORMAT.getMessage()));
            verify(rfidTxRepository, never()).findLatestScans(any(), any(), any(), any());

        }
    }
    @Test
    public void testGetLatestScans_DatesOutOfOrder() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-01 10:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-01 10:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0,0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-07-31 10:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-07-31 10:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 7, 31, 10, 0,0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.areDatesInOrder(LocalDateTime.of(2023, 8, 1, 10, 0,0), LocalDateTime.of(2023, 7, 31, 10, 0,0))).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getLatestScans("2023-08-01 10:00:00", "2023-07-31 10:00:00", "invalid-epc", "SiteName");});


            assertTrue(exception.getMessage().contains(ErrorMessage.DATE_ORDER_ERROR.getMessage()));
            verify(rfidTxRepository, never()).findLatestScans(any(), any(), any(), any());

        }
    }

    @Test
    public void testGetLatestScans_EmptyResult() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-01 10:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-01 10:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0,0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-02 12:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-02 12:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 2, 12, 0,0));
            validationUtilsMockedStatic.when(() ->ValidationUtils.areDatesInOrder(LocalDateTime.of(2023, 8, 1, 10, 0,0), LocalDateTime.of(2023, 8, 2, 12, 0,0))).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat("EPC123")).thenReturn(true);

            when(rfidTxRepository.findLatestScans(any(), any(), any(), any())).thenReturn(Collections.emptyList());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                rfidTxService.getLatestScans("2023-08-01 10:00:00", "2023-08-02 12:00:00", "EPC123", "SiteName");});


            assertTrue(exception.getMessage().contains(ErrorMessage.NO_TRANSACTIONS.getMessage()));
            verify(rfidTxRepository, times(1)).findLatestScans(any(), any(), any(), any());
        }
    }
    @Test
    public void testGetLatestScans_ValidStartDateEndDateOnly() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-01 10:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-01 10:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0,0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-02 12:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-02 12:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 2, 12, 0,0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.areDatesInOrder(LocalDateTime.of(2023, 8, 1, 10, 0,0), LocalDateTime.of(2023, 8, 2, 12, 0,0))).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(eq(null))).thenReturn(true);
            when(rfidTxRepository.findLatestScans(any(), any(), eq(null), eq(null))).thenReturn(List.of(new LatestEPC()));

            List<LatestEPC> result = rfidTxService.getLatestScans("2023-08-01 10:00:00", "2023-08-02 12:00:00", null, null);

            assertNotNull(result);
            assertFalse(result.isEmpty());
            verify(rfidTxRepository, times(1)).findLatestScans(any(), any(), eq(null), eq(null));
        }
    }

    @Test
    public void testGetLatestScans_ValidStartDateEndDateSiteNameOnly() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-01 10:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-01 10:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0,0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-02 12:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-02 12:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 2, 12, 0,0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.areDatesInOrder(LocalDateTime.of(2023, 8, 1, 10, 0,0), LocalDateTime.of(2023, 8, 2, 12, 0,0))).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(eq(null))).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNames("SiteName",".."))
                    .thenReturn("SITENAME");
            when(rfidTxRepository.findLatestScans(any(), any(), eq(null), eq("SITENAME"))).thenReturn(List.of(new LatestEPC()));

            List<LatestEPC> result = rfidTxService.getLatestScans("2023-08-01 10:00:00", "2023-08-02 12:00:00", null, "SiteName");

            assertNotNull(result);
            assertFalse(result.isEmpty());
            verify(rfidTxRepository, times(1)).findLatestScans(any(), any(), eq(null), eq("SITENAME"));
        }
    }

    @Test
    public void testGetTopReads_ValidInput() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-01 10:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-01 10:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0, 0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-02 12:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-02 12:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 2, 12, 0, 0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.areDatesInOrder(LocalDateTime.of(2023, 8, 1, 10, 0, 0), LocalDateTime.of(2023, 8, 2, 12, 0, 0))).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isPositiveInteger(5)).thenReturn(true);

            List<TopEPC> expectedList = List.of(new TopEPC());
            when(rfidTxRepository.findTopReads(5, LocalDateTime.of(2023, 8, 1, 10, 0, 0), LocalDateTime.of(2023, 8, 2, 12, 0, 0)))
                    .thenReturn(expectedList);

            List<TopEPC> result = rfidTxService.getTopReads(5, "2023-08-01 10:00:00", "2023-08-02 12:00:00");

            assertNotNull(result);
            assertEquals(1, result.size());
            verify(rfidTxRepository, times(1)).findTopReads(5, LocalDateTime.of(2023, 8, 1, 10, 0, 0), LocalDateTime.of(2023, 8, 2, 12, 0, 0));
        }
    }
    @Test
    public void testGetTopReads_InvalidStartDateFormat() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("invalid-date")).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                    rfidTxService.getTopReads(5, "invalid-date", "2023-08-02 12:00:00")
            );

            assertTrue(exception.getMessage().contains(ErrorMessage.DATE_FORMAT.getMessage()));
            verify(rfidTxRepository, times(0)).findTopReads(anyInt(), any(), any());
        }
    }

    @Test
    public void testGetTopReads_StartDateAfterEndDate() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-02 12:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-02 12:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 2, 12, 0, 0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-01 10:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-01 10:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0, 0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.areDatesInOrder(any(), any())).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                    rfidTxService.getTopReads(5, "2023-08-02 12:00:00", "2023-08-01 10:00:00")
            );

            assertTrue(exception.getMessage().contains(ErrorMessage.DATE_ORDER_ERROR.getMessage()));
            verify(rfidTxRepository, times(0)).findTopReads(anyInt(), any(), any());
        }
    }
    @Test
    public void testGetTopReads_InvalidN() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-01 10:00:00")).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-02 12:00:00")).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isPositiveInteger(-1)).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () ->
                    rfidTxService.getTopReads(-1, "2023-08-01 10:00:00", "2023-08-02 12:00:00")
            );

            assertTrue(exception.getMessage().contains(ErrorMessage.N_FORMAT.getMessage()));
            verify(rfidTxRepository, times(0)).findTopReads(anyInt(), any(), any());
        }
    }

    @Test
    public void testGetTopReads_ValidInputButNoResults() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-01 10:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-01 10:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0, 0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-02 12:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-02 12:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 2, 12, 0, 0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.areDatesInOrder(any(), any())).thenReturn(true);

            validationUtilsMockedStatic.when(() -> ValidationUtils.isPositiveInteger(5)).thenReturn(true);

            when(rfidTxRepository.findTopReads(5, LocalDateTime.of(2023, 8, 1, 10, 0, 0), LocalDateTime.of(2023, 8, 2, 12, 0, 0)))
                    .thenReturn(Collections.emptyList());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                    rfidTxService.getTopReads(5, "2023-08-01 10:00:00", "2023-08-02 12:00:00")
            );

            assertTrue(exception.getMessage().contains(ErrorMessage.NO_TRANSACTIONS.getMessage()));
            verify(rfidTxRepository, times(1)).findTopReads(5, LocalDateTime.of(2023, 8, 1, 10, 0, 0), LocalDateTime.of(2023, 8, 2, 12, 0, 0));
        }
    }


    @Test
    public void testAddRfidTx_ValidInput() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat("2023-08-01 10:00:00")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-01 10:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat("EPC123")).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidRefCode("12345")).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat("TAG123")).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNames("LocationName",".."))
                    .thenReturn("LOCATIONNAME");
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNames("SiteName",".."))
                    .thenReturn("SITENAME");
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToInt("12345", -1))
                    .thenReturn(12345);

            when(locationService.locationNameExists("LOCATIONNAME")).thenReturn(true);
            when(siteService.siteNameExists("SITENAME")).thenReturn(true);
            when(locationService.locationNameMatchesSite("LOCATIONNAME", "SITENAME")).thenReturn(Optional.of(location));
            when(rfidService.checkTagIdMatchesEpc("TAG123", "EPC123")).thenReturn(true);
            when(rfidService.isRefCodeValidForTagIdAndEpc("TAG123", "EPC123", "12345")).thenReturn(true);
            when(rfidTxRepository.findById(any())).thenReturn(Optional.empty());
            when(rfidTxRepository.save(any(RfidTx.class))).thenReturn(existingRfidTx);

            RfidTx result = rfidTxService.addRfidTx(validRfidTxRequest);

            assertNotNull(result);
            verify(rfidTxRepository, times(1)).save(any(RfidTx.class));
        }
    }

    @Test
    public void testAddRfidTx_InvalidInput_ThrowsInvalidInputException() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(anyString())).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.addRfidTx(validRfidTxRequest);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.DATE_FORMAT.getMessage()));
            verify(rfidTxRepository, never()).save(any(RfidTx.class));
        }
    }
    @Test
    public void testAddRfidTx_MissingRequiredFields() {
        RfidTxRequest request = new RfidTxRequest();
        request.setScanDate("");
        request.setEpc("");
        request.setSiteName("SiteName");
        request.setLocationName("LocationName");
        request.setRefCode("");
        request.setTagId("");
        request.setRssi(null);

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            List<String> emptyFields = Arrays.asList("scanDate", "epc", "refCode", "tagId", "rssi");
            validationUtilsMockedStatic.when(() -> ValidationUtils.getEmptyFieldNames(request)).thenReturn(emptyFields);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.addRfidTx(request);
            });

            assertTrue(exception.getMessage().contains("The following fields are empty"));
            verify(rfidTxRepository, times(0)).save(any(RfidTx.class));
        }
    }

    @Test
    public void testAddRfidTx_InvalidDateFormat() {
        RfidTxRequest request = new RfidTxRequest();
        request.setScanDate("invalid-date");
        request.setEpc("EPC123");
        request.setSiteName("SiteName");
        request.setLocationName("LocationName");
        request.setRefCode("12345");
        request.setTagId("TAG123");
        request.setRssi(new BigDecimal("12.34"));

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.getEmptyFieldNames(request)).thenReturn(Collections.emptyList());
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(request.getScanDate())).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.addRfidTx(request);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.DATE_FORMAT.getMessage()));
            verify(rfidTxRepository, times(0)).save(any(RfidTx.class));
        }
    }
    @Test
    public void testAddRfidTx_InvalidEpcFormat() {
        RfidTxRequest request = new RfidTxRequest();
        request.setScanDate("2023-08-01 10:00:00");
        request.setEpc("invalid-epc");
        request.setSiteName("SiteName");
        request.setLocationName("LocationName");
        request.setRefCode("12345");
        request.setTagId("TAG123");
        request.setRssi(new BigDecimal("12.34"));

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.getEmptyFieldNames(request)).thenReturn(Collections.emptyList());
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(request.getScanDate())).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(request.getEpc())).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.addRfidTx(request);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.EPC_FORMAT.getMessage()));
            verify(rfidTxRepository, times(0)).save(any(RfidTx.class));
        }
    }

    @Test
    public void testAddRfidTx_InvalidLocationName() {
        RfidTxRequest request = new RfidTxRequest();
        request.setScanDate("2023-08-01 10:00:00");
        request.setEpc("EPC123");
        request.setSiteName("SiteName");
        request.setLocationName("InvalidLocationName");
        request.setRefCode("12345");
        request.setTagId("TAG123");
        request.setRssi(new BigDecimal("12.34"));

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.getEmptyFieldNames(request)).thenReturn(Collections.emptyList());
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(request.getScanDate())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(request.getScanDate()))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0, 0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(request.getEpc())).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidRefCode(request.getRefCode())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToInt(request.getRefCode(), -1)).thenReturn(1234);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(request.getTagId())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNames(request.getSiteName(), ".."))
                    .thenReturn("SiteName");
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNames(request.getLocationName(), ".."))
                    .thenReturn("InvalidLocationName");

            when(locationService.locationNameExists("InvalidLocationName")).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.addRfidTx(request);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.LOCATION_NAME_DOESNT_EXIST.getMessage()));
            verify(rfidTxRepository, times(0)).save(any(RfidTx.class));
        }
    }

    @Test
    public void testAddRfidTx_RfidTxAlreadyExists() {
        RfidTxRequest request = new RfidTxRequest();
        request.setScanDate("2023-08-01 10:00:00");
        request.setEpc("EPC123");
        request.setSiteName("SITENAME");
        request.setLocationName("LOCATIONNAME");
        request.setRefCode("12345");
        request.setTagId("TAG123");
        request.setRssi(new BigDecimal("12.34"));

        RfidTxId id = new RfidTxId("TAG123", "EPC123", LocalDateTime.of(2023, 8, 1, 10, 0, 0));

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.getEmptyFieldNames(request)).thenReturn(Collections.emptyList());
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(request.getScanDate())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(request.getScanDate()))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0, 0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(request.getEpc())).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidRefCode(request.getRefCode())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToInt(request.getRefCode(), -1)).thenReturn(12345);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(request.getTagId())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNames(request.getLocationName(),".."))
                    .thenReturn("LOCATIONNAME");
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNames(request.getSiteName(),".."))
                    .thenReturn("SITENAME");
            when(locationService.locationNameExists(request.getLocationName())).thenReturn(true);
            when(siteService.siteNameExists(request.getSiteName())).thenReturn(true);
            when(locationService.locationNameMatchesSite(request.getLocationName(), request.getSiteName())).thenReturn(Optional.of(location));
            when(rfidService.checkTagIdMatchesEpc(request.getTagId(), request.getEpc())).thenReturn(true);
            when(rfidService.isRefCodeValidForTagIdAndEpc(request.getTagId(), request.getEpc(), request.getRefCode())).thenReturn(true);
            when(rfidTxRepository.findById(id)).thenReturn(Optional.of(new RfidTx()));

            DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
                rfidTxService.addRfidTx(request);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.RFID_TX_ADD_FAILURE.getMessage()));
            verify(rfidTxRepository, times(0)).save(any(RfidTx.class));
        }
    }

    @Test
    public void testAddRfidTx_LocationDoesNotBelongToSite() {
        RfidTxRequest request = new RfidTxRequest();
        request.setScanDate("2023-08-01 10:00:00");
        request.setEpc("EPC123");
        request.setSiteName("SiteName");
        request.setLocationName("LocationName");
        request.setRefCode("12345");
        request.setTagId("TAG123");
        request.setRssi(new BigDecimal("12.34"));

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.getEmptyFieldNames(request)).thenReturn(Collections.emptyList());
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(request.getScanDate())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(request.getScanDate()))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0, 0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(request.getEpc())).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidRefCode(request.getRefCode())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToInt(request.getRefCode(), -1)).thenReturn(1234);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(request.getTagId())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNames(request.getSiteName(), ".."))
                    .thenReturn("SiteName");
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNames(request.getLocationName(), ".."))
                    .thenReturn("LocationName");

            when(locationService.locationNameExists("LocationName")).thenReturn(true);
            when(siteService.siteNameExists("SiteName")).thenReturn(true);
            when(locationService.locationNameMatchesSite("LocationName", "SiteName"))
                    .thenReturn(Optional.empty());

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.addRfidTx(request);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.LOCATION_NOT_IN_SITE.getMessage()));
            verify(rfidTxRepository, times(0)).save(any(RfidTx.class));
        }
    }
    @Test
    public void testAddRfidTx_RefCodeDoesNotBelongToTagIdAndEpc() {
        RfidTxRequest request = new RfidTxRequest();
        request.setScanDate("2023-08-01 10:00:00");
        request.setEpc("EPC123");
        request.setSiteName("SiteName");
        request.setLocationName("LocationName");
        request.setRefCode("12345");
        request.setTagId("TAG123");
        request.setRssi(new BigDecimal("12.34"));

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.getEmptyFieldNames(request)).thenReturn(Collections.emptyList());
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(request.getScanDate())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(request.getScanDate()))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0, 0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(request.getEpc())).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidRefCode(request.getRefCode())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToInt(request.getRefCode(), -1)).thenReturn(12345);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(request.getTagId())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNames(request.getSiteName(), ".."))
                    .thenReturn("SiteName");
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNames(request.getLocationName(), ".."))
                    .thenReturn("LocationName");

            when(locationService.locationNameExists("LocationName")).thenReturn(true);
            when(siteService.siteNameExists("SiteName")).thenReturn(true);
            when(locationService.locationNameMatchesSite("LocationName", "SiteName"))
                    .thenReturn(Optional.of(new Location()));
            when(rfidService.checkTagIdMatchesEpc("TAG123", "EPC123")).thenReturn(true);
            when(rfidService.isRefCodeValidForTagIdAndEpc("TAG123", "EPC123", "12345")).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.addRfidTx(request);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.REF_CODE_TAG_ID_EPC_NO_MATCH.getMessage()));
            verify(rfidTxRepository, times(0)).save(any(RfidTx.class));
        }
    }
    @Test
    public void testAddRfidTx_TagIdAndEpcCombinationDoesNotExist() {
        RfidTxRequest request = new RfidTxRequest();
        request.setScanDate("2023-08-01 10:00:00");
        request.setEpc("EPC123");
        request.setSiteName("SiteName");
        request.setLocationName("LocationName");
        request.setRefCode("12345");
        request.setTagId("TAG123");
        request.setRssi(new BigDecimal("12.34"));

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.getEmptyFieldNames(request)).thenReturn(Collections.emptyList());
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(request.getScanDate())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(request.getScanDate()))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0, 0));
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(request.getEpc())).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidRefCode(request.getRefCode())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToInt(request.getRefCode(), -1)).thenReturn(12345);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(request.getTagId())).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNames(request.getSiteName(), ".."))
                    .thenReturn("SiteName");
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNames(request.getLocationName(), ".."))
                    .thenReturn("LocationName");

            when(locationService.locationNameExists("LocationName")).thenReturn(true);
            when(siteService.siteNameExists("SiteName")).thenReturn(true);
            when(locationService.locationNameMatchesSite("LocationName", "SiteName"))
                    .thenReturn(Optional.of(new Location()));
            when(rfidService.checkTagIdMatchesEpc("TAG123", "EPC123")).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.addRfidTx(request);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.TAG_ID_EPC_NO_MATCH.getMessage()));
            verify(rfidTxRepository, times(0)).save(any(RfidTx.class));
        }
    }


    @Test
    public void testGetRfidTxByEpc_ValidInput() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat("EPC123")).thenReturn(true);
            when(rfidTxRepository.findByEpc("EPC123")).thenReturn(Optional.of(List.of(existingRfidTx)));

            List<RfidTx> result = rfidTxService.getRfidTxByEpc("EPC123");

            assertNotNull(result);
            assertFalse(result.isEmpty());
            verify(rfidTxRepository, times(1)).findByEpc("EPC123");
        }
    }

    @Test
    public void testGetRfidTxByEpc_InvalidEpc_ThrowsInvalidInputException() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat("invalid-epc")).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getRfidTxByEpc("invalid-epc");
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.EPC_FORMAT.getMessage()));
            verify(rfidTxRepository, never()).findByEpc(anyString());
        }
    }

    @Test
    public void testGetRfidTxByEpc_NotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat("EPC123")).thenReturn(true);
            when(rfidTxRepository.findByEpc("EPC123")).thenReturn(Optional.of(List.of()));

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                rfidTxService.getRfidTxByEpc("EPC123");
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.RFIDTX_EPC_NOT_FOUND.format("EPC123")));
            verify(rfidTxRepository, times(1)).findByEpc("EPC123");
        }
    }

    @Test
    public void testGetRfidTxByTagId_ValidTagIdWithResults() {
        String tagId = "TAG123";
        List<RfidTx> expectedList = Arrays.asList(new RfidTx(), new RfidTx());

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(tagId)).thenReturn(true);
            when(rfidTxRepository.findByTagId(tagId.toUpperCase())).thenReturn(Optional.of(expectedList));

            List<RfidTx> result = rfidTxService.getRfidTxByTagId(tagId);

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(rfidTxRepository, times(1)).findByTagId(tagId.toUpperCase());
        }
    }
    @Test
    public void testGetRfidTxByTagId_ValidTagIdNoResults() {
        String tagId = "TAG123";

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(tagId)).thenReturn(true);
            when(rfidTxRepository.findByTagId(tagId.toUpperCase())).thenReturn(Optional.of(Collections.emptyList()));

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                rfidTxService.getRfidTxByTagId(tagId);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.RFIDTX_TAG_ID_NOT_FOUND.format(tagId.toUpperCase())));
            verify(rfidTxRepository, times(1)).findByTagId(tagId.toUpperCase());
        }
    }

    @Test
    public void testGetRfidTxByTagId_InvalidTagIdFormat() {
        String tagId = "INVALID_TAG";

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(tagId)).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getRfidTxByTagId(tagId);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.TAG_ID_FORMAT.getMessage()));
            verify(rfidTxRepository, times(0)).findByTagId(anyString());
        }
    }
    @Test
    public void testGetRfidTxByTagId_NullTagIdInput() {
        String tagId = null;

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(tagId)).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getRfidTxByTagId(tagId);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.TAG_ID_FORMAT.getMessage()));
            verify(rfidTxRepository, times(0)).findByTagId(anyString());
        }
    }

    @Test
    public void testGetRfidTxByEpcAndTagId_ValidEpcAndTagIdWithResults() {
        String epc = "EPC123";
        String tagId = "TAG123";
        List<RfidTx> expectedList = Arrays.asList(new RfidTx(), new RfidTx());

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(epc)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(tagId)).thenReturn(true);
            when(rfidTxRepository.findByEpcAndTagId(epc.toUpperCase(), tagId.toUpperCase())).thenReturn(Optional.of(expectedList));

            List<RfidTx> result = rfidTxService.getRfidTxByEpcAndTagId(epc, tagId);

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(rfidTxRepository, times(1)).findByEpcAndTagId(epc.toUpperCase(), tagId.toUpperCase());
        }
    }
    @Test
    public void testGetRfidTxByEpcAndTagId_ValidEpcAndTagIdNoResults() {
        String epc = "EPC123";
        String tagId = "TAG123";

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(epc)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(tagId)).thenReturn(true);
            when(rfidTxRepository.findByEpcAndTagId(epc.toUpperCase(), tagId.toUpperCase())).thenReturn(Optional.of(Collections.emptyList()));

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                rfidTxService.getRfidTxByEpcAndTagId(epc, tagId);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.RFIDTX_TAG_ID_EPC_NOT_FOUND.format(tagId.toUpperCase(), epc.toUpperCase())));
            verify(rfidTxRepository, times(1)).findByEpcAndTagId(epc.toUpperCase(), tagId.toUpperCase());
        }
    }
    @Test
    public void testGetRfidTxByEpcAndTagId_InvalidTagIdFormat() {
        String epc = "EPC123";
        String tagId = "INVALID_TAG";

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(epc)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(tagId)).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getRfidTxByEpcAndTagId(epc, tagId);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.TAG_ID_FORMAT.getMessage() + ErrorMessage.EPC_FORMAT));
            verify(rfidTxRepository, times(0)).findByEpcAndTagId(anyString(), anyString());
        }
    }

    @Test
    public void testGetRfidTxByEpcAndTagId_InvalidEpcAndTagId() {
        String epc = "INVALID_EPC";
        String tagId = "INVALID_TAG";

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(epc)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(tagId)).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getRfidTxByEpcAndTagId(epc, tagId);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.TAG_ID_FORMAT.getMessage() + ErrorMessage.EPC_FORMAT));
            verify(rfidTxRepository, times(0)).findByEpcAndTagId(anyString(), anyString());
        }
    }
    @Test
    public void testGetRfidTxByEpcAndTagId_NullTagIdInput() {
        String epc = "EPC123";
        String tagId = null;

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(epc)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(tagId)).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getRfidTxByEpcAndTagId(epc, tagId);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.TAG_ID_FORMAT.getMessage() + ErrorMessage.EPC_FORMAT));
            verify(rfidTxRepository, times(0)).findByEpcAndTagId(anyString(), anyString());
        }
    }


    @Test
    public void testGetRfidTxByScanDateRange_ValidDateRangeWithResults() {
        String startDate = "2023-08-01 10:00:00";
        String endDate = "2023-08-02 12:00:00";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 8, 1, 10, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 8, 2, 12, 0, 0);
        List<RfidTx> expectedList = Arrays.asList(new RfidTx(), new RfidTx());

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(startDate)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(endDate)).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(startDate)).thenReturn(startDateTime);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(endDate)).thenReturn(endDateTime);
            validationUtilsMockedStatic.when(() -> ValidationUtils.areDatesInOrder(startDateTime, endDateTime)).thenReturn(true);

            when(rfidTxRepository.findByScanDateBetween(startDateTime, endDateTime)).thenReturn(Optional.of(expectedList));

            List<RfidTx> result = rfidTxService.getRfidTxByScanDateRange(startDate, endDate);

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(rfidTxRepository, times(1)).findByScanDateBetween(startDateTime, endDateTime);
        }
    }
    @Test
    public void testGetRfidTxByScanDateRange_ValidDateRangeNoResults() {
        String startDate = "2023-08-01 10:00:00";
        String endDate = "2023-08-02 12:00:00";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 8, 1, 10, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 8, 2, 12, 0, 0);

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(startDate)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(endDate)).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(startDate)).thenReturn(startDateTime);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(endDate)).thenReturn(endDateTime);
            validationUtilsMockedStatic.when(() -> ValidationUtils.areDatesInOrder(startDateTime, endDateTime)).thenReturn(true);

            when(rfidTxRepository.findByScanDateBetween(startDateTime, endDateTime)).thenReturn(Optional.of(Collections.emptyList()));

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                rfidTxService.getRfidTxByScanDateRange(startDate, endDate);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.RFIDTX_DATE_NOT_FOUND.format(startDate, endDate)));
            verify(rfidTxRepository, times(1)).findByScanDateBetween(startDateTime, endDateTime);
        }
    }
    @Test
    public void testGetRfidTxByScanDateRange_InvalidStartDateFormat() {
        String startDate = "invalid-date";
        String endDate = "2023-08-02 12:00:00";

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(startDate)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(endDate)).thenReturn(true);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getRfidTxByScanDateRange(startDate, endDate);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.DATE_FORMAT.getMessage()));
            verify(rfidTxRepository, times(0)).findByScanDateBetween(any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }

    @Test
    public void testGetRfidTxByScanDateRange_StartDateAfterEndDate() {
        String startDate = "2023-08-02 12:00:00";
        String endDate = "2023-08-01 10:00:00";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 8, 2, 12, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 8, 1, 10, 0, 0);

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(startDate)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(endDate)).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(startDate)).thenReturn(startDateTime);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(endDate)).thenReturn(endDateTime);
            validationUtilsMockedStatic.when(() -> ValidationUtils.areDatesInOrder(startDateTime, endDateTime)).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getRfidTxByScanDateRange(startDate, endDate);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.DATE_ORDER_ERROR.getMessage()));
            verify(rfidTxRepository, times(0)).findByScanDateBetween(any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }

    @Test
    public void testGetRfidTxByScanDateRange_NullStartDate() {
        String startDate = null;
        String endDate = "2023-08-02 12:00:00";

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(startDate)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(endDate)).thenReturn(true);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getRfidTxByScanDateRange(startDate, endDate);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.DATE_FORMAT.getMessage()));
            verify(rfidTxRepository, times(0)).findByScanDateBetween(any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }

    @Test
    public void testGetRfidTxByCriteria_ValidInputsWithResults() {
        String epc = "EPC123";
        String tagId = "TAG123";
        String startDate = "2023-08-01 10:00:00";
        String endDate = "2023-08-02 12:00:00";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 8, 1, 10, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 8, 2, 12, 0, 0);


        Location location1 = new Location();
        location1.setLocationName("LOC1");
        RfidTx rfidTx1 = new RfidTx();
        rfidTx1.setLocation(location1);

        Location location2 = new Location();
        location2.setLocationName("LOC2");
        RfidTx rfidTx2 = new RfidTx();
        rfidTx2.setLocation(location2);

        List<RfidTx> expectedList = Arrays.asList(rfidTx1, rfidTx2);

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {


            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(epc)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(epc)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(tagId)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(tagId)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(startDate)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(startDate)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(endDate)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(endDate)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.areDatesInOrder(startDateTime, endDateTime)).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(startDate)).thenReturn(startDateTime);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(endDate)).thenReturn(endDateTime);


            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNamesToShow("LOC1")).thenReturn("LOC1_CONVERTED");
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNamesToShow("LOC2")).thenReturn("LOC2_CONVERTED");


            when(rfidTxRepository.findByCriteria(epc, tagId, startDateTime, endDateTime)).thenReturn(expectedList);


            List<RfidTx> result = rfidTxService.getRfidTxByCriteria(epc, tagId, startDate, endDate);


            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("LOC1_CONVERTED", result.get(0).getLocation().getLocationName());
            assertEquals("LOC2_CONVERTED", result.get(1).getLocation().getLocationName());

            verify(rfidTxRepository, times(1)).findByCriteria(epc, tagId, startDateTime, endDateTime);
            conversionUtilsMockedStatic.verify(() -> ConversionUtils.convertStringCompositeNamesToShow("LOC1"), times(1));
            conversionUtilsMockedStatic.verify(() -> ConversionUtils.convertStringCompositeNamesToShow("LOC2"), times(1));
        }
    }

    @Test
    public void testGetRfidTxByCriteria_ValidInputsNoResults() {
        String epc = "EPC123";
        String tagId = "TAG123";
        String startDate = "2023-08-01 10:00:00";
        String endDate = "2023-08-02 12:00:00";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 8, 1, 10, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 8, 2, 12, 0, 0);

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {

            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(epc)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(epc)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(tagId)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(tagId)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(startDate)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(startDate)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(endDate)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(endDate)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.areDatesInOrder(startDateTime, endDateTime)).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(startDate)).thenReturn(startDateTime);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(endDate)).thenReturn(endDateTime);

            when(rfidTxRepository.findByCriteria(epc, tagId, startDateTime, endDateTime)).thenReturn(Collections.emptyList());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                rfidTxService.getRfidTxByCriteria(epc, tagId, startDate, endDate);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.NO_TRANSACTIONS.getMessage()));
            verify(rfidTxRepository, times(1)).findByCriteria(epc, tagId, startDateTime, endDateTime);
        }
    }

    @Test
    public void testGetRfidTxByCriteria_AllInputsNullOrEmpty() {
        String epc = null;
        String tagId = null;
        String startDate = null;
        String endDate = null;


        Location location1 = new Location();
        location1.setLocationName("LOC1");
        RfidTx rfidTx1 = new RfidTx();
        rfidTx1.setLocation(location1);

        Location location2 = new Location();
        location2.setLocationName("LOC2");
        RfidTx rfidTx2 = new RfidTx();
        rfidTx2.setLocation(location2);

        List<RfidTx> expectedList = Arrays.asList(rfidTx1, rfidTx2);

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {


            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(epc)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(tagId)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(startDate)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(endDate)).thenReturn(true);


            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNamesToShow("LOC1")).thenReturn("LOC1_CONVERTED");
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringCompositeNamesToShow("LOC2")).thenReturn("LOC2_CONVERTED");


            when(rfidTxRepository.findByCriteria(epc, tagId, null, null)).thenReturn(expectedList);


            List<RfidTx> result = rfidTxService.getRfidTxByCriteria(epc, tagId, startDate, endDate);


            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("LOC1_CONVERTED", result.get(0).getLocation().getLocationName());
            assertEquals("LOC2_CONVERTED", result.get(1).getLocation().getLocationName());

            verify(rfidTxRepository, times(1)).findByCriteria(epc, tagId, null, null);
            conversionUtilsMockedStatic.verify(() -> ConversionUtils.convertStringCompositeNamesToShow("LOC1"), times(1));
            conversionUtilsMockedStatic.verify(() -> ConversionUtils.convertStringCompositeNamesToShow("LOC2"), times(1));
        }
    }

    @Test
    public void testGetRfidTxByCriteria_StartDateAfterEndDate() {
        String epc = "EPC123";
        String tagId = "TAG123";
        String startDate = "2023-08-02 12:00:00";
        String endDate = "2023-08-01 10:00:00";
        LocalDateTime startDateTime = LocalDateTime.of(2023, 8, 2, 12, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2023, 8, 1, 10, 0, 0);

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class);
             MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(epc)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(epc)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(tagId)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(tagId)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(startDate)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(startDate)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(endDate)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidDateFormat(endDate)).thenReturn(true);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(startDate)).thenReturn(startDateTime);
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate(endDate)).thenReturn(endDateTime);
            validationUtilsMockedStatic.when(() -> ValidationUtils.areDatesInOrder(startDateTime, endDateTime)).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getRfidTxByCriteria(epc, tagId, startDate, endDate);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.DATE_ORDER_ERROR.getMessage()));
            verify(rfidTxRepository, times(0)).findByCriteria(anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }

    @Test
    public void testGetRfidTxByCriteria_InvalidTagIdFormat() {
        String epc = "EPC123";
        String tagId = "INVALID_TAG";
        String startDate = "2023-08-01 10:00:00";
        String endDate = "2023-08-02 12:00:00";

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(epc)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(epc)).thenReturn(true);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(tagId)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidTagFormat(tagId)).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getRfidTxByCriteria(epc, tagId, startDate, endDate);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.TAG_ID_FORMAT.getMessage()));
            verify(rfidTxRepository, times(0)).findByCriteria(anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }
    @Test
    public void testGetRfidTxByCriteria_InvalidEpcFormat() {
        String epc = "INVALID_EPC";
        String tagId = "TAG123";
        String startDate = "2023-08-01 10:00:00";
        String endDate = "2023-08-02 12:00:00";

        try (MockedStatic<ValidationUtils> validationUtilsMockedStatic = mockStatic(ValidationUtils.class)) {
            validationUtilsMockedStatic.when(() -> ValidationUtils.isNullOrEmpty(epc)).thenReturn(false);
            validationUtilsMockedStatic.when(() -> ValidationUtils.isValidEPCFormat(epc)).thenReturn(false);

            InvalidInputException exception = assertThrows(InvalidInputException.class, () -> {
                rfidTxService.getRfidTxByCriteria(epc, tagId, startDate, endDate);
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.EPC_FORMAT.getMessage()));
            verify(rfidTxRepository, times(0)).findByCriteria(anyString(), anyString(), any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }

    @Test
    public void testUpdateRfidTx_ValidInput() {
        when(rfidTxRepository.findById(any(RfidTxId.class))).thenReturn(Optional.of(existingRfidTx));


        when(siteService.findBySiteName(eq("UPDATEDSITENAME"))).thenReturn(Optional.of(site));
        when(locationService.locationNameMatchesSite(eq("UPDATEDLOCATIONNAME"), eq("UPDATEDSITENAME")))
                .thenReturn(Optional.of(location));
        when(rfidTxRepository.save(any(RfidTx.class))).thenReturn(existingRfidTx);

        RfidTx result = rfidTxService.updateRfidTx("TAG123", "EPC123", "2023-08-01 10:00:00", validRfidTxUpdateRequest);

        assertNotNull(result);
        verify(rfidTxRepository, times(1)).save(any(RfidTx.class));
    }

    @Test
    public void testUpdateRfidTx_NotFound_ThrowsResourceNotFoundException() {
        when(rfidTxRepository.findById(any(RfidTxId.class))).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            rfidTxService.updateRfidTx("TAG123", "EPC123", "2023-08-01 10:00:00", validRfidTxUpdateRequest);
        });

        assertTrue(exception.getMessage().contains(ErrorMessage.RFIDTX_NOT_FOUND.getMessage()));
        verify(rfidTxRepository, never()).save(any(RfidTx.class));
    }

    @Test
    public void testDeleteRfidTx_ValidInput() {
        try (MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-01 10:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0));
            when(rfidTxRepository.existsById(any(RfidTxId.class))).thenReturn(true);

            assertDoesNotThrow(() -> rfidTxService.deleteRfidTx("TAG123", "EPC123", "2023-08-01 10:00:00"));
            verify(rfidTxRepository, times(1)).deleteById(any(RfidTxId.class));
        }
    }

    @Test
    public void testDeleteRfidTx_NotFound_ThrowsResourceNotFoundException() {
        try (MockedStatic<ConversionUtils> conversionUtilsMockedStatic = mockStatic(ConversionUtils.class)) {
            conversionUtilsMockedStatic.when(() -> ConversionUtils.convertStringToDate("2023-08-01 10:00:00"))
                    .thenReturn(LocalDateTime.of(2023, 8, 1, 10, 0));
            when(rfidTxRepository.existsById(any(RfidTxId.class))).thenReturn(false);

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
                rfidTxService.deleteRfidTx("TAG123", "EPC123", "2023-08-01 10:00:00");
            });

            assertTrue(exception.getMessage().contains(ErrorMessage.RFID_TX_DELETE_FAILURE.getMessage()));
            verify(rfidTxRepository, never()).deleteById(any(RfidTxId.class));
        }
    }
}
