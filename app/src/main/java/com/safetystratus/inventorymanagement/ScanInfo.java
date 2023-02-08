package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class ScanInfo implements Serializable {
    private String id;
    private String room_id;
    private String room_name;
    private String facility_id;
    private String facility_name;
    private String json_data;

    public ScanInfo(String id, String room_id,String room_name, String facility_id,String facility_name, String json_data) {
        this.id = id;
        this.room_id = room_id;
        this.room_name = room_name;
        this.facility_id = facility_id;
        this.facility_name = facility_name;
        this.json_data = json_data;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getFacility_name() {
        return facility_name;
    }

    public void setFacility_name(String facility_name) {
        this.facility_name = facility_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getFacility_id() {
        return facility_id;
    }

    public void setFacility_id(String facility_id) {
        this.facility_id = facility_id;
    }

    public String getJson_data() {
        return json_data;
    }

    public void setJson_data(String json_data) {
        this.json_data = json_data;
    }
}
