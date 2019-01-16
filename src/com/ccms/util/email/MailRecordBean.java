package com.ccms.util.email;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class MailRecordBean implements Serializable,Cloneable{
	
	private static final long serialVersionUID = 891230702465907364L;
	private String 	accountID=null;
	private String 	mailID=null;
	private String 	sender=null;
	private String 	senderName=null;
	private Date 	sendTime=null;
	private Date 	recvTime=null;
	private String 	receiver=null;
	private String 	cc=null;
	private String 	bcc=null;
	private String 	mailSubject=null;
	private String 	mailBody=null;
	private Long 	mailSize=null;
	private String 	mailStatus=null;
	
	private Set<MailFileBean> mailFiles=null;
	
	public String getAccountID() {
		return accountID;
	}
	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}
	public String getMailID() {
		return mailID;
	}
	public void setMailID(String mailID) {
		this.mailID = mailID;
	}
	public String getSender() {
		return sender;
	}
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}
	public Date getRecvTime() {
		return recvTime;
	}
	public void setRecvTime(Date recvTime) {
		this.recvTime = recvTime;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getBcc() {
		return bcc;
	}
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
	public String getMailSubject() {
		return mailSubject;
	}
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
	
	public String getMailBody() {
		return mailBody;
	}
	public void setMailBody(String mailBody) {
		this.mailBody = mailBody;
	}
	public Long getMailSize() {
		return mailSize;
	}
	public void setMailSize(Long mailSize) {
		this.mailSize = mailSize;
	}
	public String getMailStatus() {
		return mailStatus;
	}
	public void setMailStatus(String mailStatus) {
		this.mailStatus = mailStatus;
	}
	public Set<MailFileBean> getMailFiles() {
		return mailFiles;
	}
	public void setMailFiles(Set<MailFileBean> mailFiles) {
		this.mailFiles = mailFiles;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mailID == null) ? 0 : mailID.hashCode());
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
		MailRecordBean other = (MailRecordBean) obj;
		if (mailID == null) {
			if (other.mailID != null)
				return false;
		} else if (!mailID.equals(other.mailID))
			return false;
		return true;
	}
}
