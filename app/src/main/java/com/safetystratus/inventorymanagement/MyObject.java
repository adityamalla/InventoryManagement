package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class MyObject implements Serializable {
    public String objectName;
    public String objectId;

    public MyObject(String objectName, String objectId) {
        this.objectName = objectName;
        this.objectId = objectId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
