package org.plasync.client.android.testapp.friends;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.plasync.client.android.model.FriendRequest;
import org.plasync.client.android.model.FriendRequestStatus;
import org.plasync.client.android.model.User;
import org.plasync.client.android.testapp.R;
import org.plasync.client.android.testapp.games.GameInviteListener;
import org.plasync.client.android.util.image.ImageCache;
import org.plasync.client.android.util.image.ImageFetcher;
import org.plasync.client.android.util.image.ui.RecyclingImageView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by ericwood on 10/12/13.
 */
public class UsersAdapter extends BaseExpandableListAdapter {

    /* Natural ordering of groups */
    private static final int USER_GROUP_ORDER = 0;
    private static final int PENDING_GROUP_ORDER = 1;
    private static final int ACCEPTED_GROUP_ORDER = 2;
    private static final int DECLINED_GROUP_ORDER = 3;

    private static final String IMAGE_CACHE_DIR = "avatars";

    private Context context;
    private FragmentManager fragmentManager;
    private LayoutInflater inflater;

    /** Sortable container for groups.  Sorted by the order */
//    private TreeMap<Integer,UserListGroup> mapGroups = new TreeMap<Integer, UserListGroup>();
    private List<UserListGroup> groups = new ArrayList<UserListGroup>();

    /* Actual index for each group */
    private Integer userGroupIndex = null;
    private Integer pendingGroupIndex = null;
    private Integer acceptedGroupIndex = null;
    private Integer declinedGroupIndex = null;

    private GameInviteListener gameInviteListener = null;
    private FriendRequestListener friendRequestListener = null;

    private boolean gameInvitesEnabled = false;
    private boolean friendRequestsEnabled = false;

    private ImageFetcher imageFetcher;

    public UsersAdapter(Context context, FragmentManager fragmentManager,
                        FriendRequestListener friendRequestListener,
                        GameInviteListener gameInviteListener) {
//        this.context = context;
//        this.fragmentManager = fragmentManager;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(context, IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        int avatarSize = context.getResources().getDimensionPixelSize(R.dimen.avatar_size);
        imageFetcher = new ImageFetcher(context, avatarSize);
        imageFetcher.setLoadingImage(R.drawable.default_avatar);
        imageFetcher.addImageCache(fragmentManager, cacheParams);

        if (gameInviteListener != null) {
            this.gameInviteListener = gameInviteListener;
            this.gameInvitesEnabled = true;
        }

        if (friendRequestListener != null) {
            this.friendRequestListener = friendRequestListener;
            this.friendRequestsEnabled = true;
        }
    }

