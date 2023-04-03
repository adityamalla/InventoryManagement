package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class BatchInsertRooms implements Serializable {
    public String room;
    public String area;
    public String img_src;
    public String type_id;
    public String id;
    public String status;
    public String notes;
    public String facil_id;

    public BatchInsertRooms(String room, String area, String img_src, String type_id, String id, String status, String notes, String facil_id) {
        this.room = room;
        this.area = area;
        this.img_src = img_src;
        this.type_id = type_id;
        this.id = id;
        this.status = status;
        this.notes = notes;
        this.facil_id = facil_id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getImg_src() {
        return img_src;
    }

    public void setImg_src(String img_src) {
        this.img_src = img_src;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getFacil_id() {
        return facil_id;
    }

    public void setFacil_id(String facil_id) {
        this.facil_id = facil_id;
    }
}
