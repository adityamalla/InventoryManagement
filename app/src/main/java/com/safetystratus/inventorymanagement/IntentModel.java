package com.safetystratus.inventorymanagement;

import java.util.ArrayList;

public class IntentModel {
    public String site_id;
    public String user_id;
    public String token;
    public String md5;
    public String sso;
    public String empName;
    public String site_name;
    public String loggedinUsername;
    public String flag;
    public ArrayList<String> codelistfromIntent;
    public String selectedSearchValue;
    public String selectedFacilName;
    public String selectedFacil;
    public String selectedRoomName;
    public String selectedRoom;
    public String total_inventory;
    public String reconc_id;

    public IntentModel(String site_id, String user_id, String token,
                       String md5, String sso, String empName, String site_name,
                       String loggedinUsername, String flag, ArrayList<String> codelistfromIntent,
                       String selectedSearchValue, String selectedFacilName, String selectedFacil, String selectedRoomName, String selectedRoom,String total_inventory, String reconc_id) {
        this.site_id = site_id;
        this.user_id = user_id;
        this.token = token;
        this.md5 = md5;
        this.sso = sso;
        this.empName = empName;
        this.site_name = site_name;
        this.loggedinUsername = loggedinUsername;
        this.flag = flag;
        this.codelistfromIntent = codelistfromIntent;
        this.selectedSearchValue = selectedSearchValue;
        this.selectedFacilName = selectedFacilName;
        this.selectedFacil = selectedFacil;
        this.selectedRoomName = selectedRoomName;
        this.selectedRoom = selectedRoom;
        this.total_inventory = total_inventory;
        this.reconc_id = reconc_id;
    }

    public String getReconc_id() {
        return reconc_id;
    }

    public void setReconc_id(String reconc_id) {
        this.reconc_id = reconc_id;
    }

    public String getSelectedSearchValue() {
        return selectedSearchValue;
    }

    public void setSelectedSearchValue(String selectedSearchValue) {
        this.selectedSearchValue = selectedSearchValue;
    }

    public String getSelectedFacilName() {
        return selectedFacilName;
    }

    public void setSelectedFacilName(String selectedFacilName) {
        this.selectedFacilName = selectedFacilName;
    }

    public String getSelectedFacil() {
        return selectedFacil;
    }

    public void setSelectedFacil(String selectedFacil) {
        this.selectedFacil = selectedFacil;
    }

    public String getSelectedRoomName() {
        return selectedRoomName;
    }

    public void setSelectedRoomName(String selectedRoomName) {
        this.selectedRoomName = selectedRoomName;
    }

    public String getSelectedRoom() {
        return selectedRoom;
    }

    public void setSelectedRoom(String selectedRoom) {
        this.selectedRoom = selectedRoom;
    }

    public String getTotal_inventory() {
        return total_inventory;
    }

    public void setTotal_inventory(String total_inventory) {
        this.total_inventory = total_inventory;
    }

    public ArrayList<String> getCodelistfromIntent() {
        return codelistfromIntent;
    }

    public void setCodelistfromIntent(ArrayList<String> codelistfromIntent) {
        this.codelistfromIntent = codelistfromIntent;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSso() {
        return sso;
    }

    public void setSso(String sso) {
        this.sso = sso;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getSite_name() {
        return site_name;
    }

    public void setSite_name(String site_name) {
        this.site_name = site_name;
    }

    public String getLoggedinUsername() {
        return loggedinUsername;
    }

    public void setLoggedinUsername(String loggedinUsername) {
        this.loggedinUsername = loggedinUsername;
    }
}
