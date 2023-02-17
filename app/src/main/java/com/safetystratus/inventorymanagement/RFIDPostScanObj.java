package com.safetystratus.inventorymanagement;

import java.io.Serializable;
import java.util.ArrayList;

public class RFIDPostScanObj implements Serializable {
    public String user_id;
    public String token;
    public String site_id;
    public String room_id;
    public String inventory_details;

    public RFIDPostScanObj(String user_id, String token, String site_id,String room_id, String inventory_details) {
        this.user_id = user_id;
        this.token = token;
        this.site_id = site_id;
        this.room_id = room_id;
        this.inventory_details = inventory_details;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
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

    public String getInventory_details() {
        return inventory_details;
    }

    public void setInventory_details(String inventory_details) {
        this.inventory_details = inventory_details;
    }
}
