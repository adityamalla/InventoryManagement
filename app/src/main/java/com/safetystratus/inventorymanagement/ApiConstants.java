package com.safetystratus.inventorymanagement;

public class ApiConstants {
    public static final String signInUrl = "https://services.labcliq.com/rest/19/mcms/login";
    public static final String downloadDbUrl = "/rest/19/mcms/downloadcms/all?site_id=%1$s&user_id=%2$s&token=%3$s";
    public static final String accessTokenUrl = "/rest/19/sync/check_access";
    public static final String downloadRoomInvDbUrl = "/rest/19/mcms/downloadcmsroomsinv/roomsandinv?site_id=%1$s&user_id=%2$s&token=%3$s&facil_id=%4$s";
    public static final String syncpostscanneddata = "/rest/19/mcms/upload/inventoryDetails";
    public static final String syncbarcodeScannedData = "/rest/19/mcms/update/inventoryDetails";
    public static final String syncbulkbarcodeScannedData = "/rest/19/mcms/bulkupdate/inventoryDetails";
}