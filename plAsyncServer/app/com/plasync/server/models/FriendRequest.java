package com.plasync.server.models;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/2/13
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Object for clients to send requests.  The request is always represented as being from the original requestor.
 *
 * Clients should send instances of this as JSON
 */
public class FriendRequest {

    /**
     *  The id of the corresponding FriendAssociation.
     *
     *  This is the id to use for accept or decline

     */

    private Long requestId;

    private String appId;

    private User requestor;

    private User requested;

    private FriendRequestStatus requestStatus = FriendRequestStatus.PENDING;

    public FriendRequest() {
    }

    public FriendRequest(User requestor, User requested) {
        this.requestor = requestor;
        this.requested = requested;
    }

    public FriendRequest(long requestId, String appId, User requestor, User requested,
                         FriendRequestStatus requestStatus) {
        this.requestId = requestId;
        this.appId = appId;
        this.requestor = requestor;
        this.requested = requested;
        this.requestStatus = requestStatus;
    }

    public Long getRequestId() {
        return requestId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public User getRequestor() {
        return requestor;
    }

    public void setRequestor(User requestor) {
        this.requestor = requestor;
    }

    public User getRequested() {
        return requested;
    }

    public void setRequested(User requested) {
        this.requested = requested;
    }

    public FriendRequestStatus getRequestStatus() {
        return requestStatus;
    }
}
