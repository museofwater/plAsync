package org.plasync.client.android.message;

/**
 * Created by ericwood on 10/11/13.
 */
public class FriendRequestMessage {

    private final String requestorName;

    public FriendRequestMessage(String requestorName) {
        this.requestorName = requestorName;
    }

    public String getRequestorName() {
        return requestorName;
    }
}
