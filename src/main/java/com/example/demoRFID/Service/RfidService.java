package com.example.demoRFID.Service;


import com.example.demoRFID.Model.RfidId;
import com.example.demoRFID.Repository.RfidRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RfidService {

    private final RfidRepository rfidRepository;
    private final ProductService productService;
    private static final Logger logger = LoggerFactory.getLogger(RfidService.class);

    @Autowired
    public RfidService(RfidRepository rfidRepository, ProductService productService) {
        this.rfidRepository = rfidRepository;
        this.productService = productService;
    }
    /**
     * Method to check if a given TagID matches the specified EPC combination.
     * This method creates a composite key (RfidId) using the provided TagID and EPC,
     * then checks the repository to see if an entry with this composite key exists.
     *
     * @param tagId The unique identifier of the RFID tag.
     * @param epc The Electronic Product Code (EPC) associated with the RFID tag.
     * @return true if a matching TagID and EPC combination exists, false otherwise.
     */
    public boolean checkTagIdMatchesEpc(String tagId, String epc) {
        RfidId rfidId = new RfidId(tagId,epc);
        return rfidRepository.findById(rfidId).isPresent();
    }

    /**
     * Method to check if the given RefCode is associated with the correct TagID and EPC combination.
     * This method checks the repository to determine whether the specified RefCode matches
     * the provided TagID and EPC combination.
     *
     * @param tagId The unique identifier of the RFID tag.
     * @param epc The Electronic Product Code (EPC) associated with the RFID tag.
     * @param refCode The reference code associated with the product linked to this RFID tag.
     * @return true if the RefCode matches the TagID and EPC combination, false otherwise.
     */
    public boolean isRefCodeValidForTagIdAndEpc(String tagId, String epc, String refCode) {
        return rfidRepository.existsByTagIdAndEpcAndRefCode(tagId, epc, refCode);
    }
    //TODO:implement following methods if there is time
    //    public Rfid createRfid(Rfid rfid) {
    //        return rfidRepository.save(rfid);
    //    }
    //    public Optional<Rfid> getRfid(String epc, String tagId) {
    //        return rfidRepository.findByEpcAndTagId(epc, tagId);
    //    }
    //    public List<Rfid> getAllRfids() {
    //        return rfidRepository.findAll();
    //    }
    //    public Rfid updateRfid(Rfid rfid) {
    //        return rfidRepository.save(rfid);
    //    }
    //    public void deleteRfid(RfidId id) {
    //        rfidRepository.deleteById(id);
    //    }

}

