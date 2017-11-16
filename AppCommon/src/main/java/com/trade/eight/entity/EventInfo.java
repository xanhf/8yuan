package com.trade.eight.entity;

public class EventInfo {
	private long contentId;
	private String id ;
	private String image_url;
	private String title;

	private String url;
	private String summary;
	private String add_time;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getImage_url() {
		return image_url;
	}
	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getAdd_time() {
		return add_time;
	}
	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public long getContentId() {
		return contentId;
	}

	public void setContentId(long contentId) {
		this.contentId = contentId;
	}
}
