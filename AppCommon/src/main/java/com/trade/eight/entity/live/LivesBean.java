package com.trade.eight.entity.live;

import java.io.Serializable;

public class LivesBean implements Serializable{
	private String node_title;
	private String nid;
	private String node_created;
	private String node_content;
	private String node_color;
	private String node_icon;
	private String node_format;
	private String source;
	private String sourcelink;
	private String itemType;
	private int importance;

	public static final String ITEM_1 = "item_1";//时间标题
	public static final String ITEM_2 = "item_2";//正常内容
	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getNode_title() {
		return node_title;
	}

	public void setNode_title(String node_title) {
		this.node_title = node_title;
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public String getNode_created() {
		return node_created;
	}

	public void setNode_created(String node_created) {
		this.node_created = node_created;
	}

	public String getNode_content() {
		return node_content;
	}

	public void setNode_content(String node_content) {
		this.node_content = node_content;
	}

	public String getNode_color() {
		return node_color;
	}

	public void setNode_color(String node_color) {
		this.node_color = node_color;
	}

	public String getNode_icon() {
		return node_icon;
	}

	public void setNode_icon(String node_icon) {
		this.node_icon = node_icon;
	}

	public String getNode_format() {
		return node_format;
	}

	public void setNode_format(String node_format) {
		this.node_format = node_format;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSourcelink() {
		return sourcelink;
	}

	public void setSourcelink(String sourcelink) {
		this.sourcelink = sourcelink;
	}

	@Override
	public String toString() {
		return "LivesBean [node_title=" + node_title + ", nid=" + nid
				+ ", node_created=" + node_created + ", node_content="
				+ node_content + ", node_color=" + node_color + ", node_icon="
				+ node_icon + ", node_format=" + node_format + ", source="
				+ source + ", sourcelink=" + sourcelink + "]";
	}

	public int getImportance() {
		return importance;
	}

	public void setImportance(int importance) {
		this.importance = importance;
	}
}
