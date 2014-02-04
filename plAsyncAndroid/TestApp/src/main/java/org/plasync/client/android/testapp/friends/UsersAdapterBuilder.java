package org.plasync.client.android.testapp.friends;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import org.plasync.client.android.model.FriendRequest;
import org.plasync.client.android.model.FriendRequestStatus;
import org.plasync.client.android.model.User;
import org.plasync.client.android.testapp.R;
import org.plasync.client.android.testapp.games.GameInviteListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ericwood on 10/26/13.
 */
public class UsersAdapterBuilder {


    public static UsersAdapter createUsersAdapter(FragmentActivity activity, List<User> users,
                                                  List<FriendRequest> requests,
                                                  FriendRequestListener friendRequestListener,
                                                  GameInviteListener gameInviteListener) {
        UsersAdapter adapter = new UsersAdapter(activity, activity.getSupportFragmentManager(),
                                                friendRequestListener, gameInviteListener);

        // Get a list of pending friend requests removing each requestor from the users list
        List<UsersAdapter.UserListItem> pendingRequestItems =
                new ArrayList<UsersAdapter.UserListItem>();

        // create a map of lists by request status
        Map<FriendRequestStatus,List<UsersAdapter.UserListItem>> mapGroups =
                getGroups(users, requests, FriendRequestStatus.PENDING,FriendRequestStatus.ACCEPTED,
                          FriendRequestStatus.DECLINED);

        adapter.setGroups(createGroup(activity.getString(R.string.users_group_name),
                                      convertUsersToListItems(users)),
                          createGroup(activity.getString(R.string.pending_requests_group_name),
                                      mapGroups.get(FriendRequestStatus.PENDING)),
                          createGroup(activity.getString(R.string.friends_group_name),
                                      mapGroups.get(FriendRequestStatus.ACCEPTED)),
                          createGroup(activity.getString(R.string.declined_requests_group_name),
                                      mapGroups.get(FriendRequestStatus.DECLINED)));
//        // Add the remaining users first
//        if (!users.isEmpty()) {
//            adapter.addGroup(activity.getString(R.string.users_group_name),
//                    convertUsersToListItems(users));
//        }
//
//        // Then add the pending friend requests
//        adapter.addGroup(activity.getString(R.string.pending_requests_group_name),
//                mapGroups.get(FriendRequestStatus.PENDING));
//
//        // Then add the pending friend requests
//        adapter.addGroup(activity.getString(R.string.friends_group_name),
//                mapGroups.get(FriendRequestStatus.ACCEPTED));
//
//        // Then add the pending friend requests
//        adapter.addGroup(activity.getString(R.string.declined_requests_group_name),
//                mapGroups.get(FriendRequestStatus.DECLINED));

        return adapter;
    }

    private static Map<FriendRequestStatus,List<UsersAdapter.UserListItem>> getGroups(List<User> users,
                                                                                      List<FriendRequest> requests,
                                                                                      FriendRequestStatus... statuses) {
        Map<FriendRequestStatus,List<UsersAdapter.UserListItem>> mapGroups =
                new HashMap<FriendRequestStatus, List<UsersAdapter.UserListItem>>();

        for (FriendRequestStatus status : statuses) {
            mapGroups.put(status,new ArrayList<UsersAdapter.UserListItem>());
        }

        Set<User> usersWithRequests = new HashSet<User>();

        // populate the groups from the requests
        for (FriendRequest request : requests) {
            User requestor = request.getRequestor();
            mapGroups.get(request.getRequestStatus()).add(
                    new UsersAdapter.UserListItem(requestor,request));
            usersWithRequests.add(requestor);
        }

//        // Now remove all users that have requests
//        Iterator<User> i = users.iterator();
//        while (i.hasNext()) {
//            User user = i.next();
//            if (usersWithRequests.contains(user)) {
//                i.remove();
//            }
//        }

        users.removeAll(usersWithRequests);
        return mapGroups;
    }

    private static UsersAdapter.UserListGroup createGroup(String groupTitle,
                                                          List<UsersAdapter.UserListItem> items) {
        return new UsersAdapter.UserListGroup(groupTitle,items);
    }

    private static List<UsersAdapter.UserListItem> convertUsersToListItems(List<User> users) {
        List<UsersAdapter.UserListItem> userItems = new ArrayList<UsersAdapter.UserListItem>();
        for (User user : users) {
            // Create an item for each user with no associated request
            userItems.add(new UsersAdapter.UserListItem(user,null));
        }
        return userItems;
    }
}
