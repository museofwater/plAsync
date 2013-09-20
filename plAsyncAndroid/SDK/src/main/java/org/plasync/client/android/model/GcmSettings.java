package org.plasync.client.android.model;

/**
 * Created by ericwood on 9/13/13.
 */
public class GcmSettings {
    private long id;
    private String serverUrl;
    private String appId;
    private String gcmId;
    private String receiveIntentName;

    /**
     * Used when creating a non-persistent instance
     */
    public GcmSettings(String serverUrl, String appId, String gcmId, String receiveIntentName) {
        this.serverUrl = serverUrl;
        this.appId = appId;
        this.gcmId = gcmId;
        this.receiveIntentName = receiveIntentName;
    }

    /**
     * Used when creating a user retrieved from the database
     * @param id
     */
    public GcmSettings(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getReceiveIntentName() {
        return receiveIntentName;
    }

    public void setReceiveIntentName(String receiveIntentName) {
        this.receiveIntentName = receiveIntentName;
    }
}
