package uk.co.q3c.v7.base.navigate.sitemap;

public class PageRecord {
	private String StandardPageKeyName;
	private String uri;
	private String segment;
	private String labelKeyName;

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getStandardPageKeyName() {
		return StandardPageKeyName;
	}

	public void setStandardPageKeyName(String standardPageKeyName) {
		StandardPageKeyName = standardPageKeyName;
	}

	public String getLabelKeyName() {
		return labelKeyName;
	}

	public void setLabelKeyName(String labelKeyName) {
		this.labelKeyName = labelKeyName;
	}
}
