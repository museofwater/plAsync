package org.plasync.client.android.model;

/**
 * Created by ericwood on 9/5/13.
 */
public class FriendStatus {

    public enum RequestStatus {
        NONE, PENDING, ACCEPTED, DECLINED}

    // The id of the original request, which is needed to change the status
    private long requestId;

    public FriendStatus(long requestId) {
        this.requestId = requestId;
    }

    public long getRequestId() {
        return requestId;
    }
}
