package org.plasync.client.android.testapp.search;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.plasync.client.android.model.User;

import java.util.List;

/**
 * Created by ericwood on 10/12/13.
 */
public class FriendsSearchAdapter extends BaseAdapter {

    private List<User> results;

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int i) {
        return results.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    public void setResults(List<User> results) {
        this.results = results;
    }
}
