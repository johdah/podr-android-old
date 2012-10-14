package com.johandahlberg.podr.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.johandahlberg.podr.R;
import com.johandahlberg.podr.data.Download;
import com.johandahlberg.podr.data.Episode;
import com.johandahlberg.podr.data.PodrDataHandler;
import com.johandahlberg.podr.data.Subscription;
import com.johandahlberg.podr.data.helpers.PodrEpisodeHelper;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class DownloadIntentService extends IntentService {
	private static final String LOG_TAG = ".net.DownloadIntentService";

	private int id;
	private int downloadId;
	private int episodeId;
	private String downloadProgress = "";
	private PodrDataHandler dataHandler;
	private PodrEpisodeHelper episodeHelper;
	private DownloadNotifyHelper notifyHelper;
	private final static String VALIDCHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 $%`-_@{}~!#().";

	public DownloadIntentService() {
		super("DownloadService");
	}

	// Will be called asynchronously be Android
	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		dataHandler = new PodrDataHandler(getApplicationContext());
		episodeHelper = new PodrEpisodeHelper(getApplicationContext());
		notifyHelper = DownloadNotifyHelper.getInstance(getApplicationContext());
		this.downloadProgress = intent.getStringExtra("downloadProgress");
		this.id = intent.getIntExtra("id", -1);
		this.downloadId = intent.getIntExtra("downloadId", -1);
		this.episodeId = intent.getIntExtra("episodeId", -1);
		PodrDataHandler dataHandler = new PodrDataHandler(
				getApplicationContext());
		if (id == -1 || episodeId == -1)
			stopSelf();

		Download download = dataHandler.getDownloadById(downloadId);
		Episode episode = episodeHelper.getEpisodeById(episodeId);
		Subscription subscription = dataHandler.getSubscriptionById(episode
				.getSubscriptionId());

		File output = this.generateFilePath(episode);
		if (output.exists()) {
			output.delete();
		}

		InputStream stream = null;
		FileOutputStream fos = null;
		try {
			URLConnection urlConn = download.getUrl().openConnection();
			InputStream inputStream = urlConn.getInputStream();
			fos = new FileOutputStream(output.getPath());
			int totalDownloadSize = urlConn.getContentLength();
			int downloaded = 0;

			byte[] buffer = new byte[1024];
			int bufferLength = 0;

			int nextNotification = 0;

			notifyHelper.setContentText(subscription.getTitle() + " " + downloadProgress);
			notifyHelper.setProgress(totalDownloadSize, 0, false);
			notifyHelper.notifyManager();
			
			while ((bufferLength = inputStream.read(buffer)) > 0) {
				fos.write(buffer, 0, bufferLength);
				downloaded += bufferLength;

				if (downloaded >= nextNotification) {
					notifyHelper.setProgress(totalDownloadSize, downloaded, false);
					notifyHelper.notifyManager();
					nextNotification += totalDownloadSize / 100;
				}
			}

			// Sucessful finished
			download.setFile(output.toURI());
			episodeHelper.updateEpisodeStatus(download.getEpisodeId(),
					Episode.STATUS_DOWNLOADED);
			dataHandler.updateDownload(download);
			
			notifyHelper.setProgress(0, 0, false);
			notifyHelper.setContentText(getString(R.string.download_completed));
			notifyHelper.notifyManager();
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "FileNotFoundException: " + e.toString());
			e.printStackTrace();
		} catch (Exception e) {
			Log.e(LOG_TAG, "Exception: " + e.toString());
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "IOException: " + e.toString());
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					Log.e(LOG_TAG, "IOException: " + e.toString());
					e.printStackTrace();
				}
			}
		}

		if (extras != null) {
			Messenger messenger = (Messenger) extras.get("MESSENGER");
			Message msg = Message.obtain();
			msg.arg1 = id;
			msg.obj = output.getAbsolutePath();
			try {
				messenger.send(msg);
			} catch (android.os.RemoteException e1) {
				Log.w(LOG_TAG, "Exception sending message", e1);
			}
		}
	}

	public File generateFilePath(Episode episode) {
		Subscription subscription = dataHandler.getSubscriptionById(episode
				.getSubscriptionId());
		// Trim is necessary since a directory can't end with spaces.
		String folder = makeSafe(subscription.getTitle().trim());
		String fileName = makeSafe(episode.getEnclosure().getFile());
		String path = folder + "/" + fileName;

		// the path needs converting to make sure it's a valid path name
		Pattern p = Pattern.compile("[?><\\:*|^]");
		Matcher m = p.matcher(path);
		path = m.replaceAll("_");
		File dir = new File(Environment.getExternalStorageDirectory(),
				Environment.DIRECTORY_PODCASTS);
		if (!new File(dir, folder).exists()) {
			new File(dir, folder).mkdir();
		}
		return new File(dir, path);
	}

	private String makeSafe(String filename) {
		StringBuilder fixedName = new StringBuilder();
		for (int c = 0; c < filename.length(); c++) { // Make a valid name:
			if (VALIDCHARS.indexOf(filename.charAt(c)) > -1) {
				fixedName.append(filename.charAt(c));
			}
		}
		return fixedName.toString();
	}
}