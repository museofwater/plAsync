package org.plasync.client.android.model;

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
     *  The id of the request.
     *
     *  This is the id to use for accept or decline
     */
    private Long requestId;

    private User requestor;

    private User requested;

    private FriendRequestStatus requestStatus;


    public FriendRequest(User requestor, User requested) {
        this.requestor = requestor;
        this.requested = requested;
    }

    public Long getRequestId() {
        return requestId;
    }

    public User getRequestor() {
        return requestor;
    }

    public User getRequested() {
        return requested;
    }

    public FriendRequestStatus getRequestStatus() {
        return requestStatus;
    }
}