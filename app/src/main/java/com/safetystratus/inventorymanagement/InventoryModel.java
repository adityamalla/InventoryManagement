package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class InventoryModel  implements Serializable {
    public String inv_id;
    public String code;
    public String productName;
    public String cas_number;
    public String status_id;
    public String status;
    public String facil_id;
    public String room_id;
    public String room;
    public String owner;
    public String notes;
    public String comments;
    public String volume_mass;
    public String volume_mass_unit_id;
    public String volume_mass_unit;
    public String rfidCode;
    public String concentration;
    public String concentration_unit_abbrevation_id;
    public String concentration_unit_abbrevation;
    public String object_id;
    public String object_table;

    public InventoryModel(String inv_id, String code, String productName, String cas_number, String status_id,String status, String facil_id, String room_id, String room, String owner,
                          String notes, String comments, String volume_mass, String volume_mass_unit_id,String volume_mass_unit, String rfidCode, String concentration,
                          String concentration_unit_abbrevation_id, String concentration_unit_abbrevation, String object_id, String object_table) {
        this.inv_id = inv_id;
        this.code = code;
        this.productName = productName;
        this.cas_number = cas_number;
        this.status_id = status_id;
        this.status = status;
        this.facil_id = facil_id;
        this.room_id = room_id;
        this.room = room;
        this.owner = owner;
        this.notes = notes;
        this.comments = comments;
        this.volume_mass = volume_mass;
        this.volume_mass_unit_id = volume_mass_unit_id;
        this.volume_mass_unit = volume_mass_unit;
        this.rfidCode = rfidCode;
        this.concentration = concentration;
        this.concentration_unit_abbrevation_id = concentration_unit_abbrevation_id;
        this.concentration_unit_abbrevation = concentration_unit_abbrevation;
        this.object_id = object_id;
        this.object_table = object_table;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getObject_table() {
        return object_table;
    }

    public void setObject_table(String object_table) {
        this.object_table = object_table;
    }

    public String getVolume_mass_unit_id() {
        return volume_mass_unit_id;
    }

    public void setVolume_mass_unit_id(String volume_mass_unit_id) {
        this.volume_mass_unit_id = volume_mass_unit_id;
    }

    public String getConcentration_unit_abbrevation_id() {
        return concentration_unit_abbrevation_id;
    }

    public void setConcentration_unit_abbrevation_id(String concentration_unit_abbrevation_id) {
        this.concentration_unit_abbrevation_id = concentration_unit_abbrevation_id;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public String getFacil_id() {
        return facil_id;
    }

    public void setFacil_id(String facil_id) {
        this.facil_id = facil_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public String getConcentration_unit_abbrevation() {
        return concentration_unit_abbrevation;
    }

    public void setConcentration_unit_abbrevation(String concentration_unit_abbrevation) {
        this.concentration_unit_abbrevation = concentration_unit_abbrevation;
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
