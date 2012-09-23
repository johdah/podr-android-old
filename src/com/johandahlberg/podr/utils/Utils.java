package com.johandahlberg.podr.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

import android.content.Context;

import com.johandahlberg.podr.BuildConfig;

public class Utils {
	private static final long sAppLoadTime = System.currentTimeMillis();

	public static final String dateFormats[] = new String[] {
		new String("E, dd MMM yyyy HH:mm:ss Z"),
		new String("EEE, d MMM yy HH:mm:ss z"),
		new String("EEE, d MMM yy HH:mm z"),
		new String("EEE, d MMM yyyy HH:mm:ss z"),
		new String("EEE, d MMM yyyy HH:mm z"),
		new String("d MMM yy HH:mm z"),
		new String("d MMM yy HH:mm:ss z"),
		new String("d MMM yyyy HH:mm z"),
		new String("d MMM yyyy HH:mm:ss z"), };

	public static long getCurrentTime(final Context context) {
		if (BuildConfig.DEBUG) {
			return context.getSharedPreferences("mock_data",
					Context.MODE_PRIVATE).getLong("mock_current_time",
					System.currentTimeMillis())
					+ System.currentTimeMillis() - sAppLoadTime;
		} else {
			return System.currentTimeMillis();
		}
	}
	
	public static boolean isSameMonth(long time1, long time2) {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }

	public static Date parseDate(String input) {
		Date date = null;
		
		try {
			date = DateUtils.parseDate(input, dateFormats);
		} catch (DateParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
}