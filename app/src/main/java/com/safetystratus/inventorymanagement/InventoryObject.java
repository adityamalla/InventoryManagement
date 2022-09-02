package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class InventoryObject implements Serializable {
    public String rfidCode;
    public String productName;

    public InventoryObject(String rfidCode, String productName) {
        this.rfidCode = rfidCode;
        this.productName = productName;
    }

    public String getRfidCode() {
        return rfidCode;
    }

    public void setRfidCode(String rfidCode) {
        this.rfidCode = rfidCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}