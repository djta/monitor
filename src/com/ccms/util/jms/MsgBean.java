package com.ccms.util.jms;

import java.io.Serializable;

public class MsgBean implements Serializable {

	private static final long serialVersionUID = 3409861338947096317L;

	private String msg_title = null;
	private String msg_content = null;
	private String sender = null;
	private String send_date = null;
	
	public String getMsg_title() {
		return msg_title;
	}
	public void setMsg_title(String msgTitle) {
		msg_title = msgTitle;
	}
	public String getMsg_content() {
		return msg_content;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSend_date() {
		return send_date;
	}
	public void setSend_date(String sendDate) {
		send_date = sendDate;
	}
	public void setMsg_content(String msgContent) {
		msg_content = msgContent;
	}
}
