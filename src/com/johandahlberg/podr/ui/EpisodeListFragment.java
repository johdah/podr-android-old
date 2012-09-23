package com.johandahlberg.podr.ui;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.johandahlberg.podr.BuildConfig;
import com.johandahlberg.podr.R;
import com.johandahlberg.podr.data.Download;
import com.johandahlberg.podr.data.Episode;
import com.johandahlberg.podr.data.PodrContentProvider;
import com.johandahlberg.podr.data.PodrDataHandler;
import com.johandahlberg.podr.data.PodrOpenHelper;
import com.johandahlberg.podr.dummy.DummyContent;
import com.johandahlberg.podr.ui.widget.SimpleSectionedListAdapter;
import com.johandahlberg.podr.utils.Utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class EpisodeListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String LOG_TAG = ".ui.EpisodeListFragment";
	private PodrDataHandler dataHandler;
	private static final int MARK_EPISODE_READ_ID = 0;
	private static final int MARK_EPISODE_UNREAD_ID = 1;
	private static final int MARK_EPISODE_DOWNLOADING = 2;
	private static final int REMOVE_EPISODE = 3;
	private static final int PLAY_EPISODE = 4;

	private SimpleSectionedListAdapter mAdapter;
	private EpisodeAdapter mEpisodeAdapter;

	// private int currentEpisode = -1;
	private int currentSubscription = -1;

	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	private Callbacks mCallbacks = sDummyCallbacks;
	private int mActivatedPosition = ListView.INVALID_POSITION;

	public interface Callbacks {

		public void onItemSelected(int i);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(int id) {
		}
	};

	public EpisodeListFragment() {
	}
	
	/** {@inheritDoc} */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG)
			Log.d(LOG_TAG, "onActivityCreated()");
		super.onActivityCreated(savedInstanceState);

		getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		registerForContextMenu(getListView());
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		activity.getContentResolver().registerContentObserver(
				PodrContentProvider.EPISODE_CONTENT_URI, true, mObserver);
		
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		dataHandler = new PodrDataHandler(getActivity());
		mEpisodeAdapter = new EpisodeAdapter(getActivity());
		mAdapter = new SimpleSectionedListAdapter(getActivity(),
				R.layout.episode_list_header, mEpisodeAdapter);
		setListAdapter(mAdapter);

		if (savedInstanceState == null) {
			// mScrollToNow = true;
		}	
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// only display the action appropiate for the items current state
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		Episode currentEpisode = dataHandler.getEpisodeById((int) info.id);

		if (currentEpisode.getStatus() == 0) {
			menu.add(0, MARK_EPISODE_READ_ID, 0, R.string.menu_markread);
		} else {
			menu.add(0, MARK_EPISODE_UNREAD_ID, 0, R.string.menu_markunread);
		}

		if (currentEpisode.getEnclosure() != null
				&& currentEpisode.getStatus() != Episode.STATUS_DOWNLOADING
				&& currentEpisode.getStatus() != Episode.STATUS_DOWNLOADED) {
			menu.add(0, MARK_EPISODE_DOWNLOADING, 0,
					R.string.menu_markdownloading);
		}

		if (currentEpisode.getStatus() == Episode.STATUS_DOWNLOADED) {
			menu.add(0, PLAY_EPISODE, 0, R.string.menu_play);
		}
		if (currentEpisode.getStatus() == Episode.STATUS_DOWNLOADING
				|| currentEpisode.getStatus() == Episode.STATUS_DOWNLOADED) {
			menu.add(0, REMOVE_EPISODE, 0, R.string.menu_remove);
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();

		switch (item.getItemId()) {
		case MARK_EPISODE_DOWNLOADING:
			Episode episode = dataHandler.getEpisodeById((int) info.id);
			dataHandler.addDownload(new Download(-1, episode.get_id(), episode
					.getEnclosure()));
			// TODO: Should not be static text
			Toast.makeText(getActivity().getApplicationContext(),
					"Marked for download", Toast.LENGTH_LONG).show();
			return true;
		case MARK_EPISODE_READ_ID: {
			dataHandler.updateEpisodeStatus((int) info.id, Episode.STATUS_READ);
			// TODO: Should not be static text
			Toast.makeText(getActivity().getApplicationContext(),
					"Marked episode as read", Toast.LENGTH_LONG).show();

			return true;
		}
		case MARK_EPISODE_UNREAD_ID: {
			dataHandler.updateEpisodeStatus((int) info.id,
					Episode.STATUS_UNREAD);
			// TODO: Should not be static text
			Toast.makeText(getActivity().getApplicationContext(),
					"Marked episode as unread", Toast.LENGTH_LONG).show();

			return true;
		}
		case REMOVE_EPISODE: {
			Download download = dataHandler
					.getDownloadByEpisodeId((int) info.id);
			if (download != null && download.getFile() != null) {
				File file = new File(download.getFile());
				if (file.exists())
					file.delete();
			}

			if (download != null)
				dataHandler.deleteDownload(download.getId());
			dataHandler.updateEpisodeStatus((int) info.id, Episode.STATUS_READ);
			return true;
		}
		case PLAY_EPISODE:
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			Download download = dataHandler
					.getDownloadByEpisodeId((int) info.id);
			Uri uri = Uri.fromFile(new File(download.getFile()));
			intent.setDataAndType(uri, "audio/mp3");

			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		}
		return super.onContextItemSelected(item);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.list_empty_container, container, false);
		inflater.inflate(R.layout.list_loading,
				(ViewGroup) root.findViewById(android.R.id.empty), true);
		ListView listView = (ListView) root.findViewById(android.R.id.list);
		listView.setItemsCanFocus(true);
		listView.setCacheColorHint(Color.WHITE);
		listView.setSelector(android.R.color.transparent);
		return root;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
		//getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);
		
		String projection[] = { BaseColumns._ID };
		Cursor episodeCursor = getActivity().getContentResolver().query(
				Uri.withAppendedPath(PodrContentProvider.EPISODE_CONTENT_URI,
						String.valueOf(id)), projection, null, null, null);
		
		if (episodeCursor.moveToFirst()) {
			mCallbacks.onItemSelected(episodeCursor.getInt(0));
		}

		episodeCursor.close();
		listView.setItemChecked(position, true);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	// LoaderManager.LoaderCallbacks<Cursor> methods

	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		String mSelectionClause = PodrOpenHelper.EPISODE_COL_SUBSCRIPTIONID
				+ " = ?";
		String[] mSelectionArgs = { String.valueOf(currentSubscription) };

		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				PodrContentProvider.EPISODE_CONTENT_URI,
				EpisodeQuery.PROJECTION, mSelectionClause, mSelectionArgs,
				PodrOpenHelper.EPISODE_COL_PUBDATE + " DESC");

		return cursorLoader;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getActivity() == null) {
			return;
		}

		int firstNowPosition = ListView.INVALID_POSITION;
		long prevDate = -1;
		long date;

		List<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			date = cursor.getLong(EpisodeQuery.EPISODE_DATE) * 1000;
			if (!Utils.isSameMonth(prevDate, date)) {
				SimpleDateFormat formatter = new SimpleDateFormat("MMMM y",
						Locale.getDefault());
				sections.add(new SimpleSectionedListAdapter.Section(cursor
						.getPosition(), formatter.format(new Date(date))));
			}
			if (firstNowPosition == ListView.INVALID_POSITION) {
				firstNowPosition = cursor.getPosition();
			}
			prevDate = date;

			cursor.moveToNext();
		}

		mEpisodeAdapter.changeCursor(cursor);

		SimpleSectionedListAdapter.Section[] dummy = new SimpleSectionedListAdapter.Section[sections
				.size()];
		mAdapter.setSections(sections.toArray(dummy));
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// adapter.swapCursor(null);
	}

	private final ContentObserver mObserver = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			if (getActivity() == null) {
				return;
			}

			Loader<Cursor> loader = getLoaderManager().getLoader(0);
			if (loader != null) {
				loader.forceLoad();
			}
		}
	};

	public void update(int subscriptionId) {
		currentSubscription = subscriptionId;

		getLoaderManager().initLoader(0, null, this);

		String title = dataHandler.getSubscriptionById(currentSubscription)
				.getTitle();
		getActivity().getActionBar().setTitle(title);
	}

	private class EpisodeAdapter extends CursorAdapter {

		public EpisodeAdapter(Context context, Cursor c) {
			super(context, c, false);
		}

		public EpisodeAdapter(Context context) {
			super(context, null, false);
		}

		@Override
		public void bindView(View view, Context context, final Cursor cursor) {
			final String title = cursor.getString(EpisodeQuery.EPISODE_TITLE);

			DateFormat formatter = android.text.format.DateFormat
					.getDateFormat(getActivity().getApplicationContext());
			long date = mCursor.getLong(EpisodeQuery.EPISODE_DATE);
			Date dateObj = new Date(date * 1000);

			final int status = cursor.getInt(EpisodeQuery.EPISODE_STATUS);

			final TextView titleView = (TextView) view
					.findViewById(R.id.episode_title);
			final TextView dateView = (TextView) view
					.findViewById(R.id.episode_date);
			final TextView statusView = (TextView) view
					.findViewById(R.id.episode_status);

			titleView.setText(title);

			if (dateObj != null) {
				dateView.setText(formatter.format(dateObj));
			} else {
				dateView.setText(getString(R.string.unknown_date));
			}

			switch (status) {
			case Episode.STATUS_READ:
				statusView.setText(getString(R.string.read));
				break;
			case Episode.STATUS_DOWNLOADING:
				statusView.setText(getString(R.string.downloading));
				break;
			case Episode.STATUS_DOWNLOADED:
				statusView.setText(getString(R.string.downloaded));
				break;
			case Episode.STATUS_UNREAD:
			default:
				statusView.setText(getString(R.string.unread));
				break;
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return getActivity().getLayoutInflater().inflate(
					R.layout.episode_list_item, parent, false);
		}
	}

	private interface EpisodeQuery {

		String[] PROJECTION = { BaseColumns._ID,
				PodrOpenHelper.EPISODE_COL_TITLE,
				PodrOpenHelper.EPISODE_COL_PUBDATE,
				PodrOpenHelper.EPISODE_COL_STATUS };

		int _ID = 0;
		int EPISODE_TITLE = 1;
		int EPISODE_DATE = 2;
		int EPISODE_STATUS = 3;
	}
}
