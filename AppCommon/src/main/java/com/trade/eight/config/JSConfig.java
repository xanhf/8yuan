package com.trade.eight.config;

public class JSConfig {
	String mColor = "#343c48";


	public static final String base = "http://api.wallstreetcn.com/";
	
	public static final String javascript = "<script type='text/javascript'>" 
												+ "function intentTo(imageUrl) {"
												+ "window.jsObj.fullImage(imageUrl) ;"
												+ "}"
											+ "</script>";
	
	
	public static final String img = "<style type='text/css'> img{ "
			+ "border:0;"
			+ "margin:0;"
			// + "padding:20;"
			+ "max-width:100%;width:100%;text-align:center;    vertical-align:middle"
			+ "overflow:hidden;}" + "body{" + "padding:5;" + "font-size:16px;"
			+ " font-family:'微软雅黑';font-style: normal;" + "color:#343c48;}"
			+ "a:link {text-decoration:none;color:black;}\n"
			+ "a:hover {text-decoration:none;color:red;}\n"
			+ "a:active {text-decoration:none;color:red;}\n"
			+ "a:visited {text-decoration:none;color:gray;}" + "</style>";
	
	
	public static final String imgBig = "<style type='text/css'> img{ "
			+ "border:0;"
			+ "margin:0;"
			// + "padding:20;"
			+ "max-width:100%;width:100%;text-align:center;    vertical-align:middle"
			+ "overflow:hidden;}" + "body{" + "padding:5;" + "font-size:18px;"
			+ " font-family:'微软雅黑';font-style: normal;" + " color:#343c48;}"
			+ "a:link {text-decoration:none;color:black;}\n"
			+ "a:hover {text-decoration:none;color:red;}\n"
			+ "a:active {text-decoration:none;color:red;}\n"
			+ "a:visited {text-decoration:none;color:gray;}" + "</style>";
	public static final String url_news_link = "http://wallstreetcn.com/node/";
	public static final String url_live_link = "http://live.wallstreetcn.com/node/";

}
