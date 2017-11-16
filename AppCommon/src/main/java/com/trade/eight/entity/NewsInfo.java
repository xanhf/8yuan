package com.trade.eight.entity;

import java.io.Serializable;

public class NewsInfo implements Serializable{

	private String node_title;
	private int nid;
	private long node_created;
	private String file_managed_file_usage_uri;

	private String intro;
	private String content;
	private String author;
	private String user;
    private String createTime;

	private String idStr;

	public String getNode_title() {
		return node_title;
	}
	public void setNode_title(String node_title) {
		this.node_title = node_title;
	}
	public int getNid() {
		return nid;
	}
	public void setNid(int nid) {
		this.nid = nid;
	}
	public long getNode_created() {
		return node_created;
	}
	public void setNode_created(long node_created) {
		this.node_created = node_created;
	}
	public String getFile_managed_file_usage_uri() {
		return file_managed_file_usage_uri;
	}
	public void setFile_managed_file_usage_uri(String file_managed_file_usage_uri) {
		this.file_managed_file_usage_uri = file_managed_file_usage_uri;
	}


	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

	public String getIdStr() {
		return idStr;
	}

	public void setIdStr(String idStr) {
		this.idStr = idStr;
	}
}
