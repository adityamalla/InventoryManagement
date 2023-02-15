package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class BracodeScanAPIObject implements Serializable {
    public String user_id;
    public String token;
    public String site_id;
    public String code;
    public String status_id;
    public String room_id;
    public String object_id;
    public String object_table;
    public String notes;
    public String comments;
    public String quantity;
    public String uom_id;
    public String c_uom_id;
    public String concentration;

    public BracodeScanAPIObject(String user_id, String token, String site_id, String code, String status_id, String room_id, String notes, String comments, String quantity, String uom_id, String c_uom_id, String concentration, String object_id, String object_table) {
        this.user_id = user_id;
        this.token = token;
        this.site_id = site_id;
        this.code = code;
        this.status_id = status_id;
        this.room_id = room_id;
        this.notes = notes;
        this.comments = comments;
        this.quantity = quantity;
        this.uom_id = uom_id;
        this.c_uom_id = c_uom_id;
        this.concentration = concentration;
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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUom_id() {
        return uom_id;
    }

    public void setUom_id(String uom_id) {
        this.uom_id = uom_id;
    }

    public String getC_uom_id() {
        return c_uom_id;
    }

    public void setC_uom_id(String c_uom_id) {
        this.c_uom_id = c_uom_id;
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }
}
