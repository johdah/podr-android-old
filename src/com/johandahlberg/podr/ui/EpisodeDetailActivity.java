package com.johandahlberg.podr.ui;

import java.io.File;

import com.johandahlberg.podr.R;
import com.johandahlberg.podr.R.id;
import com.johandahlberg.podr.R.layout;
import com.johandahlberg.podr.data.Download;
import com.johandahlberg.podr.data.PodrDataHandler;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class EpisodeDetailActivity extends FragmentActivity {
	private static final String LOG_TAG = ".ui.EpisodeDetailActivity";
	private PodrDataHandler dataHandler;
	private int currentEpisode = -1;
	private int currentSubscription = -1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_detail);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        dataHandler = new PodrDataHandler(getApplicationContext());
        currentEpisode = getIntent().getIntExtra(EpisodeDetailFragment.ARG_ITEM_ID, -1);
        currentSubscription = getIntent().getIntExtra(EpisodeListActivity.ARG_ITEM_ID, currentSubscription);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(EpisodeDetailFragment.ARG_ITEM_ID, currentEpisode);
            EpisodeDetailFragment fragment = new EpisodeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.episode_detail_container, fragment)
                    .commit();
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_episodedetails, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;

		switch (item.getItemId()) {
		case android.R.id.home:
			// app icon in action bar clicked; go home
	        if (item.getItemId() == android.R.id.home) {
	        	intent = new Intent(this, EpisodeListActivity.class);
	        	intent.putExtra(EpisodeListActivity.ARG_ITEM_ID, currentSubscription);
	            NavUtils.navigateUpTo(this, intent);
	            return true;
	        }
			return true;
		case R.id.menu_play:
			intent = new Intent(android.content.Intent.ACTION_VIEW);
			Download download = dataHandler.getDownloadByEpisodeId(currentEpisode);
			Uri uri = Uri.fromFile(new File(download.getFile()));
			intent.setDataAndType(uri, "audio/mp3");

			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		case R.id.menu_about:
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_settings:
			/*intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);*/
			Toast.makeText(this.getApplicationContext(),
					"Not yet implemented", Toast.LENGTH_LONG).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}