package com.plasync.client.android.testapp;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import com.plasync.client.android.testapp.R;

public class TestMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_menu, menu);
        return true;
    }
    
}
