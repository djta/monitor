package com.ccms.util.email;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import com.ccms.util.Tools;
import com.sun.mail.pop3.POP3Folder;

public class RecvMail {

	//邮件方向
	public final static String MAIL_DIRECTION_RECV = "I";
	//接收邮件状态
	public final static String MAIL_RECV_STATE_NEW = "new";//新邮件
	
	public RecvMail() {
	}

	/**
	 * 获得邮件内容
	 * 
	 * @param part
	 * @param partList
	 * @param imageIndex
	 * @throws Exception
	 */
	public void getMailContent(Part part, String parentPartNo, ArrayList<MessagePart> attachFileList,ArrayList<MessagePart> inlineFileList, StringBuffer contentStringBuffer, 
			int imageCount, int otherFileCount, int attachFileCount, long fileID, String ftpServerIP,
			int ftpServerPort, String ftpUserName, String ftpPassword) throws Exception {
		String contenttype = part.getContentType();
		int nameindex = contenttype.indexOf("name");
		boolean conname = false;
		if (nameindex != -1) {
			conname = true;
		}

		MessagePart messagePart = null;
		// 首先判断文件是否附件
		String disposition = part.getDisposition();
		if ((disposition != null) && ((disposition.equals(Part.ATTACHMENT)) || (disposition.equals(Part.INLINE)))) {
			messagePart = new MessagePart();
			//String tempFileName=part.getFileName().replaceAll("utf-8", "gb2312");
			String strSrcImageFileName = Tools.decodeText(part.getFileName());

			// 获得本段内容
			int nFileSize = part.getSize();
			byte[] buffer = new byte[nFileSize];
			InputStream instream = part.getInputStream();
			instream.read(buffer, 0, nFileSize);
			instream.close();
			instream = null;

			messagePart.setByData(buffer);
			messagePart.setMsgSize(nFileSize);
			messagePart.setPartNo(parentPartNo);
			messagePart.setContentType(MessagePart.CONTENT_TYPE_ATTACH);
			messagePart.setFileName(strSrcImageFileName);
		} else if (part.isMimeType("text/html") && !conname) {
			messagePart = new MessagePart();
			String mailContent = (String) part.getContent();
			
			messagePart.setMsgSize(mailContent.getBytes().length);
			messagePart.setPartNo(parentPartNo);
			messagePart.setContent(mailContent);
			messagePart.setContentType(MessagePart.CONTENT_TYPE_PLAIN);
		} else if (part.isMimeType("image/*")) {
			messagePart = new MessagePart();
			String strSrcImageFileName = Tools.decodeText(part.getFileName());

			// 获得本段内容
			int nFileSize = part.getSize();
			byte[] buffer = new byte[nFileSize];
			InputStream instream = part.getInputStream();
			instream.read(buffer, 0, nFileSize);
			instream.close();
			instream = null;

			messagePart.setMsgSize(nFileSize);
			messagePart.setByData(buffer);
			messagePart.setPartNo(parentPartNo);
			messagePart.setContentType(MessagePart.CONTENT_TYPE_IMAGE);
			messagePart.setFileName(strSrcImageFileName);
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int counts = multipart.getCount();

			for (int partNo = 0; partNo < counts; partNo++) {
				Part innerPart = ((Multipart) multipart).getBodyPart(partNo);
				parentPartNo = parentPartNo + "-" + partNo;
				getMailContent(innerPart, parentPartNo, attachFileList, inlineFileList,contentStringBuffer, imageCount, otherFileCount, attachFileCount, fileID, ftpServerIP, ftpServerPort, ftpUserName, ftpPassword);
			}
			return;
		} else{
			return ;
		}

		// 将各个部分组合成统一的数据，包括将内容中的图片文件上传
		if (MessagePart.CONTENT_TYPE_PLAIN.equals(messagePart.getContentType())) {
			contentStringBuffer.append(messagePart.getContent());
		} else if (MessagePart.CONTENT_TYPE_IMAGE.equals(messagePart.getContentType())) {// 上传内容图片
			imageCount++;
			String fileExt = messagePart.getFileName().substring(messagePart.getFileName().length() - 3, messagePart.getFileName().length());
			String newFieName = MessagePart.CONTENT_TYPE_IMAGE + imageCount + "@" + messagePart.getPartNo() + "." + fileID + "." + fileExt;
			newFieName = Tools.replaceIllegalCharOfFilName(newFieName);

			// 上传文件
			messagePart.setSaveFileName(newFieName);
			saveToLocal(messagePart.getByData(), new File("").getCanonicalPath()+"\\"+newFieName);
			//Tools.uploadFileToFTPServer(messagePart.getByData(), messagePart.getByData().length, ftpFolder+newFieName, ftpServerIP, ftpServerPort, ftpUserName, ftpPassword, null);
			inlineFileList.add(messagePart);
		} else if (MessagePart.CONTENT_TYPE_ATTACH.equals(messagePart.getContentType())) {
			// 附件
			attachFileCount++;
			String fileExt = messagePart.getFileName().substring(messagePart.getFileName().lastIndexOf(".")+1, messagePart.getFileName().length());
			String newFieName = MessagePart.CONTENT_TYPE_ATTACH + attachFileCount + "@" + messagePart.getPartNo() + "." + fileID + "." + fileExt;
			newFieName = Tools.replaceIllegalCharOfFilName(newFieName);

			// 上传文件
			messagePart.setSaveFileName(newFieName);
			//保存附件在本地
			
			saveToLocal(messagePart.getByData(), new File("").getCanonicalPath()+"\\"+newFieName);
//			TestMail.uploadFiles(messagePart.getByData(), messagePart.getByData().length, "D:\\mail");
//			
//			Tools.uploadFileToFTPServer(messagePart.getByData(), messagePart.getByData().length, ftpFolder+newFieName, ftpServerIP, ftpServerPort, ftpUserName, ftpPassword, null);
			attachFileList.add(messagePart);
		} else {
			return ;
		}
	}
		

