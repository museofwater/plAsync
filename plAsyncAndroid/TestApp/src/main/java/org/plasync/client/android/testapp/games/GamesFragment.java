package org.plasync.client.android.testapp.games;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.plasync.client.android.testapp.R;

/**
 * Created by ericwood on 8/12/13.
 */
public class GamesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.games_fragment, container, false);
    }
}