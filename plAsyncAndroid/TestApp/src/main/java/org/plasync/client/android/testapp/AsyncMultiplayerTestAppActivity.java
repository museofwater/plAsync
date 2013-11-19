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
import android.widget.SearchView;

import org.plasync.client.android.AsyncMultiplayerSession;
import org.plasync.client.android.AsyncMultiplayerSessionError;
import org.plasync.client.android.model.FriendRequest;
import org.plasync.client.android.model.User;
import org.plasync.client.android.testapp.friends.FriendRequestListener;
import org.plasync.client.android.testapp.friends.FriendsFragment;
import org.plasync.client.android.testapp.friends.UsersAdapter;
import org.plasync.client.android.testapp.friends.UsersAdapterBuilder;
import org.plasync.client.android.testapp.games.GameInviteListener;
import org.plasync.client.android.testapp.games.GamesFragment;

import java.util.ArrayList;
import java.util.List;

public class AsyncMultiplayerTestAppActivity extends FragmentActivity
                   implements AsyncMultiplayerSession.SessionInitListener,
                              SettingsFragment.OnSetServerUrlListener {

    private static final String TAG = AsyncMultiplayerTestAppActivity.class.getName();
    private static final int USER_GROUP_INDEX = 0;
//    private static final String URL = "http://192.168.1.67:9000";
//    private static final String URL = "http://192.168.8.72:9000";

    private AsyncMultiplayerSession session;

    private String serverUrl = null;
    private User user;

    private ViewPager fragmentPager;
    private FriendsFragment friendsFragment;
    private GamesFragment gamesFragment;
    private SettingsFragment settingssFragment;

    private List<Fragment> fragments = new ArrayList<Fragment>();

//    private Button btnFriends;
//    private Button btnGames;
//    private Button btnSettings;
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

        // Create fragments
        friendsFragment = new FriendsFragment();
        gamesFragment = new GamesFragment();
        settingssFragment = new SettingsFragment();

        fragments.add(friendsFragment);
        fragments.add(gamesFragment);
        fragments.add(settingssFragment);

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
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // When the tab is selected, switch to the
                // corresponding page in the ViewPager.
                fragmentPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
        };

        ActionBar.Tab tab = actionBar.newTab()
                .setText(R.string.friends_menu_button)
                .setTabListener(tabListener);
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText(R.string.games_menu_button)
                .setTabListener(tabListener);
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText(R.string.settings_menu_button)
                .setTabListener(tabListener);
        actionBar.addTab(tab);

        showSettingsFragment();

//        tvUsername = (TextView) findViewById(R.id.tvUsername);
//
//
//        btnSetUrl = (Button) findViewById(R.id.btnServerUrl);
//        btnSetUrl.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onSetUrl();
//            }
//        });
        
//        if (username == null || userId == null) {
//            setupAsyncSession();
//        }
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
//            tvUsername.setText(username);
            settingssFragment.setUsername(username);

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
        AsyncMultiplayerTestApp.setSession(this.session);
        searchView.setEnabled(true);
    }

    @Override
    public void onInitError(AsyncMultiplayerSessionError error) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        // Multiplayer disabled
    }

//    @Override
//    public void onSearchComplete(List<User> users) {
//        // Need to get the friend requests
//        getFriendRequests();
//
//    }
//
//    @Override
//    public void onSearchError(AsyncMultiplayerSessionError error) {
//
//    }
//
//    @Override
//    public void onGetFriendRequestsComplete(List<FriendRequest> friendRequests) {
//
//    }
//
//    @Override
//    public void onGetFriendRequestsError(AsyncMultiplayerSessionError error) {
//
//    }

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
        showFragment(0);
    }

    private void showGamesFragment() {
        showFragment(1);
    }

    private void showSettingsFragment() {
        showFragment(2);
    }

    private void showFragment(int fragmentIndex) {
        fragmentPager.setCurrentItem(fragmentIndex);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchFriends(query);
        }
    }

    private void searchFriends(String query) {
        // Get the users for the app from the session.  onSearchComplete will be called when done
        session.searchUsers(query,new TestAppSearchListener());
    }

    private void showSearchResults(List<User> users, List<FriendRequest> friendRequests) {
        // Create an adapter
        UsersAdapter adapter = getUsersAdapter(users,friendRequests);
        showFriends(adapter);
        friendsFragment.setAdapter(adapter);
        if (users.size() > 0) {
            friendsFragment.expandGroup(adapter.getUserGroupIndex());
        }
    }

    private void getFriendRequests(AsyncMultiplayerSession.GetFriendRequestsListener listener) {
        // Get the friend requests.  onGetFriendRequests will be called when done
        session.getFriendRequests(user,listener);
    }

    private void showFriends(List<FriendRequest> friendRequests) {
        // Create an adapter
        UsersAdapter adapter = getUsersAdapter(new ArrayList<User>(),friendRequests);
        showFriends(adapter);
    }

    private void showFriends(UsersAdapter adapter) {
        // Make sure the fragment is showing before setting the adapter so that it has been
        // initialized
        showFriendsFragment();
        friendsFragment.setAdapter(adapter);
    }


    private UsersAdapter getUsersAdapter(List<User> users, List<FriendRequest> friendRequests) {
        return UsersAdapterBuilder.createUsersAdapter(this, users, friendRequests,
                new FriendRequestListener() {
                    @Override
                    public void onAddFriend(User friend) {

                    }

                    @Override
                    public void onDeclineFriend(FriendRequest friendRequest) {

                    }

                    @Override
                    public void onAcceptFriend(FriendRequest friendRequest) {

                    }
                },
                new GameInviteListener() {
                    @Override
                    public void onGameInvite(User user) {

                    }
                }
        );
    }

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

    private class TestAppSearchListener implements AsyncMultiplayerSession.SearchListener {
        private List<User> users;
        private List<FriendRequest> friendRequests;

        @Override
        public void onSearchComplete(List<User> users) {
            this.users = users;
            // Get the friend requests
            getFriendRequests(new AsyncMultiplayerSession.GetFriendRequestsListener() {
                @Override
                public void onGetFriendRequestsComplete(List<FriendRequest> friendRequests) {
                    TestAppSearchListener.this.friendRequests = friendRequests;
                    showSearchResults(TestAppSearchListener.this.users,friendRequests);
                }

                @Override
                public void onGetFriendRequestsError(AsyncMultiplayerSessionError error) {

                }
            });
        }

        @Override
        public void onSearchError(AsyncMultiplayerSessionError error) {

        }
    }

    private class TestAppGetFriendRequestsListener
            implements AsyncMultiplayerSession.GetFriendRequestsListener {
        private UsersAdapterBuilder adapterBuilder;

        public TestAppGetFriendRequestsListener(UsersAdapterBuilder builder) {
            this.adapterBuilder = builder;
        }


        @Override
        public void onGetFriendRequestsComplete(List<FriendRequest> friendRequests) {

        }

        @Override
        public void onGetFriendRequestsError(AsyncMultiplayerSessionError error) {

        }
    }


}
