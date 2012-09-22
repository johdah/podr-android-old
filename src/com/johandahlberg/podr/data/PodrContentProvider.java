package com.johandahlberg.podr.data;

import com.johandahlberg.podr.BuildConfig;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

public class PodrContentProvider extends ContentProvider {
	private static final String LOG_TAG = ".data.PodrContentProvider";
	private PodrOpenHelper mDB;

	private static final String AUTHORITY = "com.johandahlberg.podr.provider.PodrContentProvider";
	private static final String SUBSCRIPTION_BASE_PATH = "subscription";
	private static final String EPISODE_BASE_PATH = "episode";
	private static final String DOWNLOAD_BASE_PATH = "download";

	private static final int SUBSCRIPTION = 100;
	private static final int SUBSCRIPTION_ID = 110;
	private static final int EPISODE = 120;
	private static final int EPISODE_ID = 130;
	private static final int DOWNLOAD = 140;
	private static final int DOWNLOAD_ID = 150;

	public static final Uri SUBSCRIPTION_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + SUBSCRIPTION_BASE_PATH);
	public static final Uri EPISODE_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + EPISODE_BASE_PATH);
	public static final Uri DOWNLOAD_CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + DOWNLOAD_BASE_PATH);

	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/mt-reader";
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/mt-reader";

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, SUBSCRIPTION_BASE_PATH, SUBSCRIPTION);
		sURIMatcher.addURI(AUTHORITY, SUBSCRIPTION_BASE_PATH + "/#",
				SUBSCRIPTION_ID);
		sURIMatcher.addURI(AUTHORITY, EPISODE_BASE_PATH, EPISODE);
		sURIMatcher.addURI(AUTHORITY, EPISODE_BASE_PATH + "/#", EPISODE_ID);
		sURIMatcher.addURI(AUTHORITY, DOWNLOAD_BASE_PATH, DOWNLOAD);
		sURIMatcher.addURI(AUTHORITY, DOWNLOAD_BASE_PATH + "/#", DOWNLOAD_ID);
	}

	@Override
	public boolean onCreate() {
		if (BuildConfig.DEBUG)
			Log.v(LOG_TAG, "onCreate()");
		mDB = new PodrOpenHelper(getContext());

		return true;
	}

	/** {@inheritDoc} */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = mDB.getWritableDatabase();
		int rowsAffected = 0;
		String id;

		switch (uriType) {
		case SUBSCRIPTION:
			rowsAffected = sqlDB.delete(PodrOpenHelper.SUBSCRIPTION_TABLE_NAME,
					selection, selectionArgs);
			break;
		case SUBSCRIPTION_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsAffected = sqlDB.delete(
						PodrOpenHelper.SUBSCRIPTION_TABLE_NAME, BaseColumns._ID
								+ "=" + id, null);
			} else {
				rowsAffected = sqlDB.delete(
						PodrOpenHelper.SUBSCRIPTION_TABLE_NAME, selection
								+ " and " + BaseColumns._ID + "=" + id,
						selectionArgs);
			}
			break;
		case EPISODE:
			rowsAffected = sqlDB.delete(PodrOpenHelper.EPISODE_TABLE_NAME,
					selection, selectionArgs);
			break;
		case EPISODE_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsAffected = sqlDB.delete(PodrOpenHelper.EPISODE_TABLE_NAME,
						BaseColumns._ID + "=" + id, null);
			} else {
				rowsAffected = sqlDB.delete(PodrOpenHelper.EPISODE_TABLE_NAME,
						selection + " and " + BaseColumns._ID + "=" + id,
						selectionArgs);
			}
			break;
		case DOWNLOAD:
			rowsAffected = sqlDB.delete(PodrOpenHelper.DOWNLOAD_TABLE_NAME,
					selection, selectionArgs);
			break;
		case DOWNLOAD_ID:
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsAffected = sqlDB.delete(PodrOpenHelper.DOWNLOAD_TABLE_NAME,
						BaseColumns._ID + "=" + id, null);
			} else {
				rowsAffected = sqlDB.delete(PodrOpenHelper.DOWNLOAD_TABLE_NAME,
						selection + " and " + BaseColumns._ID + "=" + id,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown or Invalid URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return rowsAffected;
	}

	/** {@inheritDoc} */
	@Override
	public String getType(Uri uri) {
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case SUBSCRIPTION:
			return CONTENT_TYPE;
		case SUBSCRIPTION_ID:
			return CONTENT_ITEM_TYPE;
		case EPISODE:
			return CONTENT_TYPE;
		case EPISODE_ID:
			return CONTENT_ITEM_TYPE;
		case DOWNLOAD:
			return CONTENT_TYPE;
		case DOWNLOAD_ID:
			return CONTENT_ITEM_TYPE;
		default:
			return null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);

		if (uriType != SUBSCRIPTION && uriType != EPISODE
				&& uriType != DOWNLOAD) {
			throw new IllegalArgumentException("Invalid URI for insert");
		}

		SQLiteDatabase sqlDB = mDB.getWritableDatabase();

		String table = "";
		if (uriType == SUBSCRIPTION)
			table = PodrOpenHelper.SUBSCRIPTION_TABLE_NAME;
		if (uriType == EPISODE)
			table = PodrOpenHelper.EPISODE_TABLE_NAME;
		if (uriType == DOWNLOAD)
			table = PodrOpenHelper.DOWNLOAD_TABLE_NAME;

		long newID = sqlDB.insertOrThrow(table, null, values);
		try {
			if (newID > 0) {
				Uri newUri = ContentUris.withAppendedId(uri, newID);
				getContext().getContentResolver().notifyChange(uri, null);
				return newUri;
			} else {
				throw new SQLException("Failed to insert row into " + uri);
			}

		} catch (SQLiteConstraintException e) {
			Log.i(LOG_TAG, "Ignoring constraint failure.");
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		int uriType = sURIMatcher.match(uri);

		if (uriType != SUBSCRIPTION && uriType != SUBSCRIPTION_ID
				&& uriType != EPISODE && uriType != EPISODE_ID
				&& uriType != DOWNLOAD && uriType != DOWNLOAD_ID) {
			throw new IllegalArgumentException("Invalid URI for query");
		}

		if (uriType == SUBSCRIPTION || uriType == SUBSCRIPTION_ID)
			queryBuilder.setTables(PodrOpenHelper.SUBSCRIPTION_TABLE_NAME);
		if (uriType == EPISODE || uriType == EPISODE_ID)
			queryBuilder.setTables(PodrOpenHelper.EPISODE_TABLE_NAME);
		if (uriType == DOWNLOAD || uriType == DOWNLOAD_ID)
			queryBuilder.setTables(PodrOpenHelper.DOWNLOAD_TABLE_NAME);

		switch (uriType) {
		case SUBSCRIPTION_ID:
			queryBuilder.appendWhere(BaseColumns._ID + "="
					+ uri.getLastPathSegment());
			break;
		case SUBSCRIPTION:
			// no filter
			break;
		case EPISODE_ID:
			queryBuilder.appendWhere(BaseColumns._ID + "="
					+ uri.getLastPathSegment());
			break;
		case EPISODE:
			// no filter
			break;
		case DOWNLOAD_ID:
			queryBuilder.appendWhere(BaseColumns._ID + "="
					+ uri.getLastPathSegment());
			break;
		case DOWNLOAD:
			// no filter
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}

		Cursor cursor = queryBuilder.query(mDB.getReadableDatabase(),
				projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	/** {@inheritDoc} */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = mDB.getWritableDatabase();

		int rowsAffected;
		String id;
		StringBuilder modSelection;

		switch (uriType) {
		case SUBSCRIPTION_ID:
			id = uri.getLastPathSegment();
			modSelection = new StringBuilder(BaseColumns._ID + "=" + id);

			if (!TextUtils.isDigitsOnly(selection)) {
				modSelection.append(" AND " + selection);
			}

			rowsAffected = sqlDB.update(PodrOpenHelper.SUBSCRIPTION_TABLE_NAME,
					values, selection, selectionArgs);
			break;
		case SUBSCRIPTION:
			rowsAffected = sqlDB.update(PodrOpenHelper.SUBSCRIPTION_TABLE_NAME,
					values, selection, selectionArgs);
			break;
		case EPISODE_ID:
			id = uri.getLastPathSegment();
			modSelection = new StringBuilder(BaseColumns._ID + "=" + id);

			if (!TextUtils.isDigitsOnly(selection)) {
				modSelection.append(" AND " + selection);
			}

			rowsAffected = sqlDB.update(PodrOpenHelper.EPISODE_TABLE_NAME,
					values, selection, selectionArgs);
			break;
		case EPISODE:
			rowsAffected = sqlDB.update(PodrOpenHelper.EPISODE_TABLE_NAME,
					values, selection, selectionArgs);
			break;
		case DOWNLOAD_ID:
			id = uri.getLastPathSegment();
			modSelection = new StringBuilder(BaseColumns._ID + "=" + id);

			if (!TextUtils.isDigitsOnly(selection)) {
				modSelection.append(" AND " + selection);
			}

			rowsAffected = sqlDB.update(PodrOpenHelper.DOWNLOAD_TABLE_NAME,
					values, selection, selectionArgs);
			break;
		case DOWNLOAD:
			rowsAffected = sqlDB.update(PodrOpenHelper.DOWNLOAD_TABLE_NAME,
					values, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI");
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return rowsAffected;
	}
}