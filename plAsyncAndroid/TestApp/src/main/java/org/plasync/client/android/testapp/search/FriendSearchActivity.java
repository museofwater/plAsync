package org.plasync.client.android.testapp.search;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import org.plasync.client.android.AsyncMultiplayerSession;
import org.plasync.client.android.model.User;
import org.plasync.client.android.testapp.AsyncMultiplayerTestApp;

import java.util.List;

/**
 * Created by ericwood on 10/12/13.
 */
public class FriendSearchActivity extends ListActivity {

    private FriendsSearchAdapter adapter = new FriendsSearchAdapter();

    private ProgressDialog progressSearch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchFriends(query);
        }
    }

    private void searchFriends(String query) {
        AsyncMultiplayerSession session = AsyncMultiplayerTestApp.getSession();
        if (session != null) {
            List<User> results = session.searchFriends(query);
            adapter.setResults(results);
        }
    }


}
