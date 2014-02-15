package org.plasync.client.android.testapp;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import org.plasync.client.android.AsyncMultiplayerSession;
import org.plasync.client.android.AsyncMultiplayerSessionError;
import org.plasync.client.android.model.FriendRequest;
import org.plasync.client.android.model.User;
import org.plasync.client.android.testapp.friends.FriendsFragment;
import org.plasync.client.android.testapp.games.GamesFragment;

import java.util.ArrayList;
import java.util.List;

public class AsyncMultiplayerTestAppActivity extends FragmentActivity
                   implements AsyncMultiplayerSession.SessionInitListener,
                              SettingsFragment.OnSetServerUrlListener {

    private static final String TAG = AsyncMultiplayerTestAppActivity.class.getName();
    public static final int FRIENDS_FRAGMENT_INDEX = 0;
    private static final int USER_GROUP_INDEX = FRIENDS_FRAGMENT_INDEX;
    public static final int GAMES_FRAGMENT_INDEX = 1;
    public static final int SETTINGS_FRAGMENT_INDEX = 2;
    public static final String FRAGMENT_NAME_PREFIX = "android:switcher:";
    public static final String FRAGMENT_NAME_DELIMITER = ":";
//    private static final String URL = "http://192.168.1.67:9000";
//    private static final String URL = "http://192.168.8.72:9000";

    private AsyncMultiplayerSession session;

    private String serverUrl = null;
    private User user;

    private ViewPager fragmentPager;
    private ActionBar.TabListener tabListener;
    private ProgressBar[] tabProgressBars;
    //private ActionBar.TabListener nullTabListener;

    // Other than the accessor and the PagerAdapter, access to this field should only be through
    // the accessor
    private FriendsFragment friendsFragment;
    // Other than the accessor and the PagerAdapter, access to this field should only be through
    // the accessor
    private GamesFragment gamesFragment;
    // Other than the accessor and the PagerAdapter, access to this field should only be through
    // the accessor
    private SettingsFragment settingsFragment;
    private SearchFragment searchFragment;

    // Keeps track of the fragment that needs to be refreshed
    private Integer refreshIndex = null;

    private List<Fragment> fragments = new ArrayList<Fragment>();

    private SearchView searchView;

    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_app_main);

        // Get fragments from saved state.  Note that if they are in the saved state, they won't
        // have the activity attached yet.
        friendsFragment = getFriendsFragment();
        gamesFragment = getGamesFragment();
        settingsFragment = getSettingsFragment();

        // Create fragments if needed
        if (friendsFragment == null) {
            friendsFragment = new FriendsFragment();
        }
        if (gamesFragment == null) {
            gamesFragment = new GamesFragment();
        }
        if (settingsFragment == null) {
            settingsFragment = new SettingsFragment();
        }

        // Add the fragments to the list
        fragments.add(friendsFragment);
        fragments.add(gamesFragment);
        fragments.add(settingsFragment);

        tabProgressBars = new ProgressBar[fragments.size()];

        addRefreshListener(friendsFragment, FRIENDS_FRAGMENT_INDEX);
        addRefreshListener(gamesFragment, GAMES_FRAGMENT_INDEX);
        searchFragment = new SearchFragment();

        // setup the ViewPager
        fragmentPager = (ViewPager) findViewById(R.id.fragmentPager);
        fragmentPager.setAdapter(new TestAppFragmentPagerAdapter(getSupportFragmentManager()));
        fragmentPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });

        // setup action bar for tabs
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        // Create a tab listener that is called when the user changes tabs.
        tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // When the tab is selected, switch to the
                // corresponding page in the ViewPager.
                int position = tab.getPosition();
                fragmentPager.setCurrentItem(position);
                // Request the fragment to refresh itself
                requestRefresh(position);
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                int position = tab.getPosition();
                // Request the fragment to refresh itself
                requestRefresh(position);
            }
        };

        addTab(actionBar, R.string.friends_tab, FRIENDS_FRAGMENT_INDEX);
        addTab(actionBar, R.string.games_tab, GAMES_FRAGMENT_INDEX);
        addTab(actionBar, R.string.settings_tab, SETTINGS_FRAGMENT_INDEX);

        showSettingsFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getResources().getInteger(R.integer.SETUP_ASYNC_MULTIPLAYER_SESSION_RESPONSE_OK_CODE)) {
            String userId = data.getStringExtra(getString(R.string.PLASYNC_USER_ID_SETTING));
            String username = data.getStringExtra(getString(R.string.PLASYNC_USERNAME_SETTING));
            getSettingsFragment().setUsername(username);

            AsyncMultiplayerTestAppSessionConfig config = new AsyncMultiplayerTestAppSessionConfig();
            user = new User(userId, username, "0");
            config.setUser(user);
            config.setServerUrl(serverUrl);

            session = new AsyncMultiplayerSession(this, config);
            progressDialog =
                    ProgressDialog.show(this, getString(R.string.INITIALIZING_TITLE),
                            getString(R.string.INITIALIZING_MSG));
            session.init(this);
        }
        else {
            // async mulitplayer disabled
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.testapp_actions, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(getPackageName(), AsyncMultiplayerTestAppActivity.class.getName())));
        // Initially disable search view
        searchView.setEnabled(false);
        return true;
    }

    @Override
    public void onSetServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        setupAsyncSession(serverUrl);
    }

    @Override
    public void onInitComplete() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        friendsFragment.setSession(session);
        gamesFragment.setSession(session);
        searchFragment.setSession(session);
        searchView.setEnabled(true);
    }

    @Override
    public void onInitError(AsyncMultiplayerSessionError error) {
        // TODO Handle errors
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        // Multiplayer disabled
    }

    private void addRefreshListener(RefreshableFragment fragment, int fragmentIndex) {
        final class ProgressUpdateRefreshListener implements RefreshListener {
            private int fragmentIndex;

            ProgressUpdateRefreshListener(int fragmentIndex) {
                this.fragmentIndex = fragmentIndex;
            }

            @Override
            public void refreshStarted() {
                ProgressBar tabProgress = tabProgressBars[fragmentIndex];
                if (tabProgress != null) {
                    tabProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void refreshComplete() {
                ProgressBar tabProgress = tabProgressBars[fragmentIndex];
                if (tabProgress != null) {
                    tabProgress.setVisibility(View.INVISIBLE);
                }
            }
        }
        fragment.setRefreshListener(new ProgressUpdateRefreshListener(fragmentIndex));
    }

    private ActionBar.Tab addTab(ActionBar actionBar, int resId, int position) {
        ActionBar.Tab tab = actionBar.newTab()
                .setCustomView(R.layout.tab_with_progress)
                .setTabListener(tabListener);
        View tabView = tab.getCustomView();

        // Set the text
        TextView tabText = (TextView) tabView.findViewById(R.id.tabTextView);
        tabText.setText(resId);

        // Get the progress bar and store it in the list for later usage
        ProgressBar tabProgress = (ProgressBar) tabView.findViewById(R.id.tabProgressBar);
        tabProgressBars[position] = tabProgress;

        actionBar.addTab(tab);
        return tab;
    }

    private FriendsFragment getFriendsFragment() {
        if (friendsFragment ==  null) { // Happens when saved fragment is restored
            // Get the fragment from the fragment manager
            friendsFragment = (FriendsFragment)getSupportFragmentManager().findFragmentByTag(
                    getFragmentName(FRIENDS_FRAGMENT_INDEX));
        }
        return friendsFragment;
    }

    private GamesFragment getGamesFragment() {
        if (gamesFragment ==  null) { // Happens when saved fragment is restored
            // Get the fragment from the fragment manager
            gamesFragment = (GamesFragment)getSupportFragmentManager().findFragmentByTag(
                    getFragmentName(GAMES_FRAGMENT_INDEX));
        }
        return gamesFragment;
    }

        private SettingsFragment getSettingsFragment() {
        if (settingsFragment ==  null) { // Happens when saved fragment is restored
            // Get the fragment from the fragment manager
            settingsFragment = (SettingsFragment)getSupportFragmentManager().findFragmentByTag(
                    getFragmentName(SETTINGS_FRAGMENT_INDEX));
        }
        return settingsFragment;
    }

    private String getFragmentName(int position) {
        return FRAGMENT_NAME_PREFIX + R.id.fragmentPager + FRAGMENT_NAME_DELIMITER + position;
    }

    private void setupAsyncSession(String url) {
        this.serverUrl = url;
        Intent setupIntent = new Intent();

        // Explicit intent
        ComponentName setupActivityComponent = new ComponentName("org.plasync.client.android.testapp",
                "org.plasync.client.android.AsyncMultiplayerSetupActivity");
        setupIntent.setComponent(setupActivityComponent);

        // Implicit intent
//        setupIntent.setAction(getString(R.string.SETUP_ASYNC_MULTIPLAYER_SESSION_ACTION));
//        setupIntent.addCategory("android.intent.category.DEFAULT");

        setupIntent.putExtra((getString(R.string.PLASYNC_SERVER_URL_SETTING)), serverUrl);
        startActivityForResult(setupIntent,
                getResources().getInteger(R.integer.SETUP_ASYNC_MULTIPLAYER_SESSION_REQUEST_CODE));
    }

    private void showFriendsFragment() {
        showFragment(FRIENDS_FRAGMENT_INDEX);
        friendsFragment.refresh();
    }

    private void showGamesFragment() {
        showFragment(GAMES_FRAGMENT_INDEX);
        gamesFragment.refresh();
    }

    private void showSettingsFragment() {
        showFragment(SETTINGS_FRAGMENT_INDEX);
    }

    private void showFragment(int fragmentIndex) {
        // suppress the tab listener when changing tabs programmatically
        // Create a null tab listener to use when tab events should be supressed
        final ActionBar.TabListener nullTabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };
        ActionBar.Tab targetTab = getActionBar().getTabAt(fragmentIndex);
        targetTab.setTabListener(nullTabListener);
        fragmentPager.setCurrentItem(fragmentIndex);
        // Restore the tab listener
        targetTab.setTabListener(tabListener);
    }

    private void requestRefresh(int position) {
        switch(position) {
            case FRIENDS_FRAGMENT_INDEX:
                friendsFragment.refresh();
                break;
            case GAMES_FRAGMENT_INDEX:
                gamesFragment.refresh();
                break;
        }
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())  && session != null) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchFragment.searchUsers(query, new SearchFragment.SearchResultsListener() {
                @Override
                public void onSearchComplete(List<User> users, List<FriendRequest> friendRequests) {
                    // Set the view to the friends fragment
                    // Showing fragment using this lower level method will prevent the
                    // Friends fragment from refreshing
                    showFragment(FRIENDS_FRAGMENT_INDEX);
                    friendsFragment.showUsers(users, friendRequests);
                }

                @Override
                public void onSearchError(AsyncMultiplayerSessionError error) {
                    // show error message
                }
            });
        }
    }

//    private void showSearchResults(List<User> users, List<FriendRequest> friendRequests) {
//        getFriendsFragment().showUsers(users, friendRequests);
//
//    }

    private void showErrorAlert(int titleResource, int messageResource) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AsyncMultiplayerTestAppActivity.this);
        // Add the buttons
        builder.setTitle(titleResource)
                .setMessage(messageResource)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        // Create the AlertDialog
        alertDialog = builder.create();
        alertDialog.show();
    }


    private class TestAppFragmentPagerAdapter extends FragmentPagerAdapter {


        public TestAppFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