    @Override
    public int getGroupCount() {
//        return mapGroups.size();
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((UserListGroup)getGroup(groupPosition)).items.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
//        if (groupPosition == userGroupIndex) {
//            return mapGroups.get(USER_GROUP_ORDER);
//        }
//        else if (groupPosition == userGroupIndex) {
//            return mapGroups.get(USER_GROUP_ORDER);
//        }
//        else if (groupPosition == userGroupIndex) {
//            return mapGroups.get(USER_GROUP_ORDER);
//        }
//        else if (groupPosition == userGroupIndex) {
//            return mapGroups.get(USER_GROUP_ORDER);
//        }
//        throw new IllegalArgumentException("invalid group position");
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 10000 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = groups.get(groupPosition).name;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.user_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final UserListItem child = groups.get(groupPosition).items.get(childPosition);
        final String username = child.user.getUsername();
        final String avatarUrl = AvatarUtils.getUrl(child.user.getGravatarEmailHash());

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.user_list_item, null);
        }

        TextView tvUsername = (TextView) convertView.findViewById(R.id.tvUsername);
        tvUsername.setText(username);

        Button btnFriendAction1 = (Button) convertView.findViewById(R.id.btnFriendAction1);
        Button btnFriendAction2 = (Button) convertView.findViewById(R.id.btnFriendAction2);
        setButtonActions(btnFriendAction1, btnFriendAction2, child);

        Button btnGameInvite = (Button) convertView.findViewById(R.id.btnGameInvite);
        if (gameInvitesEnabled) {
            btnGameInvite.setVisibility(View.VISIBLE);
            btnGameInvite.setOnClickListener(new SendGameInviteListener(child.user));
        }
        else {
            btnGameInvite.setVisibility(View.GONE);
        }

        RecyclingImageView ivAvatar = (RecyclingImageView) convertView.findViewById(R.id.ivAvatar);
        imageFetcher.loadImage(avatarUrl, ivAvatar);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void setGameInviteListener(GameInviteListener gameInviteListener) {
        if (gameInviteListener != null) {
            this.gameInviteListener = gameInviteListener;
            this.gameInvitesEnabled = true;
        }
        else {
            this.gameInvitesEnabled = false;
        }
    }

    public void setFriendRequestListener(FriendRequestListener friendRequestListener) {
        if (friendRequestListener != null) {
            this.friendRequestListener = friendRequestListener;
            this.friendRequestsEnabled = true;
        }
        else {
            this.friendRequestsEnabled = false;
        }
    }

    public Integer getUserGroupIndex() {
        return userGroupIndex;
    }

    public Integer getPendingGroupIndex() {
        return pendingGroupIndex;
    }

    public Integer getAcceptedGroupIndex() {
        return acceptedGroupIndex;
    }

    public Integer getDeclinedGroupIndex() {
        return declinedGroupIndex;
    }

    public void setGroups(UserListGroup groupUsers, UserListGroup groupPending,
                          UserListGroup groupAccepted, UserListGroup groupDeclined) {
        int index = 0;
        if (groupUsers != null && !groupUsers.items.isEmpty()) {
//            mapGroups.put(USER_GROUP_ORDER,groupUsers);
            groups.add(groupUsers);
            userGroupIndex = index;
            index++;
        }
        if (groupPending != null && !groupPending.items.isEmpty()) {
//            mapGroups.put(PENDING_GROUP_ORDER,groupPending);
            groups.add(groupPending);
            pendingGroupIndex = index;
            index++;
        }
        if (groupAccepted != null && !groupAccepted.items.isEmpty()) {
//            mapGroups.put(ACCEPTED_GROUP_ORDER,groupAccepted);
            groups.add(groupAccepted);
            acceptedGroupIndex = index;
            index++;
        }
        if (groupDeclined != null && !groupDeclined.items.isEmpty()) {
//            mapGroups.put(DECLINED_GROUP_ORDER,groupDeclined);
            groups.add(groupDeclined);
            declinedGroupIndex = index;
            index++;
        }
    }

    private void setButtonActions(Button btnFriendAction1, Button btnFriendAction2, UserListItem child) {
        if (!friendRequestsEnabled) {
            btnFriendAction1.setVisibility(View.GONE);
            btnFriendAction2.setVisibility(View.GONE);
        }
        FriendRequest request = child.friendRequest;
        if (request == null  || request.getRequestStatus() == FriendRequestStatus.NONE) {
            btnFriendAction1.setBackgroundResource(R.drawable.ic_friend_add);
            btnFriendAction1.setOnClickListener(new AddFriendListener(child.user));
            btnFriendAction2.setVisibility(View.GONE);
        }
        else if (request.getRequestStatus() == FriendRequestStatus.ACCEPTED) {
            btnFriendAction1.setBackgroundResource(R.drawable.ic_decline);
            btnFriendAction1.setOnClickListener(new DeclineFriendListener(child.friendRequest));
            btnFriendAction2.setVisibility(View.GONE);
        }
        else if (request.getRequestStatus() == FriendRequestStatus.DECLINED) {
            btnFriendAction1.setBackgroundResource(R.drawable.ic_accept);
            btnFriendAction1.setOnClickListener(new AcceptFriendListener(child.friendRequest));
            btnFriendAction2.setVisibility(View.GONE);
        }
        else {
            btnFriendAction1.setBackgroundResource(R.drawable.ic_accept);
            btnFriendAction1.setOnClickListener(new AcceptFriendListener(child.friendRequest));
            btnFriendAction2.setVisibility(View.VISIBLE);
            btnFriendAction2.setBackgroundResource(R.drawable.ic_decline);
            btnFriendAction2.setOnClickListener(new DeclineFriendListener(child.friendRequest));
        }
    }

    static class UserListItem {
        private final User user;
        private final FriendRequest friendRequest;


        UserListItem(User user, FriendRequest friendRequest) {
            this.user = user;
            this.friendRequest = friendRequest;
        }
    }

    static class UserListGroup {
        private final String name;
        private final List<UserListItem> items;

        UserListGroup(String groupName, List<UserListItem> groupItems) {
            this.name = groupName;
            this.items = groupItems;
        }
    }

    private class AddFriendListener implements View.OnClickListener {
        private final User user;

        public AddFriendListener(User user) {
            this.user = user;
        }

        @Override
        public void onClick(View v) {
            friendRequestListener.onAddFriend(user);
        }
    }

    private class DeclineFriendListener implements View.OnClickListener {
        private final FriendRequest friendRequest;

        public DeclineFriendListener(FriendRequest friendRequest) {
            this.friendRequest = friendRequest;
        }

        @Override
        public void onClick(View v) {
            friendRequestListener.onDeclineFriend(friendRequest);
        }
    }

    private class AcceptFriendListener implements View.OnClickListener {
        private final FriendRequest friendRequest;

        public AcceptFriendListener(FriendRequest friendRequest) {
            this.friendRequest = friendRequest;
        }

        @Override
        public void onClick(View v) {
            friendRequestListener.onAcceptFriend(friendRequest);
        }
    }

    private class SendGameInviteListener implements View.OnClickListener {
        private final User user;

        private SendGameInviteListener(User user) {
            this.user = user;
        }

        @Override
        public void onClick(View v) {
            gameInviteListener.onGameInvite(user);
        }
    }
}
