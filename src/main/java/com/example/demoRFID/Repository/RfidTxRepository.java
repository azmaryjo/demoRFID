package com.example.demoRFID.Repository;

import com.example.demoRFID.Model.LatestEPC;
import com.example.demoRFID.Model.RfidTx;
import com.example.demoRFID.Model.RfidTxId;
import com.example.demoRFID.Model.TopEPC;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RfidTxRepository extends JpaRepository<RfidTx, RfidTxId> {
    /**
     * Custom query method to find the latest RFID scans based on specific criteria.
     * This method uses a Common Table Expression (CTE) to retrieve the most recent scan for each EPC within a given date range.
     * It also calculates the number of transactions and the average RSSI for each EPC.
     *
     * @param startDateTime The start of the date range for the scans.
     * @param endDateTime The end of the date range for the scans.
     * @param epc The EPC to filter by, or null to include all EPCs.
     * @param siteName The site name to filter by, or null to include all sites.
     * @return A list of LatestEPC objects representing the latest scan details for each EPC.
     */
    @Query(value = "WITH LatestScans AS (" +
            "    SELECT" +
            "        epc as epc," +
            "        CONCAT(s.siteName, ' - ', loc.locationName) AS mostRecentLocation," +
            "        rssi as rssi," +
            "        scanDate as scanDate," +
            "        COUNT(*) OVER (PARTITION BY epc) AS NumberOfTransactions," +  // Precompute count
            "        AVG(rssi) OVER (PARTITION BY epc) AS AverageRSSI," +  // Precompute average
            "        ROW_NUMBER() OVER (PARTITION BY epc ORDER BY scanDate DESC) AS rn" +
            "    FROM" +
            "        RfidTx tx" +
            "        JOIN Location loc ON tx.location.locationId = loc.locationId" +
            "        JOIN Site s ON loc.site.siteId = s.siteId" +
            "    WHERE" +
            "        scanDate BETWEEN :startdatetime AND :enddatetime" +
            "        AND (:siteName IS NULL OR s.siteName = :siteName)" +
            "        AND (:epc IS NULL OR tx.epc = :epc)" +
            ")" +
            " SELECT new com.example.demoRFID.Model.LatestEPC(epc, NumberOfTransactions,AverageRSSI,mostRecentLocation)" +
            "   FROM" +
            "    LatestScans" +
            "   WHERE" +
            "    rn = 1"
            )
    List<LatestEPC> findLatestScans(@Param("startdatetime") LocalDateTime startDateTime,
                                    @Param("enddatetime") LocalDateTime endDateTime,
                                    @Param("epc") String epc,
                                    @Param("siteName") String siteName);

    /**
     * Custom query method to find the top EPCs by the number of reads within a given date range.
     * This method groups RFID transactions by EPC and counts the number of transactions for each EPC.
     * It then orders the results by the count in descending order and limits the number of results returned.
     *
     * @param limit The maximum number of top EPCs to return.
     * @param startDateTime The start of the date range for the scans.
     * @param endDateTime The end of the date range for the scans.
     * @return A list of TopEPC objects representing the EPCs with the highest read counts.
     */
    @Query("SELECT new com.example.demoRFID.Model.TopEPC(tx.epc, COUNT(*)) " +
            "FROM RfidTx tx " +
            "WHERE tx.scanDate BETWEEN :startdatetime AND :enddatetime " +
            "GROUP BY tx.epc " +
            "ORDER BY COUNT(*) DESC "+
            "LIMIT :limit")
    List<TopEPC> findTopReads(
            @Param("limit") int limit,
            @Param("startdatetime") LocalDateTime startDateTime,
            @Param("enddatetime") LocalDateTime endDateTime);

    /**
     * Method to retrieve all RFID transactions associated with a specific EPC.
     *
     * @param epc The EPC to filter by.
     * @return An Optional containing a list of RfidTx objects associated with the given EPC, or an empty Optional if none are found.
     */
    Optional<List<RfidTx>>  findByEpc(String epc);

    /**
     * Method to retrieve all RFID transactions associated with a specific TagID.
     *
     * @param tagId The TagID to filter by.
     * @return An Optional containing a list of RfidTx objects associated with the given TagID, or an empty Optional if none are found.
     */
    Optional<List<RfidTx>> findByTagId(String tagId);

    /**
     * Method to retrieve all RFID transactions associated with a specific EPC and TagID combination.
     *
     * @param epc The EPC to filter by.
     * @param tagId The TagID to filter by.
     * @return An Optional containing a list of RfidTx objects associated with the given EPC and TagID combination, or an empty Optional if none are found.
     */
    Optional<List<RfidTx>> findByEpcAndTagId(String epc, String tagId);

    /**
     * Method to retrieve all RFID transactions that occurred within a specific date range.
     *
     * @param startDate The start of the date range for the transactions.
     * @param endDate The end of the date range for the transactions.
     * @return An Optional containing a list of RfidTx objects within the specified date range, or an empty Optional if none are found.
     */
    Optional<List<RfidTx>> findByScanDateBetween(LocalDateTime startDate, LocalDateTime endDate);


    /**
     * Custom query method to retrieve RFID transactions based on various criteria.
     * This method allows filtering by EPC, TagID, and a date range. If any of the parameters are null,
     * they are ignored in the filtering.
     *
     * @param epc The EPC to filter by, or null to include all EPCs.
     * @param tagId The TagID to filter by, or null to include all TagIDs.
     * @param startDate The start of the date range for the transactions, or null to ignore the start date.
     * @param endDate The end of the date range for the transactions, or null to ignore the end date.
     * @return A list of RfidTx objects that match the given criteria.
     */
    @Query("SELECT rt FROM RfidTx rt WHERE " +
            "(:epc IS NULL OR rt.epc = :epc) AND " +
            "(:tagId IS NULL OR rt.tagId = :tagId) AND " +
            "(:startDate IS NULL OR rt.scanDate >= :startDate) AND " +
            "(:endDate IS NULL OR rt.scanDate <= :endDate)")
    List<RfidTx> findByCriteria(@Param("epc") String epc,
                                @Param("tagId") String tagId,
                                @Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);





}
