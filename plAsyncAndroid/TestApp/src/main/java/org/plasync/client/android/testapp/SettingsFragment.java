package org.plasync.client.android.testapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.prefs.Preferences;

/**
 * Created by ericwood on 10/11/13.
 */
public class SettingsFragment extends Fragment {
    private static final String DEFAULT_URL_PREF = "DEFAULT_URL";
    private static final String DEFAULT_URL_PREF_DEFAULT = "http://";
    private OnSetServerUrlListener listener;

    private EditText tvServerUrl;
    private TextView tvUsername;
    private Button btnSetUrl;

    private SharedPreferences prefs;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        tvUsername = (TextView) view.findViewById(R.id.tvCurrentUser);
        tvServerUrl = (EditText) view.findViewById(R.id.tvServerUrl);

        prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultUrl = prefs.getString(DEFAULT_URL_PREF, DEFAULT_URL_PREF_DEFAULT);
        tvServerUrl.setText(defaultUrl);

        btnSetUrl = (Button) view.findViewById(R.id.btnSetUrl);
        btnSetUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetServerUrl();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnSetServerUrlListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSetServerUrlListener");
        }
    }

    public void setUsername(String username) {
        tvUsername.setText(username);
    }

    private void onSetServerUrl() {
        String serverUrl = tvServerUrl.getText().toString();
        if (serverUrl != null && serverUrl.length() > 0) {
            prefs.edit().putString(DEFAULT_URL_PREF,serverUrl).commit();

            listener.onSetServerUrl(serverUrl);
        }
    }

//    public void refresh() {
//
//    }

    public interface OnSetServerUrlListener {

        void onSetServerUrl(String serverUrl);
    }
}
