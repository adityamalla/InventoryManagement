package com.safetystratus.inventorymanagement;

import java.io.Serializable;

public class SiteInfo implements Serializable {
    public String siteId;
    public String siteName;
    public String userId;

    public SiteInfo(String siteId, String siteName,String userId) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.userId = userId;
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
