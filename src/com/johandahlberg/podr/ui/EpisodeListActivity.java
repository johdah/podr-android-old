package com.johandahlberg.podr.ui;

import com.johandahlberg.podr.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class EpisodeListActivity extends FragmentActivity
        implements EpisodeListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_list);

        if (findViewById(R.id.episode_detail_container) != null) {
            mTwoPane = true;
            ((EpisodeListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.episode_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(EpisodeDetailFragment.ARG_ITEM_ID, id);
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
