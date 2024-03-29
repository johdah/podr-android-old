package com.johandahlberg.podr.ui;

import com.johandahlberg.podr.BuildConfig;
import com.johandahlberg.podr.R;
import com.johandahlberg.podr.data.Episode;
import com.johandahlberg.podr.data.PodrDataHandler;
import com.johandahlberg.podr.data.helpers.PodrEpisodeHelper;
import com.johandahlberg.podr.net.UpdateService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class EpisodeListActivity extends FragmentActivity
        implements EpisodeListFragment.Callbacks {
	private static final String LOG_TAG = ".ui.EpisodeListActivity";
    public static final String ARG_ITEM_ID = "subscription_id";
	private PodrDataHandler dataHandler;
	private PodrEpisodeHelper episodeHelper;

    private boolean mTwoPane;
	private int currentEpisode = -1;
	private int currentSubscription = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG)
			Log.d(LOG_TAG, "onCreate()");
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_list);

        getActionBar().setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		currentSubscription = intent.getIntExtra(this.ARG_ITEM_ID, -1);
		dataHandler = new PodrDataHandler(this);
		episodeHelper = new PodrEpisodeHelper(this);

		EpisodeListFragment frag = (EpisodeListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.episode_list);
		frag.update(currentSubscription);

        if (findViewById(R.id.episode_detail_container) != null) {
            mTwoPane = true;
            ((EpisodeListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.episode_list))
                    .setActivateOnItemClick(true);
            
            EpisodeDetailFragment fragment = new EpisodeDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.episode_detail_container, fragment)
                    .commit();
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_episodelist, menu);
		return true;
	}

    @Override
    public void onItemSelected(int id) {
    	currentEpisode = id;
        if (mTwoPane) {
        	EpisodeDetailFragment detailsFrag = (EpisodeDetailFragment)
                    getSupportFragmentManager().findFragmentById(R.id.episode_detail_container);
        	detailsFrag.update(id);
        } else {
            Intent detailIntent = new Intent(this, EpisodeDetailActivity.class);
            detailIntent.putExtra(EpisodeDetailFragment.ARG_ITEM_ID, id);
            detailIntent.putExtra(this.ARG_ITEM_ID, currentSubscription);
            startActivity(detailIntent);
        }
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
		case R.id.menu_refresh:
			intent = new Intent(this, UpdateService.class);
			startService(intent);
			return true;
		case R.id.menu_markallread:
			if(episodeHelper.updateAllEpisodeStatus(currentSubscription, Episode.STATUS_UNREAD, Episode.STATUS_READ)) {
				Toast.makeText(this.getApplicationContext(),
						getString(R.string.marked_all_read), Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.menu_markallunread:
			if(episodeHelper.updateAllEpisodeStatus(currentSubscription, Episode.STATUS_READ, Episode.STATUS_UNREAD)) {
				Toast.makeText(this.getApplicationContext(),
									getString(R.string.marked_all_unread), Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.menu_about:
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			return true;
		case R.id.menu_settings:
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
