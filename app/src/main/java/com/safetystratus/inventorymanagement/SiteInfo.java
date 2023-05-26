package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class SiteInfo implements Serializable {
    public String siteId;
    public String siteName;
    public String userId;
    public String roleId;
    public String sso_host;
    public String api_host;

    public SiteInfo(String siteId, String siteName,String userId, String roleId, String sso_host,String api_host) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.userId = userId;
        this.roleId = roleId;
        this.sso_host = sso_host;
        this.api_host = api_host;
    }

    public String getApi_host() {
        return api_host;
    }

    public void setApi_host(String api_host) {
        this.api_host = api_host;
    }

    public String getSso_host() {
        return sso_host;
    }

    public void setSso_host(String sso_host) {
        this.sso_host = sso_host;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }
    //to display object as a string in spinner
    @Override
    public String toString() {
        return siteName;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SiteInfo){
            SiteInfo c = (SiteInfo)obj;
            if(c.getSiteName().equals(siteName) && c.getSiteId()==siteId && c.getUserId() == userId) return true;
        }
        return false;
    }
}
