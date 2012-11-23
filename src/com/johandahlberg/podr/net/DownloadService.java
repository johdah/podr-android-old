package com.johandahlberg.podr.net;

import java.util.List;

import com.johandahlberg.podr.R;
import com.johandahlberg.podr.data.Download;
import com.johandahlberg.podr.data.PodrDataHandler;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class DownloadService extends Service {
	private static final String LOG_TAG = ".net.DownloadService";

	private PodrDataHandler dataHandler;
	private DownloadNotifyHelper notifyHelper;
	private ConnectivityManager connMgr = null;
	private SharedPreferences sharedPref;
	private List<Download> downloads;
	private int initialDownloads = 0;
	
	boolean downloadOnlyWifi = true;

	private Handler downloadHandler = new Handler() {
		public void handleMessage(Message message) {
			Object path = message.obj;
			Intent intent;
			
			if (message.arg1 == -1) { // First
				if (!downloads.isEmpty()) {
					intent = new Intent(getApplicationContext(),
							DownloadIntentService.class);
					Messenger messenger = new Messenger(downloadHandler);
					intent.putExtra("MESSENGER", messenger);
					intent.putExtra("downloadProgress", "("
							+ (initialDownloads - downloads.size() + 1) + "/"
							+ initialDownloads + ")");
					intent.putExtra("id", 0);
					intent.putExtra("downloadId", downloads.get(0).getId());
					intent.putExtra("episodeId", downloads.get(0)
							.getEpisodeId());
					startService(intent);
				}
			} else if (path != null) { // Success
				downloads.remove(message.arg1);
				if (!downloads.isEmpty()) {
					intent = new Intent(getApplicationContext(),
							DownloadIntentService.class);
					Messenger messenger = new Messenger(downloadHandler);
					intent.putExtra("MESSENGER", messenger);
					intent.putExtra("downloadProgress", "("
							+ (initialDownloads - downloads.size() + 1) + "/"
							+ initialDownloads + ")");
					intent.putExtra("id", message.arg1);
					intent.putExtra("downloadId", downloads.get(0).getId());
					intent.putExtra("episodeId", downloads.get(0)
							.getEpisodeId());
					startService(intent);
				}
			} else { // Failed
				String stringPath = (path != null) ? path.toString()
						: "unknown";
				Toast.makeText(DownloadService.this,
						"Download failed on: " + stringPath, Toast.LENGTH_LONG)
						.show();
				downloads.remove(0);
				if (!downloads.isEmpty()) {
					intent = new Intent(getApplicationContext(),
							DownloadIntentService.class);
					Messenger messenger = new Messenger(downloadHandler);
					intent.putExtra("MESSENGER", messenger);
					intent.putExtra("downloadProgress", "("
							+ (initialDownloads - downloads.size() + 1) + "/"
							+ initialDownloads + ")");
					intent.putExtra("id", message.arg1);
					intent.putExtra("downloadId", downloads.get(0).getId());
					intent.putExtra("episodeId", downloads.get(0)
							.getEpisodeId());
					startService(intent);
				}
			}

		};
	};

	// Is only run when the service (singelton) is started
	@Override
	public void onCreate() {
		// Start up the thread running the service. Note that we create a
		// separate thread because the service normally runs in the process's
		// main thread, which we don't want to block. We also make it
		// background priority so CPU-intensive work will not disrupt our UI.
		HandlerThread thread = new HandlerThread("ServiceStartArguments",
				Process.THREAD_PRIORITY_BACKGROUND);
		dataHandler = new PodrDataHandler(getApplicationContext());
		connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		notifyHelper = DownloadNotifyHelper.getInstance(getApplicationContext());
		
		downloads = dataHandler.getDownloading();
		
		boolean downloadRemoved = false;
		int index = -1;
		for(int i = 0; i < downloads.size(); i++) {
			index++;
			if(downloads.get(index) == null) {
				downloads.remove(index);
				downloadRemoved = true;
				index--;
			}
		}
		if(downloadRemoved) {
			Toast.makeText(this, getString(R.string.downloading_some_failed), Toast.LENGTH_SHORT).show();
		}
		
		initialDownloads = downloads.size();
		downloadOnlyWifi = sharedPref.getBoolean("pref_key_download_only_wifi", true);
		thread.start();

		// Get the HandlerThread's Looper and use it for our Handler
		// Looper mServiceLooper = thread.getLooper();
	}

	// Is run every time a download is triggered
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (downloads.isEmpty()) {
			stopSelf();
		}
		
		NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
		if(!downloadOnlyWifi || activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
			notifyHelper.createNewNotification(
					getString(R.string.download_episodes),
					getString(R.string.download_inprogress));
			notifyHelper.setContentText(getString(R.string.download_inprogress) + ": 0/" + downloads.size());
			notifyHelper.notifyManager();

			// For each start request, send a message to start a job and deliver the
			// start ID so we know which request we're stopping when we finish the
			// job
			Message msg = downloadHandler.obtainMessage();
			msg.arg1 = -1;
			downloadHandler.sendMessage(msg);
		} else {
			stopSelf();
		}

		// If we get killed, after returning from here, restart
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
	}
}