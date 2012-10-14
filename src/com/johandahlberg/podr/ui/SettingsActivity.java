package com.johandahlberg.podr.ui;

import com.johandahlberg.podr.R;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.preferences);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
	        if (item.getItemId() == android.R.id.home) {
	        	intent = new Intent(this, MainActivity.class);
	            NavUtils.navigateUpTo(this, intent);
	            return true;
	        }
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}