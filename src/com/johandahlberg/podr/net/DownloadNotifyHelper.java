package com.johandahlberg.podr.net;

import com.johandahlberg.podr.R;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

public class DownloadNotifyHelper {
	private NotificationManager mNotifyManager = null;
	private NotificationCompat.Builder mBuilder = null;
	
	private static DownloadNotifyHelper _instance;
	private Context ctx;
	
	/** A private Constructor prevents any other class from instantiating. */
	private DownloadNotifyHelper(Context context) {
		this.ctx = context;
	}
	
	public static synchronized DownloadNotifyHelper getInstance(Context context) {
		if (_instance == null) {
			_instance = new DownloadNotifyHelper(context);
		}
		return _instance;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
	public void createNewNotification(String title, String text) {
		mNotifyManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		mBuilder = new NotificationCompat.Builder(ctx);
		mBuilder.setContentTitle(title)
	    		.setContentText(text)
    			.setSmallIcon(R.drawable.ic_launcher);
	}
	
	public void notifyManager() {
		mNotifyManager.notify(0, mBuilder.build());
	}
	
	public void setContentText(String text) {
		mBuilder.setContentText(text);
	}
	
	public void setContentTitle(String text) {
		mBuilder.setContentTitle(text);
	}
	
	public void setProgress(int max, int count, boolean indeterminate) {
		mBuilder.setProgress(max, count, indeterminate);
	}
}