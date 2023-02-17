package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class RFIDScanDataObj implements Serializable {
    public String inventory_id;
    public String scanned_by;
    public String scanned_date;
    public String rfid_code;

    public RFIDScanDataObj(String inventory_id, String scanned_by, String scanned_date, String rfid_code) {
        this.inventory_id = inventory_id;
        this.scanned_by = scanned_by;
        this.scanned_date = scanned_date;
        this.rfid_code = rfid_code;
    }

    public String getRfid_code() {
        return rfid_code;
    }

    public void setRfid_code(String rfid_code) {
        this.rfid_code = rfid_code;
    }

    public String getInventory_id() {
        return inventory_id;
    }

    public void setInventory_id(String inventory_id) {
        this.inventory_id = inventory_id;
    }

    public String getScanned_by() {
        return scanned_by;
    }

    public void setScanned_by(String scanned_by) {
        this.scanned_by = scanned_by;
    }

    public String getScanned_date() {
        return scanned_date;
    }

    public void setScanned_date(String scanned_date) {
        this.scanned_date = scanned_date;
    }
}
