package org.plasync.client.android.testapp.fragments;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import org.plasync.client.android.testapp.R;
import org.plasync.client.android.testapp.search.FriendSearchActivity;

/**
 * Created by ericwood on 8/12/13.
 */
public class FriendsFragment extends Fragment {
    private SearchView svFriends;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.friends_fragment, container, false);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        svFriends = (SearchView) view.findViewById(R.id.svFriends);
        svFriends.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(getActivity().getPackageName(),
                                  FriendSearchActivity.class.getName())));
        return view;

    }
}
