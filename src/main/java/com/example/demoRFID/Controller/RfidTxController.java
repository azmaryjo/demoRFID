package com.example.demoRFID.Controller;

import com.example.demoRFID.ErrorMessage;
import com.example.demoRFID.Model.LatestEPC;
import com.example.demoRFID.Model.Exceptions.InvalidInputException;
import com.example.demoRFID.Model.Exceptions.ResourceNotFoundException;
import com.example.demoRFID.Model.Request.RfidTxRequest;
import com.example.demoRFID.Model.Request.RfidTxUpdateRequest;
import com.example.demoRFID.Model.RfidTx;
import com.example.demoRFID.Model.TopEPC;
import com.example.demoRFID.Service.RfidTxService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/rfid")
public class RfidTxController {
    @Autowired
    private RfidTxService rfidTxService;


    /**
     * Get the latest scans within a date range.
     *
     * @param startdatetime The start date and time for the scan range.
     * @param enddatetime The end date and time for the scan range.
     * @param epc The EPC to filter by (optional).
     * @param siteName The site name to filter by (optional).
     * @return The list of latest scans or an error message.
     */
    @Operation(summary = "Get the latest scans within a date range", description = "Retrieves the latest scans within the specified date range, optionally filtered by EPC and site name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest scans retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LatestEPC.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No scans found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/latest-scans")
    public ResponseEntity<?> getLatestScans(
            @Parameter(description = "The start date and time for the scan range, in the format 'yyyy-MM-dd HH:mm:ss'.", required = true)
            @RequestParam String startdatetime,
            @Parameter(description = "The end date and time for the scan range, in the format 'yyyy-MM-dd HH:mm:ss'.", required = true)
            @RequestParam String enddatetime,
            @Parameter(description = "The EPC to filter by (optional).")
            @RequestParam(required = false) String epc,
            @Parameter(description = "The site name to filter by (optional).")
            @RequestParam(required = false) String siteName) {
        List<LatestEPC> latestScansList;
        try {
            latestScansList = rfidTxService.getLatestScans(startdatetime, enddatetime, epc, siteName);
        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(latestScansList);
    }

    /**
     * Get the top N reads within a date range.
     *
     * @param N The number of top reads to retrieve.
     * @param startdatetime The start date and time for the scan range.
     * @param enddatetime The end date and time for the scan range.
     * @return The list of top reads or an error message.
     */
    @Operation(summary = "Get the top N reads within a date range", description = "Retrieves the top N reads within the specified date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top reads retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TopEPC.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No reads found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/top-reads")
    public ResponseEntity<?> getTopReads(
            @Parameter(description = "The number of top reads to retrieve.", required = true)
            @RequestParam int N,
            @Parameter(description = "The start date and time for the scan range, in the format 'yyyy-MM-dd HH:mm:ss'.", required = true)
            @RequestParam String startdatetime,
            @Parameter(description = "The end date and time for the scan range, in the format 'yyyy-MM-dd HH:mm:ss'.", required = true)
            @RequestParam String enddatetime) {
        List<TopEPC> topReadsList;
        try {
            topReadsList = rfidTxService.getTopReads(N, startdatetime, enddatetime);
        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(topReadsList);
    }

    /**
     * Add a new RFID transaction.
     *
     * @param rfidTxRequest The request object containing the RFID transaction details.
     * @return The inserted RFID transaction or an error message.
     */
    @Operation(summary = "Add a new RFID transaction", description = "Creates a new RFID transaction in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "RFID transaction added successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RfidTx.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict due to data integrity violation",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @PostMapping("/addRfidTx")
    public ResponseEntity<?> addRfidTx(@RequestBody RfidTxRequest rfidTxRequest) {
        RfidTx inserted;
        try {
            inserted = rfidTxService.addRfidTx(rfidTxRequest);
        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }  catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(inserted);

    }

    // Endpoint to get RfidTx by EPC
    /**
     * Get RFID transactions by EPC.
     *
     * @param epc The EPC to search for.
     * @return The list of RFID transactions associated with the EPC or an error message.
     */
    @Operation(summary = "Get RFID transactions by EPC", description = "Retrieves a list of RFID transactions associated with a specific EPC.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "RFID transactions retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RfidTx.class))),
            @ApiResponse(responseCode = "400", description = "Invalid EPC format",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No RFID transactions found for the given EPC",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/by-epc")
    public ResponseEntity<?> getByEpc(@Parameter(description = "The EPC to search for.", required = true)
        @RequestParam String epc) {
        List<RfidTx> rfidTxList;
        try {
            rfidTxList = rfidTxService.getRfidTxByEpc(epc);
        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(rfidTxList);
    }

    // Endpoint to get RfidTx by TagID
    /**
     * Get RFID transactions by TagID.
     *
     * @param tagId The TagID to search for.
     * @return The list of RFID transactions associated with the TagID or an error message.
     */
    @Operation(summary = "Get RFID transactions by TagID", description = "Retrieves a list of RFID transactions associated with a specific TagID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "RFID transactions retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RfidTx.class))),
            @ApiResponse(responseCode = "400", description = "Invalid TagID format",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No RFID transactions found for the given TagID",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/by-tagid")
    public ResponseEntity<?> getByTagId(
            @Parameter(description = "The TagID to search for.", required = true)
            @RequestParam String tagId) {
        List<RfidTx> rfidTxList ;
        try {
            rfidTxList=rfidTxService.getRfidTxByTagId(tagId);
        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(rfidTxList);
    }

    /**
     * Get RFID transactions by EPC and TagID.
     *
     * @param epc The EPC to search for.
     * @param tagId The TagID to search for.
     * @return The list of RFID transactions associated with the EPC and TagID or an error message.
     */
    @Operation(summary = "Get RFID transactions by EPC and TagID", description = "Retrieves a list of RFID transactions associated with a specific EPC and TagID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "RFID transactions retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RfidTx.class))),
            @ApiResponse(responseCode = "400", description = "Invalid EPC or TagID format",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No RFID transactions found for the given EPC and TagID",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/by-epc-and-tagid")
    public ResponseEntity<?> getByEpcAndTagId(
            @Parameter(description = "The EPC to search for.", required = true)
            @RequestParam String epc,
            @Parameter(description = "The TagID to search for.", required = true)
            @RequestParam String tagId) {
        List<RfidTx> rfidTxList ;
        try {
            rfidTxList= rfidTxService.getRfidTxByEpcAndTagId(epc, tagId);
        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }  catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(rfidTxList);

    }

    /**
     * Get RFID transactions by scan date range.
     *
     * @param startDate The start date for the scan range in 'yyyy-MM-dd HH:mm:ss' format.
     * @param endDate The end date for the scan range in 'yyyy-MM-dd HH:mm:ss' format.
     * @return The list of RFID transactions within the date range or an error message.
     */
    @Operation(summary = "Get RFID transactions by scan date range", description = "Retrieves a list of RFID transactions that occurred within the specified date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "RFID transactions retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RfidTx.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date format or date range",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No RFID transactions found for the given date range",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/by-scan-date-range")
    public ResponseEntity<?> getByScanDateRange(
            @Parameter(description = "The start date for the scan range in 'yyyy-MM-dd HH:mm:ss' format.", required = true)
            @RequestParam String startDate,
            @Parameter(description = "The end date for the scan range in 'yyyy-MM-dd HH:mm:ss' format.", required = true)
            @RequestParam String endDate) {
        List<RfidTx> rfidTxList ;
        try {
            rfidTxList= rfidTxService.getRfidTxByScanDateRange(startDate, endDate);
        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
        catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(rfidTxList);
    }

    /**
     * Search RFID transactions by criteria.
     *
     * @param epc The EPC to search for (optional).
     * @param tagId The TagID to search for (optional).
     * @param startDate The start date for the search range in 'yyyy-MM-dd HH:mm:ss' format (optional).
     * @param endDate The end date for the search range in 'yyyy-MM-dd HH:mm:ss' format (optional).
     * @return The list of RFID transactions that match the criteria or an error message.
     */
    @Operation(summary = "Search RFID transactions by criteria", description = "Retrieves a list of RFID transactions that match the provided criteria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "RFID transactions retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RfidTx.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No RFID transactions found for the given criteria",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<?> searchRfidTx(
            @Parameter(description = "The EPC to search for (optional).")
            @RequestParam(required = false) String epc,
            @Parameter(description = "The TagID to search for (optional).")
            @RequestParam(required = false) String tagId,
            @Parameter(description = "The start date for the search range in 'yyyy-MM-dd HH:mm:ss' format (optional).")
            @RequestParam(required = false) String startDate,
            @Parameter(description = "The end date for the search range in 'yyyy-MM-dd HH:mm:ss' format (optional).")
            @RequestParam(required = false) String endDate) {
        List<RfidTx> rfidTxList ;
        try {
            rfidTxList= rfidTxService.getRfidTxByCriteria(epc, tagId, startDate, endDate);
        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(rfidTxList);
    }

    /**
     * Delete an RFID transaction by TagID, EPC, and scan date.
     *
     * @param tagId The TagID of the RFID transaction to delete.
     * @param epc The EPC of the RFID transaction to delete.
     * @param scanDate The scan date of the RFID transaction to delete in 'yyyy-MM-dd HH:mm:ss' format.
     * @return A success message or an error message.
     */
    @Operation(summary = "Delete an RFID transaction", description = "Deletes an RFID transaction using its TagID, EPC, and scan date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "RFID transaction deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "RFID transaction not found or could not be deleted due to a conflict",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRfidTx(
            @Parameter(description = "The TagID of the RFID transaction to delete.", required = true)
            @RequestParam String tagId,
            @Parameter(description = "The EPC of the RFID transaction to delete.", required = true)
            @RequestParam String epc,
            @Parameter(description = "The scan date of the RFID transaction to delete in 'yyyy-MM-dd HH:mm:ss' format.", required = true)
            @RequestParam String scanDate) {
        try {
            rfidTxService.deleteRfidTx(tagId,epc,scanDate);
        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (ResourceNotFoundException ex) {
           return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }

         return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorMessage.RFIDTX_DELETE_SUCCESS.format(tagId,epc,scanDate));
    }

    /**
     * Update an existing RFID transaction.
     *
     * @param tagId The TagID of the RFID transaction to update.
     * @param epc The EPC of the RFID transaction to update.
     * @param scanDate The scan date of the RFID transaction to update in 'yyyy-MM-dd HH:mm:ss' format.
     * @param updatedRfidTx The updated RFID transaction details.
     * @return The updated RFID transaction or an error message.
     */
    @Operation(summary = "Update an existing RFID transaction", description = "Updates an RFID transaction using its TagID, EPC, and scan date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "RFID transaction updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RfidTx.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "RFID transaction not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @PutMapping("/{tagId}/{epc}/{scanDate}")
    public ResponseEntity<?> updateRfidTx(
            @Parameter(description = "The TagID of the RFID transaction to update.", required = true)
            @RequestParam String tagId,
            @Parameter(description = "The EPC of the RFID transaction to update.", required = true)
            @RequestParam String epc,
            @Parameter(description = "The scan date of the RFID transaction to update in 'yyyy-MM-dd HH:mm:ss' format.", required = true)
            @RequestParam String scanDate,
            @Parameter(description = "The updated RFID transaction details.", required = true)
            @RequestBody RfidTxUpdateRequest updatedRfidTx) {
        RfidTx rfidTx;

        try {
            rfidTx= rfidTxService.updateRfidTx(tagId,epc,scanDate, updatedRfidTx);
        } catch (InvalidInputException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
        return new ResponseEntity<>(rfidTx, HttpStatus.OK);
    }

}

