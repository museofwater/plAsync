package org.plasync.client.android.testapp.friends;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.plasync.client.android.testapp.R;

/**
 * Created by ericwood on 8/12/13.
 */
public class FriendsFragment extends Fragment {
    private ExpandableListView elvFriends;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.friends_fragment, container, false);
        elvFriends = (ExpandableListView) view.findViewById(R.id.elvFriends);
        return view;

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
}
