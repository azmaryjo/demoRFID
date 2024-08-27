package com.example.demoRFID.Model;

/**
 * A simple data class representing an RFID tag's EPC (Electronic Product Code) and its read count.
 * This class is used to track the frequency with which a particular EPC has been read.
 *
 * Fields:
 * - epc: The Electronic Product Code (EPC) of the RFID tag.
 * - readCount: The number of times the EPC has been read.
 *
 * Constructors:
 * - TopEPC(): Default constructor for creating an empty instance.
 * - TopEPC(String epc, long readCount): Constructor for initializing the epc and readCount fields.
 *
 * Overrides:
 * - toString(): Provides a string representation of the TopEPC object, including the EPC and read count.
 */

public class TopEPC {
    private String epc;
    private long readCount;

    // Default constructor
    public TopEPC() {}

    // Constructor
    public TopEPC(String epc, long readCount) {
        this.epc = epc;
        this.readCount = readCount;
    }

    @Override
    public String toString() {
        return "TopEPC{" +
                "epc='" + epc + '\'' +
                ", read Counts=" + readCount +
                "}\n";
    }
}
