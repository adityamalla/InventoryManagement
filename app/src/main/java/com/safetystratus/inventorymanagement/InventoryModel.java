package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class InventoryModel  implements Serializable {
    public String inv_id;
    public String code;
    public String productName;
    public String cas_number;
    public String status;
    public String location;
    public String owner;
    public String notes;
    public String comments;
    public String volume_mass;
    public String volume_mass_unit;
    public String rfidCode;

    public InventoryModel(String inv_id, String code, String productName, String cas_number, String status, String location, String owner,  String notes, String comments, String volume_mass, String volume_mass_unit, String rfidCode) {
        this.inv_id = inv_id;
        this.code = code;
        this.productName = productName;
        this.cas_number = cas_number;
        this.status = status;
        this.location = location;
        this.owner = owner;
        this.notes = notes;
        this.comments = comments;
        this.volume_mass = volume_mass;
        this.volume_mass_unit = volume_mass_unit;
        this.rfidCode = rfidCode;
    }

    public String getInv_id() {
        return inv_id;
    }

    public void setInv_id(String inv_id) {
        this.inv_id = inv_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCas_number() {
        return cas_number;
    }

    public void setCas_number(String cas_number) {
        this.cas_number = cas_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getVolume_mass() {
        return volume_mass;
    }

    public void setVolume_mass(String volume_mass) {
        this.volume_mass = volume_mass;
    }

    public String getVolume_mass_unit() {
        return volume_mass_unit;
    }

    public void setVolume_mass_unit(String volume_mass_unit) {
        this.volume_mass_unit = volume_mass_unit;
    }

    public String getRfidCode() {
        return rfidCode;
    }

    public void setRfidCode(String rfidCode) {
        this.rfidCode = rfidCode;
    }
}