	/**
	 * 接收邮件，并将附件存储在指定的ftp服务器上
	 * 
	 * @param pop3Host
	 * @param username
	 * @param password
	 * @param projectID
	 * @param ftpSeverIP
	 * @param ftpServerPort
	 * @param ftpUserName
	 * @param ftpPassword
	 * @return Set<MailRecordBean> 邮件记录
	 */
	public Set<MailRecordBean> recvMail(String accountID, String pop3Host, int pop3Port, String username, String password, int overTimeLen, String bakTimeLen,
													String ftpServerIP, int ftpServerPort, String ftpUserName, String ftpPassword) {
		Store store = null;
		POP3Folder inbox = null;
		Set<MailRecordBean> recordSet = null;
		try {
			System.out.println(username+"  is start receive mail!");
			// 1.连接邮件服务器
			Properties prop = new Properties();
			prop.put("mail.pop3.host", pop3Host);
			prop.put("mail.pop3.port", String.valueOf(pop3Port));
			prop.put("mail.pop3.connectiontimeout", String.valueOf(overTimeLen * 60 * 1000));
			Session session = Session.getDefaultInstance(prop);
			store = session.getStore("pop3");
			store.connect(pop3Host, username, password);

			// 2.获得已经接收到的邮件列表
			inbox = (POP3Folder) store.getDefaultFolder().getFolder("INBOX");
			inbox.open(Folder.READ_WRITE);
			Message[] mailMsg = inbox.getMessages();

			FetchProfile profile = new FetchProfile();
			profile.add(FetchProfile.Item.ENVELOPE);
			inbox.fetch(mailMsg, profile);
			
			//存储接收邮件
			recordSet = new HashSet<MailRecordBean>();
			// 3.判断邮件类型并处理邮件
			for (int nMsgIdx = 0; nMsgIdx < mailMsg.length; nMsgIdx++) {
				boolean bRecvFlag=true;
				try {
					Object out_content = mailMsg[nMsgIdx].getContent();
					if (out_content == null) {
						continue;
					}

					// 查询该邮件是否已经被接收
					String mailID = inbox.getUID(mailMsg[nMsgIdx]);
					mailID = mailID.replace("+", "");
					mailID = Tools.replaceIllegalCharOfFilName(mailID);

					// 获得邮件基本信息
					Date sendDate = mailMsg[nMsgIdx].getSentDate();
					String from = Tools.decodeText(mailMsg[nMsgIdx].getFrom()[0].toString());

					InternetAddress ia = new InternetAddress(from);
					String senderName = ia.getPersonal();
					if (null == senderName) {
						senderName = "";
					}
					String senderAddress = ia.getAddress();
					String title = mailMsg[nMsgIdx].getSubject();

					// 单封邮件变量定义
					String inlineFileNames = "";
					String mailContent = "";
					StringBuffer contentStringBuffer = new StringBuffer(4000);

					int attachFileCount = 0;
					int imageCount = 0;
					int otherFileCount = 0;
					long mailSize = 0;
					Set<MailFileBean> fileSet = new HashSet<MailFileBean>();

					// 临时文件名
					long fileID = new Date().getTime();

					if (out_content instanceof Multipart) {
						Multipart mp = (Multipart) mailMsg[nMsgIdx].getContent();

						ArrayList<MessagePart> attachFileList = new ArrayList<MessagePart>();
						ArrayList<MessagePart> inlineFileList = new ArrayList<MessagePart>();

						int nStartPartIdx = 0;
						/*
						 * if (mp.getCount() >= 2) { nStartPartIdx = 1; }
						 */

						// 逐段获取邮件内容
						for (int nPartIdx = nStartPartIdx; nPartIdx < mp.getCount(); nPartIdx++) {
							Part part = ((Multipart) out_content).getBodyPart(nPartIdx);
							getMailContent(part, String.valueOf(nPartIdx), attachFileList, inlineFileList, contentStringBuffer, imageCount, otherFileCount, attachFileCount, fileID, ftpServerIP, ftpServerPort, ftpUserName, ftpPassword);
						}

						// 邮件内容
						mailSize += contentStringBuffer.length();
						mailContent = contentStringBuffer.toString();
						contentStringBuffer = null;

						// 附件
						for (int attachIdx = 0; attachIdx < attachFileList.size(); attachIdx++) {
							MessagePart msgPart = (MessagePart) attachFileList.get(attachIdx);
							mailSize += msgPart.getMsgSize();
							MailFileBean fileBean = new MailFileBean();
							fileBean.setMailID(mailID);
							fileBean.setSaveFileName(new File("").getCanonicalPath()+"\\"+msgPart.getSaveFileName());
							fileBean.setFileName(msgPart.getFileName());
							fileBean.setFileType(msgPart.getContentType());
							fileSet.add(fileBean);
						}
						attachFileList.clear();

						// 内容中的图片
						String strTmp = "", strRemain = "";
						for (int inlineFileIdx = 0; inlineFileIdx < inlineFileList.size(); inlineFileIdx++) {
							MessagePart msgPart = (MessagePart) inlineFileList.get(inlineFileIdx);
							mailSize += msgPart.getMsgSize();

							inlineFileNames = inlineFileNames + msgPart.getSaveFileName() + ";";

							// 替换内容中的图片文件占位符（特殊字符占位，当读邮件时替换为http，显示图片）
							String searchTag = "cid:" + msgPart.getFileName();
							String imagSrc = "../temp/" + msgPart.getSaveFileName();
							int nCIDIdx = mailContent.indexOf(searchTag);
							if(nCIDIdx >= 0){
								strTmp = mailContent.substring(0, nCIDIdx);
								strRemain = mailContent.substring(nCIDIdx);
	
								int endIndex = strRemain.indexOf("\"");
								if (endIndex <= 0) {
									endIndex = strRemain.indexOf(">");
								}
								if (endIndex > 0) {
									strTmp = strTmp + imagSrc + strRemain.substring(endIndex);
								}
								mailContent = strTmp;
							}
						}
						inlineFileList.clear();
					} else {
						InputStream is = mailMsg[nMsgIdx].getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(is));
						String thisLine = reader.readLine();
						while (thisLine != null) {
							contentStringBuffer.append("\n").append(thisLine);
							thisLine = reader.readLine();
						}
						mailSize += contentStringBuffer.length();

						mailContent = contentStringBuffer.toString();
						contentStringBuffer = null;
					}

					// 向任务表入EMIAL接收任务
					MailRecordBean mailRecord = new MailRecordBean();
					mailRecord.setMailSize(mailSize);
					mailRecord.setMailID(mailID);
					mailRecord.setAccountID(accountID);
					mailRecord.setMailStatus(MAIL_RECV_STATE_NEW);
					mailRecord.setSendTime(sendDate);
					mailRecord.setSenderName(senderName);
					mailRecord.setSender(senderAddress);
					mailRecord.setReceiver(username);
					mailRecord.setMailSubject(title);
					mailRecord.setMailBody(mailContent);
					mailRecord.setRecvTime(new Date());
					mailRecord.setMailFiles(fileSet);
					recordSet.add(mailRecord);
				} catch (Exception ex) {
					bRecvFlag=false;
					ex.printStackTrace();
				}
				
				if(bRecvFlag){
					// 删除已成功接收的邮件
					if ("0".equals(bakTimeLen)) {
						mailMsg[nMsgIdx].setFlag(Flags.Flag.DELETED, true);
					}
				}
			} 
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (inbox != null) {
					inbox.close(true);
				}
				if (store != null) {
					store.close();
				}
			} catch (Exception ex1) {
				ex1.printStackTrace();
			}
		}
		return recordSet;
	}
	
	public static boolean saveToLocal(byte[] srcFileData, String destFileName){
		try {
			FileOutputStream fos = new FileOutputStream(new File(destFileName));
			fos.write(srcFileData);
			fos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
}
