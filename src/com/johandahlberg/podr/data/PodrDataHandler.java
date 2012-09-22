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
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

public class PodrDataHandler {
	private static final String LOG_TAG = ".data.PodrDataHandler";
	Context context;

	public PodrDataHandler(Context context) {
		this.context = context;
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
