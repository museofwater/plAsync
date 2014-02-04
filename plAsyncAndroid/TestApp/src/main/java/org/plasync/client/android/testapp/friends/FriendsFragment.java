package org.plasync.client.android.testapp.friends;


import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.plasync.client.android.AsyncMultiplayerSession;
import org.plasync.client.android.AsyncMultiplayerSessionError;
import org.plasync.client.android.model.FriendRequest;
import org.plasync.client.android.model.User;
import org.plasync.client.android.testapp.DeferedDisplayFragmentSupport;
import org.plasync.client.android.testapp.R;
import org.plasync.client.android.testapp.games.GameInviteListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericwood on 8/12/13.
 */
public class FriendsFragment extends DeferedDisplayFragmentSupport implements
        AsyncMultiplayerSession.GetFriendRequestsListener,
        AsyncMultiplayerSession.RespondToFriendRequestListener,
        AsyncMultiplayerSession.CreateFriendRequestListener,
        FriendRequestListener

{
    private ExpandableListView elvFriends;
    private AsyncMultiplayerSession session;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.friends_list_view, container, false);
        elvFriends = (ExpandableListView) view.findViewById(R.id.elvFriends);
        return view;
    }

    @Override
    public void onAddFriend(User friend) {
        createFriendRequest(friend);
    }

    @Override
    public void onDeclineFriend(FriendRequest friendRequest) {
        session.respondToFriendRequest(friendRequest, false, this);
    }

    @Override
    public void onAcceptFriend(FriendRequest friendRequest) {
        session.respondToFriendRequest(friendRequest, true, this);
    }

    @Override
    public void onGetFriendRequestsComplete(List<FriendRequest> friendRequests) {
        showUsers(friendRequests);
    }

    @Override
    public void onGetFriendRequestsError(AsyncMultiplayerSessionError error) {
        // TODO show error message
    }

    @Override
    public void onCreateRequestComplete() {
        // Do nothing
    }

    @Override
    public void onCreateRequestError(AsyncMultiplayerSessionError error) {
        // TODO show error message
    }

    @Override
    public void onRespondToFriendRequestComplete() {
        refresh();
    }

    @Override
    public void onRespondToFriendRequestError(AsyncMultiplayerSessionError error) {
        // TODO show error message
    }

    public void setAdapter(UsersAdapter adapter) {
        elvFriends.setAdapter(adapter);
        setDefaultExpansion(adapter);
    }

    public void expandGroup(int groupIndex) {
        elvFriends.expandGroup(groupIndex);
    }

    private void setDefaultExpansion(UsersAdapter adapter) {
        // Expand pending friends by default
        Integer groupIndex = adapter.getPendingGroupIndex();
        if (groupIndex != null) {
             expandGroup(adapter.getPendingGroupIndex());
        }

        // Expand accepted friends by default
        groupIndex = adapter.getAcceptedGroupIndex();
        if (groupIndex != null) {
            expandGroup(adapter.getAcceptedGroupIndex());
        }
    }

    public void setSession(AsyncMultiplayerSession session) {
        this.session = session;
    }

    public void refresh() {
        if (session != null) {
            session.getFriendRequests(this);
        }
    }

    public void showUsers(List<User> users, List<FriendRequest> friendRequests) {
        // This request may need to be deferred, so create a command for it
        requestDisplay(new ShowUsersCommand(users, friendRequests));
    }

    public void showUsers(List<FriendRequest> friendRequests) {
        // This request may need to be deferred, so create a command for it
        requestDisplay(new ShowUsersCommand(friendRequests));
    }

    private UsersAdapter getUsersAdapter(List<User> users, List<FriendRequest> friendRequests) {
        return UsersAdapterBuilder.createUsersAdapter(getActivity(), users, friendRequests,
                this,
                new GameInviteListener() {
                    @Override
                    public void onGameInvite(User user) {

                    }
                }
        );
    }

    private void createFriendRequest(User friend) {
        FriendRequest friendRequest = new FriendRequest(session.getUser(),friend);
        session.createFriendRequest(friendRequest, this);
    }

    private class ShowUsersCommand implements DisplayCommand {
        private List<User> users;
        private List <FriendRequest> friendRequests;

        public ShowUsersCommand(List<FriendRequest> friendRequests) {
            this.users = new ArrayList<User>();
            this.friendRequests = friendRequests;
        }

        public ShowUsersCommand(List<User> users, List<FriendRequest> friendRequests) {
            this.users = users;
            this.friendRequests = friendRequests;
        }

        @Override
        public void execute() {
            UsersAdapter adapter = getUsersAdapter(users, friendRequests);
            setAdapter(adapter);
            if (users.size() > 0) {
                expandGroup(adapter.getUserGroupIndex());
            }
        }
    }
}
