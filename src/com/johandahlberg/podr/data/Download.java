package com.johandahlberg.podr.data;

import java.net.URI;
import java.net.URL;

public class Download {
	private int id = -1;
	private int episodeId = -1;
	private URL url;
	private URI file;

	public Download(int id) {
		this.setId(id);
	}

	public Download(int id, int episodeId, URL url) {
		this.setId(id);
		this.episodeId = episodeId;
		this.url = url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + episodeId;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + id;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		Download other = (Download) obj;
		if (episodeId != other.episodeId)
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (id != other.id)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEpisodeId() {
		return episodeId;
	}

	public void setEpisodeId(int episodeId) {
		this.episodeId = episodeId;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public URI getFile() {
		return file;
	}

	public void setFile(URI file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "Download [id=" + id + ", episodeId=" + episodeId + ", url="
				+ url + ", file=" + file + "]";
	}
}