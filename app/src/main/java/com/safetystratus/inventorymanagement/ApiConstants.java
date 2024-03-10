package com.safetystratus.inventorymanagement;

public class ApiConstants {
    public static final String signInUrl = "https://services.labcliq.com/rest/20/mcms/login";
    public static final String downloadDbUrl = "/rest/20/mcms/downloadcms/all?site_id=%1$s&user_id=%2$s&token=%3$s";
    public static final String accessTokenUrl = "/rest/20/mcms/check_access";
    public static final String updateSSOAccessTokenDetails = "/rest/20/sync/update/ssoAccessTokenDetails";
    public static final String downloadRoomInvDbUrl = "/rest/20/mcms/downloadcmsroomsinv/roomsandinv?site_id=%1$s&user_id=%2$s&token=%3$s&facil_id=%4$s";
    public static final String syncpostscanneddata = "/rest/20/mcms/upload/inventoryDetails";
    public static final String syncbarcodeScannedData = "/rest/20/mcms/update/inventoryDetails";
    public static final String syncbulkbarcodeScannedData = "/rest/20/mcms/bulkupdate/inventoryDetails";
}