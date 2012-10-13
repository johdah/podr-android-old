package com.johandahlberg.podr.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.johandahlberg.podr.data.PodrDataHandler;
import com.johandahlberg.podr.data.Subscription;

public class PodrBackupHelper {
	private static final String LOG_TAG = ".utils.PodrBackupHelper";
	private static final String ns = null;
	private PodrDataHandler dataHandler;
	private Context ctx;
	private String filename = "podr-export.opml";

	public PodrBackupHelper(Context context) {
		this.ctx = context;
		this.dataHandler = new PodrDataHandler(ctx);
	}

	// TODO: Remove static texts
	// TODO: Add notifications
	public boolean backup() {
		File externalStorage = new File(Environment.getExternalStorageDirectory(),
				Environment.DIRECTORY_DOWNLOADS);
		FileWriter fw;
		try {
			fw = new FileWriter(new File(externalStorage, filename));
			fw.write(generateXML());
			fw.close();
		} catch (FileNotFoundException e1) {
			Log.e(LOG_TAG, "backup() - FileNotFoundException");
			Toast.makeText(ctx, "Backup failed!", Toast.LENGTH_SHORT).show();
			return false;
		} catch (IOException e) {
			Log.e(LOG_TAG, "backup() - IOException");
			Toast.makeText(ctx, "Backup failed!", Toast.LENGTH_SHORT).show();
			return false;
		}

		Toast.makeText(ctx, "Backup succeeded!", Toast.LENGTH_SHORT).show();
		return true;
	}

	public String generateXML() {
		XmlSerializer serializer = Xml.newSerializer();
		StringWriter writer = new StringWriter();
		List<Subscription> subscriptions = dataHandler.getSubscriptions();

		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			serializer.startTag("", "opml");
			serializer.attribute("", "version", "2.0");
			serializer.startTag("", "head");
			serializer.startTag("", "dateCreated");
			
			DateFormat dateFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z");
			Date date = new Date();
			serializer.text(dateFormat.format(date));
			
			serializer.endTag("", "dateCreated");
			serializer.endTag("", "head");
			serializer.startTag("", "body");
			
			for (Subscription subscription : subscriptions) {
				serializer.startTag("", "outline");
				serializer.attribute("", "xmlUrl", subscription.getLink().toString());
				serializer.endTag("", "outline");
			}

			serializer.endTag("", "body");
			serializer.endTag("", "opml");
			serializer.endDocument();
			return writer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// TODO: Remove static texts
	// TODO: Add notifications
	public boolean restore() {
		File externalStorage = new File(Environment.getExternalStorageDirectory(),
				Environment.DIRECTORY_DOWNLOADS);
		InputStream reader = null;
		try {
			reader = new BufferedInputStream(new FileInputStream(new File(
					externalStorage, filename)));
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "restore() - FileNotFoundException");
			Toast.makeText(ctx, "Restore failed!", Toast.LENGTH_SHORT).show();
		} finally {
			if (reader != null) {
				try {
					if (parseXML(reader))
						Toast.makeText(ctx, "Restore succeeded!", Toast.LENGTH_SHORT).show();
					reader.close();
					return true;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	public boolean parseXML(InputStream reader) {
		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(reader, null);
			parser.nextTag();

			parser.require(XmlPullParser.START_TAG, ns, "opml");

			while (parser.next() != XmlPullParser.END_TAG) {
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}

				String name = parser.getName();
				if (name.equals("head")) {
					parseHead(parser);
				} else if (name.equals("body")) {
					parseBody(parser);
				} else {
					skipXML(parser);
				}
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(ctx, "XmlPullParserException!",
					Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(ctx, "IOException!",
					Toast.LENGTH_SHORT).show();
		}

		return false;
	}

	private void parseHead(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "head");
		Subscription subscription = null;
		
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = parser.getName();
			if (name.equals("outline")) {
				subscription = new Subscription(parseOutline(parser));
				if (subscription != null && dataHandler.isSubscriptionUnique(subscription.getLink().toString()))
					dataHandler.addSubscription(subscription);
			} else {
				skipXML(parser);
			}
		}
	}

	private void parseBody(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "body");
		Subscription subscription = null;
		
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			
			String name = parser.getName();
			if (name.equals("outline")) {
				subscription = new Subscription(parseOutline(parser));
				if (subscription != null && dataHandler.isSubscriptionUnique(subscription.getLink().toString()))
					dataHandler.addSubscription(subscription);
			} else {
				skipXML(parser);
			}
		}
	}

	private URL parseOutline(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "outline");
	    URL url = new URL(parser.getAttributeValue(null, "xmlUrl"));  
        parser.nextTag();
	    parser.require(XmlPullParser.END_TAG, ns, "outline");
	    
	    return url;
	}

	private void skipXML(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}