package com.ccms.util.email;

/**
 * 
 * @author shirely gong
 * @create date:2010-8-22
 * @last modify:2010-8-22 at hopecool
 */

public class MessagePart {
	public static final String CONTENT_TYPE_PLAIN = "plain";
	public static final String CONTENT_TYPE_IMAGE = "image";
	public static final String CONTENT_TYPE_OTHER = "other";
	public static final String CONTENT_TYPE_ATTACH = "attach";

	private String partNo = null;
	private String contentType = null;
	private String fileName = null;
	private String content = null;
	private byte[]   byData=null;
	
	private String saveFileName = null;
	private int		 msgSize=0;

	public String getSaveFileName() {
		return saveFileName;
	}

	public void setSaveFileName(String saveFileName) {
		this.saveFileName = saveFileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPartNo() {
		return partNo;
	}

	public void setPartNo(String partNo) {
		this.partNo = partNo;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public byte[] getByData() {
		return byData;
	}

	public void setByData(byte[] byData) {
		this.byData = byData;
	}

	public int getMsgSize() {
		return msgSize;
	}

	public void setMsgSize(int msgSize) {
		this.msgSize = msgSize;
	}
}
