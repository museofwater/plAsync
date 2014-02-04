package org.plasync.client.android.testapp;

import android.support.v4.app.Fragment;

import org.plasync.client.android.AsyncMultiplayerSession;
import org.plasync.client.android.AsyncMultiplayerSessionError;
import org.plasync.client.android.model.FriendRequest;
import org.plasync.client.android.model.User;
import org.plasync.client.android.testapp.friends.FriendsFragment;

import java.util.List;

/**
 * Created by ericwood on 1/17/14.
 */
public class SearchFragment extends Fragment implements AsyncMultiplayerSession.SearchListener,
        AsyncMultiplayerSession.GetFriendRequestsListener {
    private AsyncMultiplayerSession session;
    private List<User> users;
    private SearchResultsListener listener;

    public void setSession(AsyncMultiplayerSession session) {
        this.session = session;
    }

    public void searchUsers(String query, SearchResultsListener listener) {
        this.listener = listener;
        // Get the users for the app from the session.  onSearchComplete will be called when done
        session.searchUsers(query, this);
    }

    @Override
    public void onSearchComplete(List<User> users) {
        this.users = users;
        session.getFriendRequests(this);
    }

    @Override
    public void onSearchError(AsyncMultiplayerSessionError error) {
        // Inform the search results listener
        listener.onSearchError(error);
    }

    @Override
    public void onGetFriendRequestsComplete(List<FriendRequest> friendRequests) {
        // Inform the search results listener
        if (listener != null) {
            listener.onSearchComplete(users,friendRequests);
        }
    }

    @Override
    public void onGetFriendRequestsError(AsyncMultiplayerSessionError error) {
        // Inform the search results listener
        listener.onSearchError(error);
    }

    public interface SearchResultsListener {
        void onSearchComplete(List<User> users, List<FriendRequest> friendRequests);
        void onSearchError(AsyncMultiplayerSessionError error);
    }
}
