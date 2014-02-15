package org.plasync.client.android.testapp;

/**
 * Created by ericwood on 2/12/14.
 */
public interface RefreshableFragment {
    void refresh();
    void setRefreshListener(RefreshListener listener);
}
