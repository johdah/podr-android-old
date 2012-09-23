package com.johandahlberg.podr.data;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.johandahlberg.podr.BuildConfig;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

public class PodrDataHandler {
	private static final String LOG_TAG = ".data.PodrDataHandler";
	Context context;

	public PodrDataHandler(Context context) {
		this.context = context;
	}

	public void addDownload(Download download) {
		if (BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Add download: " + download.getId());

		// Defines an object to contain the new values to insert
		ContentValues mNewValues = new ContentValues();

		if (download.getEpisodeId() != -1)
			mNewValues.put(PodrOpenHelper.DOWNLOAD_COL_EPISODEID,
					download.getEpisodeId());
		if (download.getFile() != null)
			mNewValues.put(PodrOpenHelper.DOWNLOAD_COL_FILE, download.getFile()
					.toString());
		if (download.getUrl() != null)
			mNewValues.put(PodrOpenHelper.DOWNLOAD_COL_URL, download.getUrl()
					.toString());

		// Uri mNewUri = context.getContentResolver().insert(
		context.getContentResolver().insert(
				PodrContentProvider.DOWNLOAD_CONTENT_URI, mNewValues // the
																		// values
																		// to
																		// insert
				);

		this.updateEpisodeStatus(download.getEpisodeId(),
				Episode.STATUS_DOWNLOADING);

		if (BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Added download: " + download.getId());
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

		// Uri mNewUri = context.getContentResolver().insert(
		context.getContentResolver().insert(
				PodrContentProvider.EPISODE_CONTENT_URI, // the user dictionary
															// content URI
				mNewValues // the values to insert
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

	public void addSubscription(Subscription subscription) {
		// Defines an object to contain the new values to insert
		ContentValues mNewValues = new ContentValues();

		/*
		 * Sets the values of each column and inserts the word. The arguments to
		 * the "put" method are "column name" and "value"
		 */
		mNewValues.put(PodrOpenHelper.SUBSCRIPTION_COL_LINK, subscription
				.getLink().toString());

		// Inserts the subscription and defines a new Uri object that
		// recieves the result of the insertion
		context.getContentResolver().insert(
				PodrContentProvider.SUBSCRIPTION_CONTENT_URI, mNewValues);
	}
	
	public boolean deleteDownload(int id) {
		String mSelectionClause = BaseColumns._ID + " = ?";
		String[] mSelectionArgs = { String.valueOf(id) };

		int deletedRows = context.getContentResolver().delete(
				PodrContentProvider.DOWNLOAD_CONTENT_URI, mSelectionClause,
				mSelectionArgs);
		return (deletedRows > 0);
	}

	public Download getDownloadByEpisodeId(int episodeId) {
		// A "projection" defines the columns that will be returned for each row
		String[] mProjection = { BaseColumns._ID,
				PodrOpenHelper.DOWNLOAD_COL_EPISODEID,
				PodrOpenHelper.DOWNLOAD_COL_FILE,
				PodrOpenHelper.DOWNLOAD_COL_URL };

		String mSelectionClause = PodrOpenHelper.DOWNLOAD_COL_EPISODEID
				+ " = ?";
		String[] mSelectionArgs = { String.valueOf(episodeId) };

		// Does a query against the table and returns a Cursor object
		Cursor mCursor = context.getContentResolver().query(
				PodrContentProvider.DOWNLOAD_CONTENT_URI, mProjection,
				mSelectionClause, mSelectionArgs, null);

		Download newDownload = null;

		if (mCursor.moveToFirst()) {
			newDownload = new Download(mCursor.getInt(0));
			newDownload.setEpisodeId(mCursor.getInt(1));
			try {
				String uri = mCursor.getString(2);
				if (uri != null) {
					newDownload.setFile(new URI(uri));
				}
			} catch (URISyntaxException e) {

			}
			try {
				newDownload.setUrl(new URL(mCursor.getString(3)));
			} catch (MalformedURLException e) {

			}
		}

		mCursor.close();

		return newDownload;
	}

	public Download getDownloadById(int id) {
		Download newDownload = new Download(id);

		// A "projection" defines the columns that will be returned for each row
		String[] mProjection = { BaseColumns._ID,
				PodrOpenHelper.DOWNLOAD_COL_EPISODEID,
				PodrOpenHelper.DOWNLOAD_COL_FILE,
				PodrOpenHelper.DOWNLOAD_COL_URL };

		String mSelectionClause = BaseColumns._ID + " = ?";
		String[] mSelectionArgs = { String.valueOf(id) };

		// Does a query against the table and returns a Cursor object
		Cursor mCursor = context.getContentResolver().query(
				PodrContentProvider.DOWNLOAD_CONTENT_URI, mProjection,
				mSelectionClause, mSelectionArgs, null);

		if (mCursor.moveToFirst()) {
			newDownload.setId(mCursor.getInt(0));
			newDownload.setEpisodeId(mCursor.getInt(1));
			try {
				String uri = mCursor.getString(2);
				if (uri != null) {
					newDownload.setFile(new URI(uri));
				}
			} catch (URISyntaxException e) {

			}
			try {
				newDownload.setUrl(new URL(mCursor.getString(3)));
			} catch (MalformedURLException e) {

			}
		}

		mCursor.close();

		return newDownload;
	}

	public List<Download> getDownloaded() {
		List<Download> downloads = new ArrayList<Download>();

		List<Episode> downloaded = this
				.getEpisodeByStatus(Episode.STATUS_DOWNLOADED);
		for (Episode episode : downloaded) {
			Download download = this.getDownloadByEpisodeId(episode.get_id());
			if (download != null)
				downloads.add(download);
			else
				downloads.add(new Download(-1, episode.get_id(), null));
		}

		return downloads;
	}

	public List<Download> getDownloading() {
		List<Download> downloads = new ArrayList<Download>();

		List<Episode> downloading = this
				.getEpisodeByStatus(Episode.STATUS_DOWNLOADING);
		for (Episode episode : downloading) {
			downloads.add(this.getDownloadByEpisodeId(episode.get_id()));
		}

		return downloads;
	}

	public List<Download> getDownloads() {
		List<Download> downloads = new ArrayList<Download>();

		// A "projection" defines the columns that will be returned for each row
		String[] mProjection = { BaseColumns._ID,
				PodrOpenHelper.DOWNLOAD_COL_EPISODEID,
				PodrOpenHelper.DOWNLOAD_COL_FILE,
				PodrOpenHelper.DOWNLOAD_COL_URL };

		// Does a query against the table and returns a Cursor object
		Cursor mCursor = context.getContentResolver().query(
				PodrContentProvider.DOWNLOAD_CONTENT_URI, mProjection, null,
				null, null);

		if (mCursor == null)
			return null;
		while (mCursor.moveToNext()) {
			Download newDownload = new Download(mCursor.getInt(0));
			newDownload.setEpisodeId(mCursor.getInt(1));
			try {
				String uri = mCursor.getString(2);
				if (uri != null) {
					newDownload.setFile(new URI(uri));
				}
			} catch (URISyntaxException e) {

			}
			try {
				newDownload.setUrl(new URL(mCursor.getString(3)));
			} catch (MalformedURLException e) {

			}

			downloads.add(newDownload);
		}

		mCursor.close();

		return downloads;
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

	public Subscription getSubscriptionById(int id) {
		Subscription newSubscription = new Subscription();

		// A "projection" defines the columns that will be returned for each row
		String[] mProjection = { BaseColumns._ID,
				PodrOpenHelper.SUBSCRIPTION_COL_TITLE,
				PodrOpenHelper.SUBSCRIPTION_COL_LINK,
				PodrOpenHelper.SUBSCRIPTION_COL_DESC,
				PodrOpenHelper.SUBSCRIPTION_COL_LANGUAGE,
				PodrOpenHelper.SUBSCRIPTION_COL_COPYRIGHT,
				PodrOpenHelper.SUBSCRIPTION_COL_LASTUPDATED,
				PodrOpenHelper.SUBSCRIPTION_COL_AUTODOWNLOAD,
				PodrOpenHelper.SUBSCRIPTION_COL_ITUNESAUTHOR,
				PodrOpenHelper.SUBSCRIPTION_COL_ITUNESIMAGE,
				PodrOpenHelper.SUBSCRIPTION_COL_ITUNESOWNEREMAIL,
				PodrOpenHelper.SUBSCRIPTION_COL_ITUNESOWNERNAME,
				PodrOpenHelper.SUBSCRIPTION_COL_ITUNESSUBTITLE,
				PodrOpenHelper.SUBSCRIPTION_COL_ITUNESSUMMARY };

		String mSelectionClause = BaseColumns._ID + " = ?";
		String[] mSelectionArgs = { String.valueOf(id) };

		// Does a query against the table and returns a Cursor object
		Cursor mCursor = context.getContentResolver().query(
				PodrContentProvider.SUBSCRIPTION_CONTENT_URI, mProjection,
				mSelectionClause, mSelectionArgs, null);

		if (mCursor == null)
			return null;
		if (mCursor.moveToFirst()) {
			try {
				newSubscription = new Subscription(
						new URL(mCursor.getString(2)));
				newSubscription.set_id(mCursor.getInt(0));
				newSubscription.setTitle(mCursor.getString(1));
				if (mCursor.getString(3) != null)
					newSubscription.setDesc(mCursor.getString(3));
				if (mCursor.getString(4) != null)
					newSubscription.setLanguage(mCursor.getString(4));
				if (mCursor.getString(5) != null)
					newSubscription.setCopyright(mCursor.getString(5));

				DateFormat dateParser = new SimpleDateFormat(
						"E, dd MMM yyyy HH:mm:ss Z");
				try {
					String pubdate = mCursor.getString(6);
					newSubscription.setLastUpdated(dateParser.parse(pubdate));
				} catch (ParseException e) {
					if (BuildConfig.DEBUG)
						Log.e(LOG_TAG, "ParseException on subscription: "
								+ newSubscription.get_id());
				}

				newSubscription.setAutoDownload(mCursor.getInt(7) == 1);
				newSubscription.setItunesAuthor(mCursor.getString(8));
				try {
					newSubscription
							.setItunesImage(new URL(mCursor.getString(9)));
				} catch (MalformedURLException e) {
					if (BuildConfig.DEBUG)
						Log.e(LOG_TAG,
								"MalformedURLException on subscription: "
										+ newSubscription.get_id());
				}
				newSubscription.setItunesOwnerEmail(mCursor.getString(10));
				newSubscription.setItunesOwnerName(mCursor.getString(11));
				newSubscription.setItunesSubtitle(mCursor.getString(12));
				newSubscription.setItunesSummary(mCursor.getString(13));

			} catch (MalformedURLException e1) {
			}
		}

		mCursor.close();

		return newSubscription;
	}

	public List<Subscription> getSubscriptions() {
		List<Subscription> subscriptions = new ArrayList<Subscription>();

		// A "projection" defines the columns that will be returned for each row
		String[] mProjection = { BaseColumns._ID,
				PodrOpenHelper.SUBSCRIPTION_COL_TITLE,
				PodrOpenHelper.SUBSCRIPTION_COL_LINK,
				PodrOpenHelper.SUBSCRIPTION_COL_DESC,
				PodrOpenHelper.SUBSCRIPTION_COL_LANGUAGE,
				PodrOpenHelper.SUBSCRIPTION_COL_COPYRIGHT,
				PodrOpenHelper.SUBSCRIPTION_COL_LASTUPDATED,
				PodrOpenHelper.SUBSCRIPTION_COL_AUTODOWNLOAD,
				PodrOpenHelper.SUBSCRIPTION_COL_ITUNESAUTHOR,
				PodrOpenHelper.SUBSCRIPTION_COL_ITUNESIMAGE,
				PodrOpenHelper.SUBSCRIPTION_COL_ITUNESOWNEREMAIL,
				PodrOpenHelper.SUBSCRIPTION_COL_ITUNESOWNERNAME,
				PodrOpenHelper.SUBSCRIPTION_COL_ITUNESSUBTITLE,
				PodrOpenHelper.SUBSCRIPTION_COL_ITUNESSUMMARY };

		// Does a query against the table and returns a Cursor object
		Cursor mCursor = context.getContentResolver().query(
				PodrContentProvider.SUBSCRIPTION_CONTENT_URI, mProjection,
				null, null, null);
		if (mCursor == null)
			return null;
		while (mCursor.moveToNext()) {
			Subscription newSubscription;
			try {
				newSubscription = new Subscription(
						new URL(mCursor.getString(2)));
				newSubscription.set_id(mCursor.getInt(0));
				newSubscription.setTitle(mCursor.getString(1));
				if (mCursor.getString(3) != null)
					newSubscription.setDesc(mCursor.getString(3));
				if (mCursor.getString(4) != null)
					newSubscription.setLanguage(mCursor.getString(4));
				if (mCursor.getString(5) != null)
					newSubscription.setCopyright(mCursor.getString(5));

				DateFormat dateParser = new SimpleDateFormat(
						"E, dd MMM yyyy HH:mm:ss Z");
				try {
					String pubdate = mCursor.getString(6);
					newSubscription.setLastUpdated(dateParser.parse(pubdate));
				} catch (ParseException e) {
					if (BuildConfig.DEBUG)
						Log.e(LOG_TAG, "ParseException on subscription: "
								+ newSubscription.get_id());
				}

				newSubscription.setAutoDownload(mCursor.getInt(7) == 1);
				newSubscription.setItunesAuthor(mCursor.getString(8));
				try {
					newSubscription
							.setItunesImage(new URL(mCursor.getString(9)));
				} catch (MalformedURLException e) {
					if (BuildConfig.DEBUG)
						Log.e(LOG_TAG,
								"MalformedURLException on subscription: "
										+ newSubscription.get_id());
				}
				newSubscription.setItunesOwnerEmail(mCursor.getString(10));
				newSubscription.setItunesOwnerName(mCursor.getString(11));
				newSubscription.setItunesSubtitle(mCursor.getString(12));
				newSubscription.setItunesSummary(mCursor.getString(13));

				subscriptions.add(newSubscription);
			} catch (MalformedURLException e1) {
			}
		}

		mCursor.close();

		return subscriptions;
	}

	public int getUnreadBySubscription(int subscriptionId) {
		String[] mProjection = { BaseColumns._ID };
		String mSelectionClause = PodrOpenHelper.EPISODE_COL_SUBSCRIPTIONID
				+ " = ? AND " + PodrOpenHelper.EPISODE_COL_STATUS + " = ?";
		String[] mSelectionArgs = new String[2];
		mSelectionArgs[0] = String.valueOf(subscriptionId);
		mSelectionArgs[1] = String.valueOf(Episode.STATUS_UNREAD);

		// Does a query against the table and returns a Cursor object
		Cursor mCursor = context.getContentResolver().query(
				PodrContentProvider.EPISODE_CONTENT_URI, mProjection,
				mSelectionClause, mSelectionArgs, null);
		if (mCursor == null)
			return 0;
		else
			return mCursor.getCount();
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

	public boolean isSubscriptionUnique(String link) {
		String[] mProjection = { BaseColumns._ID };

		String mSelectionClause = PodrOpenHelper.SUBSCRIPTION_COL_LINK + " = ?";
		String[] mSelectionArgs = { String.valueOf(link) };

		// Does a query against the table and returns a Cursor object
		Cursor mCursor = context.getContentResolver().query(
				PodrContentProvider.SUBSCRIPTION_CONTENT_URI, mProjection,
				mSelectionClause, mSelectionArgs, null);

		boolean count = (mCursor.getCount() > 0) ? false : true;
		mCursor.close();
		return count;
	}
	
	public void setAutodownload(int subscriptionId, boolean flag) {
		if (BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Update Autodownload flag on subscription: " + subscriptionId);

		String mSelectionClause = BaseColumns._ID + " = ?";
		String[] mSelectionArgs = { String.valueOf(subscriptionId) };

		ContentValues mUpdateValues = new ContentValues();
		int autodownloadFlag = (flag) ? 1 : 0;
		mUpdateValues.put(PodrOpenHelper.SUBSCRIPTION_COL_AUTODOWNLOAD, autodownloadFlag);

		int mRowsUpdated = context.getContentResolver().update(
				PodrContentProvider.SUBSCRIPTION_CONTENT_URI,
				mUpdateValues,
				mSelectionClause,
				mSelectionArgs
				);

		if (mRowsUpdated > 0 && BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Updated Autodownload flag on subscription: " + subscriptionId);
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
				PodrContentProvider.EPISODE_CONTENT_URI, // the user dictionary
				mUpdateValues, // the columns to update
				mSelectionClause, // the column to select on
				mSelectionArgs // the value to compare to
				);

		if (mRowsUpdated > 0 && BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Updated Episode Status for all subscription: "
					+ currentSubscription);

		return mRowsUpdated > 0 ? true : false;
	}

	public void updateDownload(Download download) {
		if (BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Update Download: " + download.getId());

		// Defines an object to contain the updated values
		ContentValues mUpdateValues = new ContentValues();

		// Defines selection criteria for the rows you want to update
		String mSelectionClause = BaseColumns._ID + " = ?";
		String[] mSelectionArgs = { String.valueOf(download.getId()) };

		/*
		 * Sets the updated value and updates the selected words.
		 */
		if (download.getEpisodeId() != -1)
			mUpdateValues.put(PodrOpenHelper.DOWNLOAD_COL_EPISODEID,
					download.getEpisodeId());
		if (download.getFile() != null)
			mUpdateValues.put(PodrOpenHelper.DOWNLOAD_COL_FILE, download
					.getFile().toString());
		if (download.getUrl() != null)
			mUpdateValues.put(PodrOpenHelper.DOWNLOAD_COL_URL, download
					.getUrl().toString());

		int mRowsUpdated = context.getContentResolver().update(
				PodrContentProvider.DOWNLOAD_CONTENT_URI, mUpdateValues, // the
																			// columns
																			// to
																			// update
				mSelectionClause, // the column to select on
				mSelectionArgs // the value to compare to
				);

		if (mRowsUpdated > 0 && BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Updated Download: " + download.getId());
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
				PodrContentProvider.EPISODE_CONTENT_URI, // the user dictionary
															// content URI
				mUpdateValues, // the columns to update
				mSelectionClause, // the column to select on
				mSelectionArgs // the value to compare to
				);

		if (mRowsUpdated > 0 && BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Updated Episode Status: " + episodeId);

		return mRowsUpdated > 0 ? true : false;
	}

	public void updateSubscription(Subscription subscription) {
		if (BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Update Subscription: " + subscription.getLink());

		// Defines an object to contain the updated values
		ContentValues mUpdateValues = new ContentValues();

		// Defines selection criteria for the rows you want to update
		String mSelectionClause = BaseColumns._ID + " = ?";
		String[] mSelectionArgs = { String.valueOf(subscription.get_id()) };

		/*
		 * Sets the updated value and updates the selected words.
		 */
		if (subscription.getTitle() != null)
			mUpdateValues.put(PodrOpenHelper.SUBSCRIPTION_COL_TITLE,
					subscription.getTitle());
		if (subscription.getLink() != null)
			mUpdateValues.put(PodrOpenHelper.SUBSCRIPTION_COL_LINK,
					subscription.getLink().toString());
		if (subscription.getLanguage() != null)
			mUpdateValues.put(PodrOpenHelper.SUBSCRIPTION_COL_LANGUAGE,
					subscription.getLanguage());
		if (subscription.getCopyright() != null)
			mUpdateValues.put(PodrOpenHelper.SUBSCRIPTION_COL_COPYRIGHT,
					subscription.getCopyright());
		if (subscription.getDesc() != null)
			mUpdateValues.put(PodrOpenHelper.SUBSCRIPTION_COL_DESC,
					subscription.getDesc());
		if (subscription.getLastUpdated() != null)
			mUpdateValues.put(PodrOpenHelper.SUBSCRIPTION_COL_LASTUPDATED,
					subscription.getLastUpdated().toString());
		if (subscription.getItunesSubtitle() != null)
			mUpdateValues.put(PodrOpenHelper.SUBSCRIPTION_COL_ITUNESSUBTITLE,
					subscription.getItunesSubtitle());
		if (subscription.getItunesAuthor() != null)
			mUpdateValues.put(PodrOpenHelper.SUBSCRIPTION_COL_ITUNESAUTHOR,
					subscription.getItunesAuthor());
		if (subscription.getItunesSummary() != null)
			mUpdateValues.put(PodrOpenHelper.SUBSCRIPTION_COL_ITUNESSUMMARY,
					subscription.getItunesSummary());
		if (subscription.getItunesOwnerName() != null)
			mUpdateValues.put(PodrOpenHelper.SUBSCRIPTION_COL_ITUNESOWNERNAME,
					subscription.getItunesOwnerName());
		if (subscription.getItunesOwnerEmail() != null)
			mUpdateValues.put(PodrOpenHelper.SUBSCRIPTION_COL_ITUNESOWNEREMAIL,
					subscription.getItunesOwnerEmail());
		if (subscription.getItunesImage() != null)
			mUpdateValues.put(PodrOpenHelper.SUBSCRIPTION_COL_ITUNESIMAGE,
					subscription.getItunesImage().toString());
		/*
		 * if(subscription.getItunesCategory() != null)
		 * mUpdateValues.put(SUBSCRIPTION_COL_ITUNESCATEGORY, "itunesCategory");
		 */

		int mRowsUpdated = context.getContentResolver().update(
				PodrContentProvider.SUBSCRIPTION_CONTENT_URI, mUpdateValues,
				mSelectionClause, mSelectionArgs);

		if (mRowsUpdated > 0 && BuildConfig.DEBUG)
			Log.d(LOG_TAG, "Updated Subscription: " + subscription.getLink());
	}
}
