package com.safetystratus.inventorymanagement;

import java.io.Serializable;
import java.util.ArrayList;

public class BulkUpdateModel implements Serializable {
    public String codeList;
    public String status_id;
    public String room_id;
    public String object_id;
    public String primary_user_id;
    public String notes;
    public String comments;
    public String object_table;
    public String user_id;
    public String site_id;
    public String token;

    public BulkUpdateModel(String codeList, String status_id, String room_id, String object_id, String primary_user_id, String notes, String comments, String object_table, String user_id, String site_id, String token) {
        this.codeList = codeList;
        this.status_id = status_id;
        this.room_id = room_id;
        this.object_id = object_id;
        this.primary_user_id = primary_user_id;
        this.notes = notes;
        this.comments = comments;
        this.object_table = object_table;
        this.user_id = user_id;
        this.site_id = site_id;
        this.token = token;

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCodeList() {
        return codeList;
    }

    public void setCodeList(String codeList) {
        this.codeList = codeList;
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

    public String getObject_id() {
        return object_id;
    }

    public void setObject_id(String object_id) {
        this.object_id = object_id;
    }

    public String getPrimary_user_id() {
        return primary_user_id;
    }

    public void setPrimary_user_id(String primary_user_id) {
        this.primary_user_id = primary_user_id;
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

    public String getObject_table() {
        return object_table;
    }

    public void setObject_table(String object_table) {
        this.object_table = object_table;
    }
}
