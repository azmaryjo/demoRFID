package com.example.demoRFID.Model;

import lombok.Data;
/**
 * Model class representing the latest data associated with an RFID tag's EPC.
 * This class uses Lombok's @Data annotation to automatically generate getter, setter, toString, equals,
 * and hashCode methods. It also includes constructors for initializing the fields.
 *
 * Fields:
 * - epc: The Electronic Product Code (EPC) of the RFID tag.
 * - numberOfTransactions: The total number of transactions associated with this EPC.
 * - averageRssi: The average Received Signal Strength Indicator (RSSI) value across all transactions.
 * - mostRecentLocation: The most recent location where the RFID tag was detected.
 *
 * Constructors:
 * - LatestEPC(String epc, Long numberOfTransactions, Double averageRssi, String mostRecentLocation): Initializes all fields.
 * - LatestEPC(): Default constructor for creating an empty instance.
 *
 * Overrides:
 * - toString(): Provides a string representation of the LatestEPC object, including all fields.
 */
@Data
public class LatestEPC {

    private  String epc;
    private  Long numberOfTransactions;
    private  Double averageRssi;
    private  String mostRecentLocation;

    public LatestEPC(String epc, Long numberOfTransactions, Double averageRssi, String mostRecentLocation) {
        this.epc = epc;
        this.numberOfTransactions = numberOfTransactions;
        this.averageRssi = averageRssi;
        this.mostRecentLocation = mostRecentLocation;
    }

    public LatestEPC() {

    }

    @Override
    public String toString() {
        return "LatestEPC{" +
                "epc='" + epc + '\'' +
                ", numberOfTransactions=" + numberOfTransactions +
                ", averageRssi=" + averageRssi +
                ", mostRecentLocation='" + mostRecentLocation + '\'' +
                '}'+"\n";
    }
}

