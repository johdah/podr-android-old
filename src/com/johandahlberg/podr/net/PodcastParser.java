package com.johandahlberg.podr.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.johandahlberg.podr.data.Episode;
import com.johandahlberg.podr.data.PodrDataHandler;
import com.johandahlberg.podr.data.Subscription;
import com.johandahlberg.podr.utils.Utils;

public class PodcastParser {
	private static final String LOG_TAG = ".net.PodcastParser";
	private static final String ns = null;
	private PodrDataHandler dataHandler;
	public Subscription subscription;
	public List<Episode> episodes;

	public PodcastParser(Context context, Subscription subscription) {
		Log.v(LOG_TAG,
				"Beginning parsing subscription: " + subscription.get_id());
		this.subscription = subscription;
		episodes = new ArrayList<Episode>();
		this.dataHandler = new PodrDataHandler(context);
	}

	public void parse(InputStream in) throws IOException,
			XmlPullParserException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			readFeed(parser);
		} catch (XmlPullParserException e) {
			throw e;
		} finally {
			in.close();
		}
	}

	// TODO: Add categories
	private void readFeed(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		Episode episode;

		parser.require(XmlPullParser.START_TAG, ns, "rss");
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, ns, "channel");

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("item")) {
				episode = readEpisode(parser);
				if (dataHandler.isEpisodeUnique(episode.getGuid())) {
					episodes.add(episode);
				} else {
					return;
				}
			} else if (name.equals("title")) {
				subscription.setTitle(readStringElement(parser, "title"));
			} else if (name.equals("copyright")) {
				subscription
						.setCopyright(readStringElement(parser, "copyright"));
			} else if (name.equals("description")) {
				subscription.setDesc(readStringElement(parser, "description"));
			} else if (name.equals("language")) {
				subscription.setLanguage(readStringElement(parser, "language"));
			} else if (name.equals("lastBuildDate")) {
				subscription.setLastUpdated(readDate(parser, "lastBuildDate"));
			} else if (name.equals("itunes:author")) {
				subscription.setItunesAuthor(readStringElement(parser,
						"itunes:author"));
			} else if (name.equals("itunes:image")) {
				subscription.setItunesImage(new URL(readAttributeElement(
						parser, "itunes:image", "href")));
			} else if (name.equals("itunes:email")) {
				subscription.setItunesOwnerEmail(readStringElement(parser,
						"itunes:email"));
			} else if (name.equals("itunes:name")) {
				subscription.setItunesOwnerName(readStringElement(parser,
						"itunes:name"));
			} else if (name.equals("itunes:subtitle")) {
				subscription.setItunesSubtitle(readStringElement(parser,
						"itunes:subtitle"));
			} else if (name.equals("itunes:summary")) {
				subscription.setItunesSummary(readStringElement(parser,
						"itunes:summary"));
			} else {
				skip(parser);
			}
		}
	}

	// TODO: Add keywords
	private Episode readEpisode(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "item");
		Episode episode = new Episode();
		episode.setSubscriptionId(subscription.get_id());

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("title")) {
				episode.setTitle(readStringElement(parser, "title"));
			} else if (name.equals("guid")) {
				episode.setGuid(readStringElement(parser, "guid"));
			} else if (name.equals("enclosure")) {
				episode.setEnclosure(new URL(readAttributeElement(parser,
						"enclosure", "url")));
			} else if (name.equals("pubDate")) {
				episode.setPubDate(readDate(parser, "pubDate"));
			} else if (name.equals("itunes:author")) {
				episode.setItunesAuthor(readStringElement(parser,
						"itunes:author"));
			} else if (name.equals("itunes:duration")) {
				episode.setItunesDuration(readStringElement(parser,
						"itunes:duration"));
			} else if (name.equals("itunes:image")) {
				episode.setItunesImage(new URL(readAttributeElement(parser,
						"itunes:image", "href")));
			} else if (name.equals("itunes:subtitle")) {
				episode.setItunesSubtitle(readStringElement(parser,
						"itunes:subtitle"));
			} else if (name.equals("itunes:summary")) {
				episode.setItunesSummary(readStringElement(parser,
						"itunes:summary"));
			} else {
				skip(parser);
			}
		}

		return episode;
	}

	private Date readDate(XmlPullParser parser, String tag) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, tag);

		Date date = Utils.parseDate(readText(parser));

		parser.require(XmlPullParser.END_TAG, ns, tag);
		return date;
	}

	private String readAttributeElement(XmlPullParser parser, String tag,
			String attribute) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, tag);
		String url = parser.getAttributeValue(null, attribute);
		parser.nextTag();
		parser.require(XmlPullParser.END_TAG, ns, tag);

		return url;
	}

	private String readStringElement(XmlPullParser parser, String tag)
			throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, tag);
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, tag);
		return title;
	}

	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException,
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

	public Subscription getSubscription() {
		return subscription;
	}

	public List<Episode> getEpisodes() {
		if (episodes.isEmpty()) {
			return Collections.emptyList();
		}

		return episodes;
	}

}