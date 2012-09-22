package com.johandahlberg.podr.data;

import java.net.URL;
import java.util.Date;
import java.util.List;

public class Subscription {
	private int _id;
	private String title;
	private URL link;
	private String language;
	private String copyright;
	private String desc;
	private Date lastUpdated;
	private boolean autoDownload;

	/* iTunes elements */
	private String itunesSubtitle;
	private String itunesAuthor;
	private String itunesSummary;
	private String itunesOwnerName;
	private String itunesOwnerEmail;
	private URL itunesImage;
	private List<String> itunesCategory;

	public Subscription() {
		// TODO Auto-generated constructor stub
	}

	public Subscription(URL link) {
		this.link = link;
	}

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

	public URL getLink() {
		return link;
	}

	public void setLink(URL link) {
		this.link = link;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCopyright() {
		return copyright;
	}

	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public boolean isAutoDownload() {
		return autoDownload;
	}

	public void setAutoDownload(boolean autoDownload) {
		this.autoDownload = autoDownload;
	}

	public String getItunesSubtitle() {
		return itunesSubtitle;
	}

	public void setItunesSubtitle(String itunesSubtitle) {
		this.itunesSubtitle = itunesSubtitle;
	}

	public String getItunesAuthor() {
		return itunesAuthor;
	}

	public void setItunesAuthor(String itunesAuthor) {
		this.itunesAuthor = itunesAuthor;
	}

	public String getItunesSummary() {
		return itunesSummary;
	}

	public void setItunesSummary(String itunesSummary) {
		this.itunesSummary = itunesSummary;
	}

	public String getItunesOwnerName() {
		return itunesOwnerName;
	}

	public void setItunesOwnerName(String itunesOwnerName) {
		this.itunesOwnerName = itunesOwnerName;
	}

	public String getItunesOwnerEmail() {
		return itunesOwnerEmail;
	}

	public void setItunesOwnerEmail(String itunesOwnerEmail) {
		this.itunesOwnerEmail = itunesOwnerEmail;
	}

	public URL getItunesImage() {
		return itunesImage;
	}

	public void setItunesImage(URL itunesImage) {
		this.itunesImage = itunesImage;
	}

	public List<String> getItunesCategory() {
		return itunesCategory;
	}

	public void setItunesCategory(List<String> itunesCategory) {
		this.itunesCategory = itunesCategory;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + _id;
		result = prime * result
				+ ((copyright == null) ? 0 : copyright.hashCode());
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result
				+ ((itunesAuthor == null) ? 0 : itunesAuthor.hashCode());
		result = prime * result
				+ ((itunesCategory == null) ? 0 : itunesCategory.hashCode());
		result = prime * result
				+ ((itunesImage == null) ? 0 : itunesImage.hashCode());
		result = prime
				* result
				+ ((itunesOwnerEmail == null) ? 0 : itunesOwnerEmail.hashCode());
		result = prime * result
				+ ((itunesOwnerName == null) ? 0 : itunesOwnerName.hashCode());
		result = prime * result
				+ ((itunesSubtitle == null) ? 0 : itunesSubtitle.hashCode());
		result = prime * result
				+ ((itunesSummary == null) ? 0 : itunesSummary.hashCode());
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
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
		Subscription other = (Subscription) obj;
		if (_id != other._id)
			return false;
		if (copyright == null) {
			if (other.copyright != null)
				return false;
		} else if (!copyright.equals(other.copyright))
			return false;
		if (desc == null) {
			if (other.desc != null)
				return false;
		} else if (!desc.equals(other.desc))
			return false;
		if (itunesAuthor == null) {
			if (other.itunesAuthor != null)
				return false;
		} else if (!itunesAuthor.equals(other.itunesAuthor))
			return false;
		if (itunesCategory == null) {
			if (other.itunesCategory != null)
				return false;
		} else if (!itunesCategory.equals(other.itunesCategory))
			return false;
		if (itunesImage == null) {
			if (other.itunesImage != null)
				return false;
		} else if (!itunesImage.equals(other.itunesImage))
			return false;
		if (itunesOwnerEmail == null) {
			if (other.itunesOwnerEmail != null)
				return false;
		} else if (!itunesOwnerEmail.equals(other.itunesOwnerEmail))
			return false;
		if (itunesOwnerName == null) {
			if (other.itunesOwnerName != null)
				return false;
		} else if (!itunesOwnerName.equals(other.itunesOwnerName))
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
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
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
		return "Subscription [_id=" + _id + ", title=" + title + ", link="
				+ link + ", language=" + language + ", copyright=" + copyright
				+ ", desc=" + desc + "]";
	}
}