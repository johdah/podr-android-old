package com.johandahlberg.podr.ui;

import java.text.DateFormat;

import com.johandahlberg.podr.R;
import com.johandahlberg.podr.R.id;
import com.johandahlberg.podr.R.layout;
import com.johandahlberg.podr.data.Episode;
import com.johandahlberg.podr.data.PodrDataHandler;
import com.johandahlberg.podr.data.helpers.PodrEpisodeHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EpisodeDetailFragment extends Fragment {
	private static final String LOG_TAG = ".ui.EpisodeDetailsFragment";

    public static final String ARG_ITEM_ID = "item_id";
    int currentEpisode = -1;
    Episode mItem;
    PodrDataHandler dataHandler;
    PodrEpisodeHelper episodeHelper = null;

    public EpisodeDetailFragment() {
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
		update(currentEpisode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	dataHandler = new PodrDataHandler(getActivity());
    	episodeHelper = new PodrEpisodeHelper(getActivity());
        Intent intent = getActivity().getIntent();
		currentEpisode = intent.getIntExtra(ARG_ITEM_ID, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_episode_detail, container, false);
    }
    
    public void update(int id) {
    	currentEpisode = id;
    	mItem = episodeHelper.getEpisodeById(currentEpisode);
    	
    	if (mItem != null && id > -1) {
    		getActivity().getActionBar().setTitle(mItem.getTitle());
            TextView tvTitle = ((TextView) getActivity().findViewById(R.id.episodedetails_title));
            tvTitle.setText(mItem.getTitle());
            
            TextView tvPubDate = (TextView) getActivity().findViewById(
                    R.id.episodedetails_pubDate);
		    // TODO: Something wrong with the date
		    DateFormat formatter = android.text.format.DateFormat
		    .getDateFormat(getActivity().getApplicationContext());
		    tvPubDate.setText(formatter.format(mItem.getPubDate()));

            TextView tvStatus = (TextView) getActivity().findViewById(
                            R.id.episodedetails_status);
            switch (mItem.getStatus()) {
            case Episode.STATUS_UNREAD:
                    tvStatus.setText(getString(R.string.unread));
                    break;
            case Episode.STATUS_READ:
                    tvStatus.setText(getString(R.string.read));
                    break;
            case Episode.STATUS_DOWNLOADED:
                    tvStatus.setText(getString(R.string.downloaded));
                    break;
            }

            TextView tvSummary = (TextView) getActivity().findViewById(
                            R.id.episodedetails_summary);
            tvSummary.setText(mItem.getItunesSummary().toString());
        } else if(id == -1) {
        	((TextView) getActivity().findViewById(R.id.episodedetails_title)).setText("Ingen podd är vald!");
        }
    }
}