package com.johandahlberg.podr.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import com.johandahlberg.podr.BuildConfig;
import com.johandahlberg.podr.R;
import com.johandahlberg.podr.data.Download;
import com.johandahlberg.podr.data.Episode;
import com.johandahlberg.podr.data.PodrContentProvider;
import com.johandahlberg.podr.data.PodrDataHandler;
import com.johandahlberg.podr.data.PodrOpenHelper;
import com.johandahlberg.podr.data.Subscription;
import com.johandahlberg.podr.data.helpers.PodrEpisodeHelper;
//import com.johandahlberg.podr.ui.SettingsActivity;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class UpdateService extends Service {
	private static final String LOG_TAG = ".net.UpdateService";

	private NotificationManager mNotifyManager = null;
	private NotificationCompat.Builder mBuilder = null;
	private SharedPreferences sharedPref;
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	private Context context;
	private int addedEpisodes = 0;

	// Handler that receives messages from the thread
	private final class ServiceHandler extends Handler {
		private PodrDataHandler dataHandler;
		private PodrEpisodeHelper episodeHelper;

		public ServiceHandler(Looper looper) {
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			if (BuildConfig.DEBUG)
				Log.d(LOG_TAG, "handleMessage()");
			Subscription subscription;
			InputStream inputStream;
			PodcastParser parser;
			dataHandler = new PodrDataHandler(context);
			episodeHelper = new PodrEpisodeHelper(context);

			// TODO: Implement only on wifi check
			/*boolean updateOnlyWifi = sharedPref.getBoolean(
					"pref_key_update_only_wifi", true);*/
			int autodownloadLimit = Integer.parseInt(sharedPref.getString(
					"pref_key_autodownload_limit", "5"));

			try {
				// Do work
				String[] projection = { BaseColumns._ID,
						PodrOpenHelper.SUBSCRIPTION_COL_LINK };

				ContentResolver cr = getContentResolver();
				Cursor c = cr.query(
						PodrContentProvider.SUBSCRIPTION_CONTENT_URI,
						projection, null, null, null);

				// Loop through subscriptions
				c.moveToFirst();
				int count = 0;
				do {
					// Notification
					mBuilder.setProgress(c.getCount(), count, false);
					mBuilder.setContentText(getString(R.string.update_inprogress) + ": " + count + "/"
							+ c.getCount());
					mNotifyManager.notify(0, mBuilder.build());
					
					subscription = new Subscription(new URL(c.getString(1)));
					subscription.set_id(c.getInt(0));

					inputStream = downloadUrl(subscription.getLink());
					if (inputStream != null) {
						parser = new PodcastParser(context, subscription);
						try {
							parser.parse(inputStream);
						} catch (XmlPullParserException e) {
							Log.e(LOG_TAG, "XmlPullParserException: " + e.toString());
						} catch (IOException e) {
							Log.e(LOG_TAG, "IOException: " + e.toString());
						}

						dataHandler.updateSubscription(subscription);
						List<Episode> episodes = parser.getEpisodes();
						episodeHelper.addEpisodes(episodes);
						Subscription updatedSubscription = dataHandler.getSubscriptionById(subscription.get_id());
						if(updatedSubscription != null && updatedSubscription.isAutoDownload()) {
							int updatedEpisodeCount = 0;
							for (Episode episode : episodes) {
								updatedEpisodeCount++;
								if (updatedEpisodeCount > autodownloadLimit
										&& autodownloadLimit != -1) {
									break;
								}
								
								Episode updatedEpisode = episodeHelper
										.getEpisodeByGuid(episode.getGuid());
								episodeHelper.updateEpisodeStatus(updatedEpisode.get_id(), Episode.STATUS_DOWNLOADING);
								dataHandler.addDownload(new Download(-1, updatedEpisode.get_id(), updatedEpisode.getEnclosure()));
							}
						}

						addedEpisodes += parser.getEpisodes().size();
					}
					count++;
				} while (c.moveToNext());

				c.close();
			} catch (Exception e) {

			}
			
            mBuilder.setContentText(getString(R.string.update_completed)).setProgress(0,0,false);
            mNotifyManager.notify(0, mBuilder.build());

			checkForDeletedFiles();

			Intent intent = new Intent(getApplicationContext(),
					DownloadService.class);
			startService(intent);
			stopSelf(msg.arg1);
		}

		private InputStream downloadUrl(URL url) {
			HttpURLConnection conn;
			try {
				conn = (HttpURLConnection) url.openConnection();
				conn.setReadTimeout(10000 /* milliseconds */);
				conn.setConnectTimeout(15000 /* milliseconds */);
				conn.setRequestMethod("GET");
				conn.setDoInput(true);
				// Starts the query
				conn.connect();
				return conn.getInputStream();
			} catch (IOException e) {
				Log.e(LOG_TAG, "IOException: " + e.toString());
				e.printStackTrace();
				return null;
			}
		}

		private void checkForDeletedFiles() {
			List<Download> downloads = dataHandler.getDownloaded();

			for (Download download : downloads) {
				if (download.getId() == -1) {
					episodeHelper.updateEpisodeStatus(download.getEpisodeId(),
							Episode.STATUS_READ);
					continue;
				}

				URI fileUri = download.getFile();
				File file = new File(fileUri);
				if (!file.exists()) {
					episodeHelper.updateEpisodeStatus(download.getEpisodeId(),
							Episode.STATUS_READ);
					dataHandler.deleteDownload(download.getId());
				}
			}
		}
	}

	// Called when started
	@Override
	public void onCreate() {
		if (BuildConfig.DEBUG)
			Log.v(LOG_TAG, "onCreate()");
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		thread.start();

		context = getApplicationContext();

		mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setContentTitle(getString(R.string.update_subscriptions))
	    		.setContentText(getString(R.string.update_inprogress))
    			.setSmallIcon(R.drawable.ic_launcher);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		// Get the HandlerThread's Looper and use it for our Handler
		mServiceLooper = thread.getLooper();
		mServiceHandler = new ServiceHandler(mServiceLooper);
	}

	// Called even if the service is already running
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// For each start request, send a message to start a job and deliver the
		// start ID so we know which request we're stopping when we finish the
		// job
		Message msg = mServiceHandler.obtainMessage();
		msg.arg1 = startId;
		mServiceHandler.sendMessage(msg);

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {}
}