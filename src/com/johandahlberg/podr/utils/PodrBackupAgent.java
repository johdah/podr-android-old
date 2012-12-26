package com.johandahlberg.podr.utils;

import java.io.File;
import java.io.IOException;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.BackupManager;
import android.app.backup.FileBackupHelper;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class PodrBackupAgent extends BackupAgentHelper {
	private static final String LOG_TAG = ".utils.PodrBackupAgent";
	static final Object sDataLock = new Object();

    // The names of the SharedPreferences groups that the application maintains.  These
    // are the same strings that are passed to getSharedPreferences(String, int).
    static final String PREFS_AUTODOWNLOAD_LIMIT = "pref_key_autodownload_limit";
    static final String PREFS_DOWNLOAD_ONLY_WIFI = "pref_key_download_only_wifi";
    static final String PREFS_UPDATE_ONLY_WIFI = "pref_key_update_only_wifi";

    // An arbitrary string used within the BackupAgentHelper implementation to
    // identify the SharedPreferencesBackupHelper's data.
    static final String PREFS_BACKUP_KEY = "podr_prefs";
    static final String SUBSCRIPTION_BACKUP_KEY = "podr_subscriptions";

    // Simply allocate a helper and install it
    public void onCreate() {
    	Log.d(LOG_TAG, "onCreate()");

    	// TODO: Not working
    	SharedPreferencesBackupHelper prefHelper = new SharedPreferencesBackupHelper(
    			this, 
				PREFS_AUTODOWNLOAD_LIMIT,
				PREFS_DOWNLOAD_ONLY_WIFI,
				PREFS_UPDATE_ONLY_WIFI);
        addHelper(PREFS_BACKUP_KEY, prefHelper);

		/*PodrBackupHelper backupHelper = new PodrBackupHelper(getApplicationContext());
		backupHelper.backup();
		File externalStorage = new File(Environment.getExternalStorageDirectory(),
				Environment.DIRECTORY_DOWNLOADS);
		String uri = externalStorage.toString() + "/podr-export.opml";
        FileBackupHelper subHelper = new FileBackupHelper(this, uri);
        addHelper(SUBSCRIPTION_BACKUP_KEY, subHelper);*/
    }
    
    @Override
    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data,
              ParcelFileDescriptor newState) throws IOException {
    	Log.d(LOG_TAG, "onBackup()");
        // Hold the lock while the FileBackupHelper performs backup
        synchronized (PodrBackupAgent.sDataLock) {
            super.onBackup(oldState, data, newState);
        }
    }

    @Override
    public void onRestore(BackupDataInput data, int appVersionCode,
            ParcelFileDescriptor newState) throws IOException {
    	Log.d(LOG_TAG, "onRestore()");
        // Hold the lock while the FileBackupHelper restores the file
        synchronized (PodrBackupAgent.sDataLock) {
            super.onRestore(data, appVersionCode, newState);
        }
    }
    
    public static void requestBackup(Context context) {
    	Log.d(LOG_TAG, "requestBackup()");
    	BackupManager bm = new BackupManager(context);
    	bm.dataChanged();
    }
}
