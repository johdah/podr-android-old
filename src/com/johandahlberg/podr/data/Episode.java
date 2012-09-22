package com.johandahlberg.podr.data;

import java.net.URL;
import java.util.Date;
import java.util.List;

public class Episode {
	public static final int STATUS_UNREAD = 0;
	public static final int STATUS_READ = 1;
	public static final int STATUS_DOWNLOADING = 2;
	public static final int STATUS_DOWNLOADED = 3;
	
	private int _id = -1;
	private String title = null;
	private String guid = null;
	private Date pubDate = null;
	private URL enclosure = null;
	private int subscriptionId = -1;
	
	/* iTunes elements */
	private String itunesAuthor = null;
	private String itunesSubtitle = null;
	private String itunesSummary = null;
	private URL itunesImage = null;
	private String itunesDuration = null;
	private List<String> itunesKeywords = null;

	private int status = STATUS_UNREAD;
	
	public Episode() {}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public URL getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(URL enclosure) {
		this.enclosure = enclosure;
	}

	public int getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(int subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getItunesAuthor() {
		return itunesAuthor;
	}

	public void setItunesAuthor(String itunesAuthor) {
		this.itunesAuthor = itunesAuthor;
	}

	public String getItunesSubtitle() {
		return itunesSubtitle;
	}

	public void setItunesSubtitle(String itunesSubtitle) {
		this.itunesSubtitle = itunesSubtitle;
	}

	public String getItunesSummary() {
		return itunesSummary;
	}

	public void setItunesSummary(String itunesSummary) {
		this.itunesSummary = itunesSummary;
	}

	public URL getItunesImage() {
		return itunesImage;
	}

	public void setItunesImage(URL itunesImage) {
		this.itunesImage = itunesImage;
	}

	public String getItunesDuration() {
		return itunesDuration;
	}

	public void setItunesDuration(String itunesDuration) {
		this.itunesDuration = itunesDuration;
	}

	public List<String> getItunesKeywords() {
		return itunesKeywords;
	}

	public void setItunesKeywords(List<String> itunesKeywords) {
		this.itunesKeywords = itunesKeywords;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _id;
		result = prime * result
				+ ((enclosure == null) ? 0 : enclosure.hashCode());
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
		result = prime * result
				+ ((itunesAuthor == null) ? 0 : itunesAuthor.hashCode());
		result = prime * result
				+ ((itunesDuration == null) ? 0 : itunesDuration.hashCode());
		result = prime * result
				+ ((itunesImage == null) ? 0 : itunesImage.hashCode());
		result = prime * result
				+ ((itunesKeywords == null) ? 0 : itunesKeywords.hashCode());
		result = prime * result
				+ ((itunesSubtitle == null) ? 0 : itunesSubtitle.hashCode());
		result = prime * result
				+ ((itunesSummary == null) ? 0 : itunesSummary.hashCode());
		result = prime * result + ((pubDate == null) ? 0 : pubDate.hashCode());
		result = prime * result + status;
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Episode other = (Episode) obj;
		if (_id != other._id)
			return false;
		if (enclosure == null) {
			if (other.enclosure != null)
				return false;
		} else if (!enclosure.equals(other.enclosure))
			return false;
		if (guid == null) {
			if (other.guid != null)
				return false;
		} else if (!guid.equals(other.guid))
			return false;
		if (itunesAuthor == null) {
			if (other.itunesAuthor != null)
				return false;
		} else if (!itunesAuthor.equals(other.itunesAuthor))
			return false;
		if (itunesDuration == null) {
			if (other.itunesDuration != null)
				return false;
		} else if (!itunesDuration.equals(other.itunesDuration))
			return false;
		if (itunesImage == null) {
			if (other.itunesImage != null)
				return false;
		} else if (!itunesImage.equals(other.itunesImage))
			return false;
		if (itunesKeywords == null) {
			if (other.itunesKeywords != null)
				return false;
		} else if (!itunesKeywords.equals(other.itunesKeywords))
			return false;
		if (itunesSubtitle == null) {
			if (other.itunesSubtitle != null)
				return false;
		} else if (!itunesSubtitle.equals(other.itunesSubtitle))
			return false;
		if (itunesSummary == null) {
			if (other.itunesSummary != null)
				return false;
		} else if (!itunesSummary.equals(other.itunesSummary))
			return false;
		if (pubDate == null) {
			if (other.pubDate != null)
				return false;
		} else if (!pubDate.equals(other.pubDate))
			return false;
		if (status != other.status)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Episode [_id=" + _id + ", title=" + title + ", guid=" + guid
				+ ", pubDate=" + pubDate + ", status=" + status + "]";
	}
}