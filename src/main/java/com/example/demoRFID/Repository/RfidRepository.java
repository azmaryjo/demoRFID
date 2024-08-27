package com.example.demoRFID.Repository;

import com.example.demoRFID.Model.Rfid;
import com.example.demoRFID.Model.RfidId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


@Repository
public interface RfidRepository extends JpaRepository<Rfid, RfidId> {


    // Method to find an RFID entry by its composite key (tagId and epc)
    Optional<Rfid> findById(RfidId rfidId);

    /**
     * Method to find an RFID entry by its EPC and TagID combination.
     *
     * @param epc The Electronic Product Code (EPC) of the RFID tag.
     * @param tagId The unique identifier of the RFID tag.
     * @return An Optional containing the Rfid entity if a match is found, or an empty Optional if no match exists.
     */
    Optional<Rfid> findByEpcAndTagId(String epc, String tagId);

    /**
     * Method to find all RFID entries associated with a specific TagID.
     *
     * @param tagId The unique identifier of the RFID tag.
     * @return An Optional containing a list of Rfid entities associated with the given TagID, or an empty Optional if none are found.
     */
    Optional<List<Rfid>> findByTagId(String tagId);


    /**
     * Custom query method to check if the given RefCode is associated with the correct TagID and EPC combination.
     * This method uses a JPQL query to verify whether the specified TagID and EPC combination corresponds to the provided RefCode.
     *
     * @param tagId The unique identifier of the RFID tag.
     * @param epc The Electronic Product Code (EPC) of the RFID tag.
     * @param refCode The reference code associated with the product linked to this RFID tag.
     * @return true if the combination of TagID, EPC, and RefCode exists, false otherwise.
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Rfid r WHERE r.id.tagId = :tagId AND r.id.epc = :epc AND r.product.refCode = :refCode")
    boolean existsByTagIdAndEpcAndRefCode(@Param("tagId") String tagId, @Param("epc") String epc, @Param("refCode") String refCode);
}
