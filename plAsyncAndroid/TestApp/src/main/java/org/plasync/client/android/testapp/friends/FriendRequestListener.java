package org.plasync.client.android.testapp.friends;

import org.plasync.client.android.model.FriendRequest;
import org.plasync.client.android.model.User;

/**
 * Created by ericwood on 10/24/13.
 */
public interface FriendRequestListener {
    void onAddFriend(User friend);

    void onDeclineFriend(FriendRequest friendRequest);

    void onAcceptFriend(FriendRequest friendRequest);
}
