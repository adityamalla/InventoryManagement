package com.safetystratus.inventorymanagement;

public interface RfidListeners {
    void onSuccess(Object object);

    void onFailure(Exception exception);

    void onFailure(String message);

}
