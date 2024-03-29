package com.johandahlberg.podr.ui;

import com.johandahlberg.podr.R;
import com.johandahlberg.podr.data.Episode;
import com.johandahlberg.podr.net.UpdateService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.Toast;

public class AboutActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_about);
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