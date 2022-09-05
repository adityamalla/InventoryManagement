package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class InventoryObject implements Serializable {
    public String inv_id;
    public String rfidCode;
    public String productName;
    public String code;
    public String scanned;


    public InventoryObject(String rfidCode, String productName, String inv_id, String code, String scanned) {
        this.rfidCode = rfidCode;
        this.productName = productName;
        this.inv_id = inv_id;
        this.code = code;
        this.scanned = scanned;
    }

    public String getScanned() {
        return scanned;
    }

    public void setScanned(String scanned) {
        this.scanned = scanned;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInv_id() {
        return inv_id;
    }

    public void setInv_id(String inv_id) {
        this.inv_id = inv_id;
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