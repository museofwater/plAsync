package org.plasync.client.android.testapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import org.plasync.client.android.AsyncMultiplayerSession;
import org.plasync.client.android.AsyncMultiplayerSessionError;
import org.plasync.client.android.model.User;
import org.plasync.client.android.testapp.fragments.FriendsFragment;
import org.plasync.client.android.testapp.fragments.GamesFragment;
import org.plasync.client.android.testapp.fragments.SettingsFragment;
import org.plasync.client.android.testapp.search.FriendSearchActivity;

import java.util.ArrayList;
import java.util.List;

public class AsyncMultiplayerTestAppActivity extends FragmentActivity
        implements AsyncMultiplayerSession.AsyncMultiplayerSessionListener,
                   SettingsFragment.OnSetServerUrlListener {

    private static final String TAG = AsyncMultiplayerTestAppActivity.class.getName();
//    private static final String URL = "http://192.168.1.67:9000";
//    private static final String URL = "http://192.168.8.72:9000";

    private AsyncMultiplayerSession session;

    private String serverUrl = null;
    private String username = null;
    private String userId = null;

    private ViewPager fragmentPager;
    private FriendsFragment friendsFragment;
    private GamesFragment gamesFragment;
    private SettingsFragment settingssFragment;

    private List<Fragment> fragments = new ArrayList<Fragment>();

//    private Button btnFriends;
//    private Button btnGames;
//    private Button btnSettings;
    private SearchView searchView;

    private ProgressDialog progressInit;



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

        showSettings();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getResources().getInteger(R.integer.SETUP_ASYNC_MULTIPLAYER_SESSION_RESPONSE_OK_CODE)) {
            userId = data.getStringExtra(getString(R.string.PLASYNC_USER_ID_SETTING));
            username = data.getStringExtra(getString(R.string.PLASYNC_USERNAME_SETTING));
//            tvUsername.setText(username);
            settingssFragment.setUsername(username);

            AsyncMultiplayerTestAppSessionConfig config = new AsyncMultiplayerTestAppSessionConfig();
            config.setUser(new User(userId, username));
            config.setServerUrl(serverUrl);

            session = new AsyncMultiplayerSession(this, config, this);
            progressInit =
                    ProgressDialog.show(this, getString(R.string.INITIALIZING_TITLE),
                            getString(R.string.INITIALIZING_MSG));
            session.init();
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
                new ComponentName(getPackageName(), FriendSearchActivity.class.getName())));
        // Initially disable search view
        searchView.setEnabled(false);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.menuSetServerUrl:
//                setupAsyncSession();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

//    private void setupAsyncSession() {
//        getUrlFromUser();
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

//    private void getUrlFromUser() {
//        FragmentManager fm = getSupportFragmentManager();
//        ServerUrlDialogFragment serverUrlDialog = new ServerUrlDialogFragment();
//        serverUrlDialog.setListener(new ServerUrlDialogFragment.ServerUrlDialogListener() {
//            @Override
//            public void onFinishEditDialog(String inputText) {
//                setupAsyncSession(inputText);
//            }
//        });
//        serverUrlDialog.show(fm, "fragment_server_url");
//    }


    @Override
    public void onInitComplete() {
        if (progressInit.isShowing()) {
            progressInit.dismiss();
        }
        AsyncMultiplayerTestApp.setSession(this.session);
        searchView.setEnabled(true);
    }

    @Override
    public void onInitError(AsyncMultiplayerSessionError error) {
        if (progressInit.isShowing()) {
            progressInit.dismiss();
        }
        // Multiplayer disabled
    }

    private void showFriends() {
        showFragment(0);
    }

    private void showGames() {
        showFragment(1);
    }

    private void showSettings() {
        showFragment(2);
    }

    private void showFragment(int fragmentIndex) {
        fragmentPager.setCurrentItem(fragmentIndex);
    }

    @Override
    public void onSetServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
        setupAsyncSession(serverUrl);
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
