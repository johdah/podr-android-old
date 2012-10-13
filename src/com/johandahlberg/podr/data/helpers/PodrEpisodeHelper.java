package com.johandahlberg.podr.data.helpers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

import com.johandahlberg.podr.BuildConfig;
import com.johandahlberg.podr.data.Episode;
import com.johandahlberg.podr.data.PodrContentProvider;
import com.johandahlberg.podr.data.PodrOpenHelper;

public class PodrEpisodeHelper {
	private static final String LOG_TAG = ".data.helpers.PodrEpisodeHelper";
	Context context;

	public PodrEpisodeHelper(Context context) {
		this.context = context;
	}
	public void addEpisode(Episode episode) {
		if (BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Add episode: " + episode.getTitle());

		// Defines an object to contain the new values to insert
		ContentValues mNewValues = new ContentValues();

		/*
		 * DateFormat dateParser = new SimpleDateFormat(
		 * "E, dd MMM yyyy HH:mm:ss Z");
		 */

		if (episode.getGuid() != null)
			mNewValues.put(PodrOpenHelper.EPISODE_COL_GUID, episode.getGuid());
		if (episode.getEnclosure() != null)
			mNewValues.put(PodrOpenHelper.EPISODE_COL_ENCLOSURE, episode
					.getEnclosure().toString());
		if (episode.getPubDate() != null)
			mNewValues.put(PodrOpenHelper.EPISODE_COL_PUBDATE, episode
					.getPubDate().getTime() / 1000);
		mNewValues.put(PodrOpenHelper.EPISODE_COL_STATUS, episode.getStatus());
		if (episode.getTitle() != null)
			mNewValues
					.put(PodrOpenHelper.EPISODE_COL_TITLE, episode.getTitle());
		if (episode.getSubscriptionId() != -1)
			mNewValues.put(PodrOpenHelper.EPISODE_COL_SUBSCRIPTIONID,
					episode.getSubscriptionId());
		if (episode.getItunesAuthor() != null)
			mNewValues.put(PodrOpenHelper.EPISODE_COL_ITUNESAUTHOR,
					episode.getItunesAuthor());
		if (episode.getItunesDuration() != null)
			mNewValues.put(PodrOpenHelper.EPISODE_COL_ITUNESDURATION,
					episode.getItunesDuration());
		if (episode.getItunesImage() != null)
			mNewValues.put(PodrOpenHelper.EPISODE_COL_ITUNESIMAGE, episode
					.getItunesImage().toString());
		// mNewValues.put(PodrOpenHelper.EPISODE_COL_ITUNESKEYWORDS,
		// episode.getItunesKeywords());
		if (episode.getItunesSubtitle() != null)
			mNewValues.put(PodrOpenHelper.EPISODE_COL_ITUNESSUBTITLE,
					episode.getItunesSubtitle());
		if (episode.getItunesSummary() != null)
			mNewValues.put(PodrOpenHelper.EPISODE_COL_ITUNESSUMMARY,
					episode.getItunesSummary());

		context.getContentResolver().insert(
				PodrContentProvider.EPISODE_CONTENT_URI,
				mNewValues
				);

		if (BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Added episode: " + episode.getTitle());
	}
	
	public void addEpisodes(List<Episode> episodes) {
		if (episodes.isEmpty())
			return;

		for (Episode episode : episodes) {
			addEpisode(episode);
		}
	}
	
	public List<Episode> getEpisodes() {
		List<Episode> episodes = new ArrayList<Episode>();

		// A "projection" defines the columns that will be returned for each row
		String[] mProjection = { BaseColumns._ID,
				PodrOpenHelper.EPISODE_COL_TITLE,
				PodrOpenHelper.EPISODE_COL_GUID,
				PodrOpenHelper.EPISODE_COL_ENCLOSURE,
				PodrOpenHelper.EPISODE_COL_PUBDATE,
				PodrOpenHelper.EPISODE_COL_STATUS,
				PodrOpenHelper.EPISODE_COL_SUBSCRIPTIONID,
				PodrOpenHelper.EPISODE_COL_ITUNESAUTHOR,
				PodrOpenHelper.EPISODE_COL_ITUNESDURATION, 
				PodrOpenHelper.EPISODE_COL_ITUNESIMAGE,
				PodrOpenHelper.EPISODE_COL_ITUNESSUBTITLE,
				PodrOpenHelper.EPISODE_COL_ITUNESSUMMARY };

		// Does a query against the table and returns a Cursor object
		Cursor mCursor = context.getContentResolver().query(
				PodrContentProvider.EPISODE_CONTENT_URI, mProjection, null,
				null, null);
		if (mCursor == null)
			return null;
		while (mCursor.moveToNext()) {
			Episode newEpisode = new Episode();
			newEpisode.set_id(mCursor.getInt(0));
			newEpisode.setTitle(mCursor.getString(1));
			newEpisode.setGuid(mCursor.getString(2));
			try {
				newEpisode.setEnclosure(new URL(mCursor.getString(3)));
			} catch (MalformedURLException e) {
				if (BuildConfig.DEBUG)
					Log.e(LOG_TAG, "MalformedURLException on episode: "
							+ newEpisode.get_id());
			}

			long date = mCursor.getInt(4);
			newEpisode.setPubDate(new Date(date * 1000));

			newEpisode.setStatus(mCursor.getInt(5));
			newEpisode.setSubscriptionId(mCursor.getInt(6));

			newEpisode.setItunesAuthor(mCursor.getString(7));
			newEpisode.setItunesDuration(mCursor.getString(8));
			try {
				newEpisode.setItunesImage(new URL(mCursor.getString(9)));
			} catch (MalformedURLException e) {
				if (BuildConfig.DEBUG)
					Log.e(LOG_TAG, "MalformedURLException on episode: "
							+ newEpisode.get_id());
			}
			newEpisode.setItunesSubtitle(mCursor.getString(10));
			newEpisode.setItunesSummary(mCursor.getString(11));
			episodes.add(newEpisode);
		}

		mCursor.close();

		return episodes;
	}

	public Episode getEpisodeByGuid(String guid) {
		Episode newEpisode = new Episode();

		// A "projection" defines the columns that will be returned for each row
		String[] mProjection = { BaseColumns._ID,
				PodrOpenHelper.EPISODE_COL_TITLE,
				PodrOpenHelper.EPISODE_COL_GUID,
				PodrOpenHelper.EPISODE_COL_ENCLOSURE,
				PodrOpenHelper.EPISODE_COL_PUBDATE,
				PodrOpenHelper.EPISODE_COL_STATUS,
				PodrOpenHelper.EPISODE_COL_SUBSCRIPTIONID,
				PodrOpenHelper.EPISODE_COL_ITUNESAUTHOR,
				PodrOpenHelper.EPISODE_COL_ITUNESDURATION, 
				PodrOpenHelper.EPISODE_COL_ITUNESIMAGE,
				PodrOpenHelper.EPISODE_COL_ITUNESSUBTITLE,
				PodrOpenHelper.EPISODE_COL_ITUNESSUMMARY };

		String mSelectionClause = PodrOpenHelper.EPISODE_COL_GUID + " = ?";
		String[] mSelectionArgs = { guid };

		// Does a query against the table and returns a Cursor object
		Cursor mCursor = context.getContentResolver().query(
				PodrContentProvider.EPISODE_CONTENT_URI, mProjection,
				mSelectionClause, mSelectionArgs, null);

		if (mCursor.moveToFirst()) {
			newEpisode.set_id(mCursor.getInt(0));
			newEpisode.setTitle(mCursor.getString(1));
			newEpisode.setGuid(mCursor.getString(2));
			try {
				newEpisode.setEnclosure(new URL(mCursor.getString(3)));
			} catch (MalformedURLException e) {
				if (BuildConfig.DEBUG)
					Log.e(LOG_TAG, "MalformedURLException on episode: "
							+ newEpisode.get_id());
			}

			long date = mCursor.getInt(4);
			newEpisode.setPubDate(new Date(date * 1000));

			newEpisode.setStatus(mCursor.getInt(5));
			newEpisode.setSubscriptionId(mCursor.getInt(6));

			newEpisode.setItunesAuthor(mCursor.getString(7));
			newEpisode.setItunesDuration(mCursor.getString(8));
			try {
				newEpisode.setItunesImage(new URL(mCursor.getString(9)));
			} catch (MalformedURLException e) {
				if (BuildConfig.DEBUG)
					Log.e(LOG_TAG, "MalformedURLException on episode: "
							+ newEpisode.get_id());
			}
			newEpisode.setItunesSubtitle(mCursor.getString(10));
			newEpisode.setItunesSummary(mCursor.getString(11));
		}

		mCursor.close();

		return newEpisode;
	}

	public Episode getEpisodeById(int id) {
		Episode newEpisode = new Episode();

		// A "projection" defines the columns that will be returned for each row
		String[] mProjection = { BaseColumns._ID,
				PodrOpenHelper.EPISODE_COL_TITLE,
				PodrOpenHelper.EPISODE_COL_GUID,
				PodrOpenHelper.EPISODE_COL_ENCLOSURE,
				PodrOpenHelper.EPISODE_COL_PUBDATE,
				PodrOpenHelper.EPISODE_COL_STATUS,
				PodrOpenHelper.EPISODE_COL_SUBSCRIPTIONID,
				PodrOpenHelper.EPISODE_COL_ITUNESAUTHOR,
				PodrOpenHelper.EPISODE_COL_ITUNESDURATION, 
				PodrOpenHelper.EPISODE_COL_ITUNESIMAGE,
				PodrOpenHelper.EPISODE_COL_ITUNESSUBTITLE,
				PodrOpenHelper.EPISODE_COL_ITUNESSUMMARY };

		String mSelectionClause = BaseColumns._ID + " = ?";
		String[] mSelectionArgs = { String.valueOf(id) };

		// Does a query against the table and returns a Cursor object
		Cursor mCursor = context.getContentResolver().query(
				PodrContentProvider.EPISODE_CONTENT_URI, mProjection,
				mSelectionClause, mSelectionArgs, null);

		if (mCursor.moveToFirst()) {
			newEpisode.set_id(mCursor.getInt(0));
			newEpisode.setTitle(mCursor.getString(1));
			newEpisode.setGuid(mCursor.getString(2));
			try {
				newEpisode.setEnclosure(new URL(mCursor.getString(3)));
			} catch (MalformedURLException e) {
				if (BuildConfig.DEBUG)
					Log.e(LOG_TAG, "MalformedURLException on episode: "
							+ newEpisode.get_id());
			}

			long date = mCursor.getInt(4);
			newEpisode.setPubDate(new Date(date * 1000));

			newEpisode.setStatus(mCursor.getInt(5));
			newEpisode.setSubscriptionId(mCursor.getInt(6));

			newEpisode.setItunesAuthor(mCursor.getString(7));
			newEpisode.setItunesDuration(mCursor.getString(8));
			try {
				newEpisode.setItunesImage(new URL(mCursor.getString(9)));
			} catch (MalformedURLException e) {
				if (BuildConfig.DEBUG)
					Log.e(LOG_TAG, "MalformedURLException on episode: "
							+ newEpisode.get_id());
			}
			newEpisode.setItunesSubtitle(mCursor.getString(10));
			newEpisode.setItunesSummary(mCursor.getString(11));
		}

		mCursor.close();

		return newEpisode;
	}

	public List<Episode> getEpisodeByStatus(int status) {
		List<Episode> episodes = new ArrayList<Episode>();

		// A "projection" defines the columns that will be returned for each row
		String[] mProjection = { BaseColumns._ID,
				PodrOpenHelper.EPISODE_COL_TITLE,
				PodrOpenHelper.EPISODE_COL_GUID,
				PodrOpenHelper.EPISODE_COL_ENCLOSURE,
				PodrOpenHelper.EPISODE_COL_PUBDATE,
				PodrOpenHelper.EPISODE_COL_STATUS,
				PodrOpenHelper.EPISODE_COL_SUBSCRIPTIONID,
				PodrOpenHelper.EPISODE_COL_ITUNESAUTHOR,
				PodrOpenHelper.EPISODE_COL_ITUNESDURATION, 
				PodrOpenHelper.EPISODE_COL_ITUNESIMAGE,
				PodrOpenHelper.EPISODE_COL_ITUNESSUBTITLE,
				PodrOpenHelper.EPISODE_COL_ITUNESSUMMARY };

		String mSelectionClause = PodrOpenHelper.EPISODE_COL_STATUS + " = ?";
		String[] mSelectionArgs = { String.valueOf(status) };

		// Does a query against the table and returns a Cursor object
		Cursor mCursor = context.getContentResolver().query(
				PodrContentProvider.EPISODE_CONTENT_URI, mProjection,
				mSelectionClause, mSelectionArgs, null);
		if (mCursor == null)
			return null;
		while (mCursor.moveToNext()) {
			Episode newEpisode = new Episode();
			newEpisode.set_id(mCursor.getInt(0));
			newEpisode.setTitle(mCursor.getString(1));
			newEpisode.setGuid(mCursor.getString(2));
			try {
				newEpisode.setEnclosure(new URL(mCursor.getString(3)));
			} catch (MalformedURLException e) {
				if (BuildConfig.DEBUG)
					Log.e(LOG_TAG, "MalformedURLException on episode: "
							+ newEpisode.get_id());
			}

			long date = mCursor.getInt(4);
			newEpisode.setPubDate(new Date(date * 1000));

			newEpisode.setStatus(mCursor.getInt(5));
			newEpisode.setSubscriptionId(mCursor.getInt(6));
			newEpisode.setItunesAuthor(mCursor.getString(7));
			newEpisode.setItunesDuration(mCursor.getString(8));
			try {
				newEpisode.setItunesImage(new URL(mCursor.getString(9)));
			} catch (MalformedURLException e) {
				if (BuildConfig.DEBUG)
					Log.e(LOG_TAG, "MalformedURLException on episode: "
							+ newEpisode.get_id());
			}
			newEpisode.setItunesSubtitle(mCursor.getString(10));
			newEpisode.setItunesSummary(mCursor.getString(11));
			episodes.add(newEpisode);
		}

		mCursor.close();

		return episodes;
	}

	public boolean isEpisodeUnique(String guid) {
		String[] mProjection = { BaseColumns._ID };

		String mSelectionClause = PodrOpenHelper.EPISODE_COL_GUID + " = ?";
		String[] mSelectionArgs = { String.valueOf(guid) };

		// Does a query against the table and returns a Cursor object
		Cursor mCursor = context.getContentResolver().query(
				PodrContentProvider.EPISODE_CONTENT_URI, mProjection,
				mSelectionClause, mSelectionArgs, null);

		boolean count = (mCursor.getCount() > 0) ? false : true;
		mCursor.close();
		return count;
	}

	public boolean updateAllEpisodeStatus(int currentSubscription,
			int fromStatus, int status) {
		if (BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Update Episode Status for all subscription: "
					+ currentSubscription);

		// Defines an object to contain the updated values
		ContentValues mUpdateValues = new ContentValues();
		String mSelectionClause;
		String[] mSelectionArgs = null;

		// Defines selection criteria for the rows you want to update
		if (fromStatus != -1) {
			mSelectionClause = PodrOpenHelper.EPISODE_COL_SUBSCRIPTIONID
					+ " = ? AND " + PodrOpenHelper.EPISODE_COL_STATUS + " = ?";
			mSelectionArgs = new String[2];
			mSelectionArgs[0] = String.valueOf(currentSubscription);
			mSelectionArgs[1] = String.valueOf(fromStatus);
		} else {
			mSelectionClause = PodrOpenHelper.EPISODE_COL_SUBSCRIPTIONID
					+ " = ?";
			mSelectionArgs = new String[1];
			mSelectionArgs[0] = String.valueOf(currentSubscription);
		}

		/*
		 * Sets the updated value and updates the selected words.
		 */
		mUpdateValues.put(PodrOpenHelper.EPISODE_COL_STATUS, status);

		int mRowsUpdated = context.getContentResolver().update(
				PodrContentProvider.EPISODE_CONTENT_URI,
				mUpdateValues,
				mSelectionClause,
				mSelectionArgs
				);

		if (mRowsUpdated > 0 && BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Updated Episode Status for all subscription: "
					+ currentSubscription);

		return mRowsUpdated > 0 ? true : false;
	}

	public boolean updateEpisodeStatus(int episodeId, int status) {
		if (BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Update Episode Status: " + episodeId);

		// Defines an object to contain the updated values
		ContentValues mUpdateValues = new ContentValues();

		// Defines selection criteria for the rows you want to update
		String mSelectionClause = BaseColumns._ID + " = ?";
		String[] mSelectionArgs = { String.valueOf(episodeId) };

		/*
		 * Sets the updated value and updates the selected words.
		 */
		mUpdateValues.put(PodrOpenHelper.EPISODE_COL_STATUS, status);

		int mRowsUpdated = context.getContentResolver().update(
				PodrContentProvider.EPISODE_CONTENT_URI,
				mUpdateValues,
				mSelectionClause,
				mSelectionArgs
				);

		if (mRowsUpdated > 0 && BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Updated Episode Status: " + episodeId);

		return mRowsUpdated > 0 ? true : false;
	}
}