package org.plasync.client.android.testapp.games;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.plasync.client.android.AsyncMultiplayerSession;
import org.plasync.client.android.testapp.DeferedDisplayFragmentSupport;
import org.plasync.client.android.testapp.R;
import org.plasync.client.android.testapp.RefreshListener;
import org.plasync.client.android.testapp.RefreshableFragment;

/**
 * Created by ericwood on 8/12/13.
 */
public class GamesFragment extends DeferedDisplayFragmentSupport implements
        RefreshableFragment
{
    private AsyncMultiplayerSession session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.games_fragment, container, false);
    }

    @Override
    public void setRefreshListener(RefreshListener listener) {

    }

    public void setSession(AsyncMultiplayerSession session) {
        this.session = session;
    }

    public void refresh() {

    }


}
