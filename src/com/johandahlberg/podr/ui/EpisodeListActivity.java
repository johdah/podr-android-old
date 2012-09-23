package com.johandahlberg.podr.ui;

import com.johandahlberg.podr.BuildConfig;
import com.johandahlberg.podr.R;
import com.johandahlberg.podr.data.PodrDataHandler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

public class EpisodeListActivity extends FragmentActivity
        implements EpisodeListFragment.Callbacks {
	private static final String LOG_TAG = ".ui.EpisodeListActivity";
	private PodrDataHandler dataHandler;

    private boolean mTwoPane;
	//private int currentEpisode = -1;
	private int currentSubscription = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG)
			Log.d(LOG_TAG, "onCreate()");
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_list);

		Intent intent = getIntent();
		currentSubscription = intent.getIntExtra("subId", -1);
		dataHandler = new PodrDataHandler(this);

		EpisodeListFragment frag = (EpisodeListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.episode_list);
		frag.update(currentSubscription);

        if (findViewById(R.id.episode_detail_container) != null) {
            mTwoPane = true;
            ((EpisodeListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.episode_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(int id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putInt(EpisodeDetailFragment.ARG_ITEM_ID, id);
            EpisodeDetailFragment fragment = new EpisodeDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.episode_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, EpisodeDetailActivity.class);
            detailIntent.putExtra(EpisodeDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
