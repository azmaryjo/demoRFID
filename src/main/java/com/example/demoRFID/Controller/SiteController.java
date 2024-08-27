package com.example.demoRFID.Controller;

import com.example.demoRFID.Model.Exceptions.InvalidInputException;
import com.example.demoRFID.Model.Exceptions.ResourceNotFoundException;
import com.example.demoRFID.Model.Site;
import com.example.demoRFID.Service.SiteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
public class SiteController {

    private final SiteService siteService;
    private static final Logger logger = LoggerFactory.getLogger(SiteController.class);

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    /**
     * Create a new site.
     *
     * @param site The site details.
     * @return The created site or an error message.
     */
    @Operation(summary = "Create a new site", description = "Creates a new site in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Site created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Site.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict due to a duplicate site",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> createSite(@RequestBody Site site) {
        try {
            Site createdSite = siteService.createSite(site);
            return new ResponseEntity<>(createdSite, HttpStatus.CREATED);
        } catch (InvalidInputException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (DuplicateKeyException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get a site by its ID.
     *
     * @param siteId The ID of the site to retrieve.
     * @return The site details or an error message.
     */
    @Operation(summary = "Get a site by its ID", description = "Retrieves the details of a site using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Site retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Site.class))),
            @ApiResponse(responseCode = "400", description = "Invalid site ID",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Site not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/{siteId}")
    public ResponseEntity<?> getSite(
            @Parameter(description = "The ID of the site to retrieve.", required = true)
            @PathVariable Long siteId) {
        try {
            Site site = siteService.getSite(siteId);
            return new ResponseEntity<>(site, HttpStatus.OK);
        } catch (InvalidInputException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all sites.
     *
     * @return The list of all sites or an error message.
     */
    @Operation(summary = "Get all sites", description = "Retrieves a list of all sites in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sites retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Site.class))),
            @ApiResponse(responseCode = "404", description = "No sites found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping
    public ResponseEntity<?> getAllSites() {
        try {
            List<Site> sites = siteService.getAllSites();
            return new ResponseEntity<>(sites, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing site.
     *
     * @param site The site details to update.
     * @return The updated site or an error message.
     */
    @Operation(summary = "Update an existing site", description = "Updates the details of an existing site in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Site updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Site.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Site not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @PutMapping
    public ResponseEntity<?> updateSite(@RequestBody Site site) {
        try {
            Site updatedSite = siteService.updateSite(site);
            return new ResponseEntity<>(updatedSite, HttpStatus.OK);
        } catch (InvalidInputException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a site by its ID.
     *
     * @param siteId The ID of the site to delete.
     * @return An appropriate HTTP response based on the outcome of the operation.
     */
    @Operation(summary = "Delete a site by its ID", description = "Deletes a site from the system using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Site deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid site ID",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Site not found",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflict due to data integrity violation",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content)
    })
    @DeleteMapping("/{siteId}")
    public ResponseEntity<?> deleteSite(
            @Parameter(description = "The ID of the site to delete.", required = true)
            @PathVariable Long siteId) {
        try {
            siteService.deleteSite(siteId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (InvalidInputException e) {

            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        } catch (ResourceNotFoundException e) {

            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {

            return new ResponseEntity<>(e.getMessage(),HttpStatus.CONFLICT);
        } catch (Exception e) {

            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
