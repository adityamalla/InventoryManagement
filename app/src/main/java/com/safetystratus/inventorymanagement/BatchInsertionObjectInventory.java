package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class BatchInsertionObjectInventory implements Serializable {
    public String id;
    public String opened_date;
    public String name;
    public String room_id;
    public String sec_code;
    public String object_table;
    public String modified_user_id;
    public String modified_date;
    public String last_test_date;
    public String primary_user_id;
    public String lot;
    public String create_date;
    public String code;
    public String expiration_date;
    public String create_user_id;
    public String object_id;
    public String facil_id;
    public String room;
    public String receipt_date;
    public String notes;
    public String comment;
    public String quantity;
    public String concentration;
    public String quantity_unit_abbreviation;
    public String quantity_unit_abbreviation_id;
    public String concentration_unit_abbrevation;
    public String concentration_unit_abbrevation_id;
    public String cas_number;
    public String status;
    public String status_id;
    public String loc;
    public String loc_id;
    public String test_frequency;
    public String owner;

    public BatchInsertionObjectInventory(String id, String opened_date, String name, String room_id, String sec_code, String object_table, String modified_user_id, String modified_date, String last_test_date,
                                         String primary_user_id, String lot, String create_date, String code, String expiration_date, String create_user_id, String object_id,
                                         String facil_id, String room, String receipt_date, String notes, String comment, String quantity, String concentration, String quantity_unit_abbreviation,
                                         String quantity_unit_abbreviation_id, String concentration_unit_abbrevation, String concentration_unit_abbrevation_id, String cas_number, String status, String status_id, String loc, String loc_id, String test_frequency, String owner) {
        this.id = id;
        this.opened_date = opened_date;
        this.name = name;
        this.room_id = room_id;
        this.sec_code = sec_code;
        this.object_table = object_table;
        this.modified_user_id = modified_user_id;
        this.modified_date = modified_date;
        this.last_test_date = last_test_date;
        this.primary_user_id = primary_user_id;
        this.lot = lot;
        this.create_date = create_date;
        this.code = code;
        this.expiration_date = expiration_date;
        this.create_user_id = create_user_id;
        this.object_id = object_id;
        this.facil_id = facil_id;
        this.room = room;
        this.receipt_date = receipt_date;
        this.notes = notes;
        this.comment = comment;
        this.quantity = quantity;
        this.concentration = concentration;
        this.quantity_unit_abbreviation = quantity_unit_abbreviation;
        this.quantity_unit_abbreviation_id = quantity_unit_abbreviation_id;
        this.concentration_unit_abbrevation = concentration_unit_abbrevation;
        this.concentration_unit_abbrevation_id = concentration_unit_abbrevation_id;
        this.cas_number = cas_number;
        this.status = status;
        this.status_id = status_id;
        this.loc = loc;
        this.loc_id = loc_id;
        this.test_frequency = test_frequency;
        this.owner = owner;
    }

    public String getTest_frequency() {
        return test_frequency;
    }

    public void setTest_frequency(String test_frequency) {
        this.test_frequency = test_frequency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpened_date() {
        return opened_date;
    }

    public void setOpened_date(String opened_date) {
        this.opened_date = opened_date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getSec_code() {
        return sec_code;
    }

    public void setSec_code(String sec_code) {
        this.sec_code = sec_code;
    }

    public String getObject_table() {
        return object_table;
    }

    public void setObject_table(String object_table) {
        this.object_table = object_table;
    }

    public String getModified_user_id() {
        return modified_user_id;
    }

    public void setModified_user_id(String modified_user_id) {
        this.modified_user_id = modified_user_id;
    }

    public String getModified_date() {
        return modified_date;
    }

    public void setModified_date(String modified_date) {
        this.modified_date = modified_date;
    }

    public String getLast_test_date() {
        return last_test_date;
    }

    public void setLast_test_date(String last_test_date) {
        this.last_test_date = last_test_date;
    }

    public String getPrimary_user_id() {
        return primary_user_id;
    }

    public void setPrimary_user_id(String primary_user_id) {
        this.primary_user_id = primary_user_id;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExpiration_date() {
        return expiration_date;
    }

    public void setExpiration_date(String expiration_date) {
        this.expiration_date = expiration_date;
    }

    public String getCreate_user_id() {
        return create_user_id;
    }

    public void setCreate_user_id(String create_user_id) {
        this.create_user_id = create_user_id;
    }

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getFacil_id() {
        return facil_id;
    }

    public void setFacil_id(String facil_id) {
        this.facil_id = facil_id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getReceipt_date() {
        return receipt_date;
    }

    public void setReceipt_date(String receipt_date) {
        this.receipt_date = receipt_date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public String getQuantity_unit_abbreviation() {
        return quantity_unit_abbreviation;
    }

    public void setQuantity_unit_abbreviation(String quantity_unit_abbreviation) {
        this.quantity_unit_abbreviation = quantity_unit_abbreviation;
    }

    public String getQuantity_unit_abbreviation_id() {
        return quantity_unit_abbreviation_id;
    }

    public void setQuantity_unit_abbreviation_id(String quantity_unit_abbreviation_id) {
        this.quantity_unit_abbreviation_id = quantity_unit_abbreviation_id;
    }

    public String getConcentration_unit_abbrevation() {
        return concentration_unit_abbrevation;
    }

    public void setConcentration_unit_abbrevation(String concentration_unit_abbrevation) {
        this.concentration_unit_abbrevation = concentration_unit_abbrevation;
    }

    public String getConcentration_unit_abbrevation_id() {
        return concentration_unit_abbrevation_id;
    }

    public void setConcentration_unit_abbrevation_id(String concentration_unit_abbrevation_id) {
        this.concentration_unit_abbrevation_id = concentration_unit_abbrevation_id;
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

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getLoc_id() {
        return loc_id;
    }

    public void setLoc_id(String loc_id) {
        this.loc_id = loc_id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
