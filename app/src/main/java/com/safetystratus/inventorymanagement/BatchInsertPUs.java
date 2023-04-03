package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class BatchInsertPUs implements Serializable {
    public String primary_user;
    public String primary_user_id;

    public BatchInsertPUs(String primary_user, String primary_user_id) {
        this.primary_user = primary_user;
        this.primary_user_id = primary_user_id;
    }

    public String getPrimary_user() {
        return primary_user;
    }

    public void setPrimary_user(String primary_user) {
        this.primary_user = primary_user;
    }

    public String getPrimary_user_id() {
        return primary_user_id;
    }

    public void setPrimary_user_id(String primary_user_id) {
        this.primary_user_id = primary_user_id;
    }
}
