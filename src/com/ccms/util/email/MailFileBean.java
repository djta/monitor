package com.ccms.util.email;

import java.io.Serializable;

public class MailFileBean implements Serializable,Cloneable{
	
	private static final long serialVersionUID = 7213783408127621084L;
	private String mailID=null;
	private String saveFileName=null;
	private String fileName=null;
	private String fileType=null;
	
	public String getMailID() {
		return mailID;
	}
	public void setMailID(String mailID) {
		this.mailID = mailID;
	}
	public String getSaveFileName() {
		return saveFileName;
	}
	public void setSaveFileName(String saveFileName) {
		this.saveFileName = saveFileName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
}
