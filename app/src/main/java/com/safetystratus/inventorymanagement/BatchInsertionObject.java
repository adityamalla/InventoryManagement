package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class BatchInsertionObject implements Serializable {
    public String location_id;
    public String room_id;
    public String inventory_id;
    public String scanned_by;
    public String scanned;
    public String reconc_id;
    public String rfid_code;

    public BatchInsertionObject(String location_id, String room_id, String inventory_id, String scanned_by, String scanned, String reconc_id, String rfid_code) {
        this.location_id = location_id;
        this.room_id = room_id;
        this.inventory_id = inventory_id;
        this.scanned_by = scanned_by;
        this.scanned = scanned;
        this.reconc_id = reconc_id;
        this.rfid_code = rfid_code;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
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

    public String getScanned() {
        return scanned;
    }

    public void setScanned(String scanned) {
        this.scanned = scanned;
    }

    public String getReconc_id() {
        return reconc_id;
    }

    public void setReconc_id(String reconc_id) {
        this.reconc_id = reconc_id;
    }

    public String getRfid_code() {
        return rfid_code;
    }

    public void setRfid_code(String rfid_code) {
        this.rfid_code = rfid_code;
    }
}
