package com.safetystratus.inventorymanagement;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


public class SiteAuthResponse implements Parcelable {

    private String Message;
    @SerializedName("access_token")
    private String accessToken;
    private String name;
    @SerializedName("user_id")
    private Integer userId;
    @SerializedName("request_return")
    private String requestReturn;
    @SerializedName("site_id")
    private Integer siteId;
    private String username;
    @SerializedName("request_time")
    private String requestTime;

    /**
     *
     * @return
     *     The Message
     */
    public String getMessage() {
        return Message;
    }

    /**
     *
     * @param Message
     *     The Message
     */
    public void setMessage(String Message) {
        this.Message = Message;
    }

    /**
     *
     * @return
     *     The accessToken
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     *
     * @param accessToken
     *     The access_token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     *
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     *
     * @param userId
     *     The user_id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     *
     * @return
     *     The requestReturn
     */
    public String getRequestReturn() {
        return requestReturn;
    }

    /**
     *
     * @param requestReturn
     *     The request_return
     */
    public void setRequestReturn(String requestReturn) {
        this.requestReturn = requestReturn;
    }

    /**
     *
     * @return
     *     The siteId
     */
    public Integer getSiteId() {
        return siteId;
    }

    /**
     *
     * @param siteId
     *     The site_id
     */
    public void setSiteId(Integer siteId) {
        this.siteId = siteId;
    }

    /**
     *
     * @return
     *     The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     *     The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     *     The requestTime
     */
    public String getRequestTime() {
        return requestTime;
    }

    /**
     *
     * @param requestTime
     *     The request_time
     */
    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Message);
        dest.writeString(accessToken);
        dest.writeString(name);
        dest.writeInt(userId);
        dest.writeString(requestReturn);
        dest.writeInt(siteId);
        dest.writeString(username);
        dest.writeString(requestTime);
    }

    public static final Parcelable.Creator<SiteAuthResponse> CREATOR
            = new Parcelable.Creator<SiteAuthResponse>() {
        public SiteAuthResponse createFromParcel(Parcel in) {
            return new SiteAuthResponse(in);
        }

        public SiteAuthResponse[] newArray(int size) {
            return new SiteAuthResponse[size];
        }
    };

    private SiteAuthResponse(Parcel in) {
        this.Message = in.readString();
        this.accessToken = in.readString();
        this.name = in.readString();
        this.userId = in.readInt();
        this.requestReturn = in.readString();
        this.siteId = in.readInt();
        this.username = in.readString();
        this.requestTime = in.readString();
    }
}
