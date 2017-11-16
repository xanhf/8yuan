package com.trade.eight.entity.trude;

public abstract class AbstractApiResponse {
	private String code;
	private String msg;
	private String uuid ;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public Boolean isSuccess(){
		return code!=null&&(code.equals("00")||code.equals("0"));
	}
	
}