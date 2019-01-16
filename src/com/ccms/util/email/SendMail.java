package com.ccms.util.email;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

public class SendMail {

	private final static String  COMMON_FILE_SPLIT = ";";
	
	private MimeMessage mimeMsg; // MIME邮件对象

	private Session session; // 邮件会话对象

	private Properties props; // 系统属性
	private String username = ""; // smtp认证用户名和密码

	private String password = "";

	private Multipart mp; // Multipart对象,邮件内容,标题,附件等内容均添加到其中后再生成MimeMessage对象

	public SendMail(String smtp) {
		setSmtpHost(smtp);
		createMimeMessage();
	}

	/**
	 * @param hostName
	 *  String
	 */
	public void setSmtpHost(String hostName) {
		if (props == null)
			props = System.getProperties(); // 获得系统属性对象
		props.put("mail.smtp.host", hostName); // 设置SMTP主机
	}
	
	/**
	 * @param port
	 *  String
	 */
	public void setSmtpPort(String port){
		if (props == null)
			props = System.getProperties(); // 获得系统属性对象
		props.put("mail.smtp.port", port); // 设置SMTP 端口
	}
	
	/**
	 * @return boolean
	 */
	public boolean createMimeMessage() {
		try {
			session = Session.getDefaultInstance(props, null); // 获得邮件会话对象
		} catch (Exception e) {
			System.err.println("getDefaultInstance error！" + e);
			return false;
		}

		try {
			mimeMsg = new MimeMessage(session); // 创建MIME邮件对象
			mp = new MimeMultipart();
			return true;
		} catch (Exception e) {
			System.err.println("MimeMessage！" + e);
			return false;
		}
	}

	/**
	 * @param need
	 *            boolean
	 */
	public void setNeedAuth(boolean need) {
		if (props == null)
			props = System.getProperties();

		if (need) {
			props.put("mail.smtp.auth", "true");
		} else {
			props.put("mail.smtp.auth", "false");
		}
	}

	/**
	 * @param name
	 *            String
	 * @param pass
	 *            String
	 */
	public void setNamePass(String name, String pass) {
		username = name;
		password = pass;
	}

	/**
	 * @param mailSubject
	 *            String
	 * @return boolean
	 */
	public boolean setSubject(String mailSubject) {
		try {
			mimeMsg.setSubject(MimeUtility.encodeText(mailSubject,"GB2312","B")); 
			return true;
		} catch (Exception e) {
			System.err.println("setSubject error！");
			return false;
		}
	}

	/**
	 * @param mailBody
	 *            String
	 */
	public boolean setBody(String mailBody) {
		try {
			BodyPart bp = new MimeBodyPart();
			bp.setContent(mailBody, "text/html;charset=GB2312");
			mp.addBodyPart(bp);

			return true;
		} catch (Exception e) {
			System.err.println("setBody！" + e);
			return false;
		}
	}

	/**
	 * 增加附件
	 * @param srcFilename
	 * @param realFilaName
	 * @param fileType
	 * @return
	 */
	public boolean addFileAffix(String srcFilename,byte[] fileData,String fileType) {
		try {
			BodyPart bp = new MimeBodyPart();
			DataHandler dh = new DataHandler(new ByteArrayDataSource(fileData,fileType));
			bp.setDataHandler(dh);
			bp.setFileName(MimeUtility.encodeText(srcFilename, "GB2312", "B"));
			mp.addBodyPart(bp);

			return true;
		} catch (Exception e) {
			System.err.println("addFileAffix：" + srcFilename + "error！" + e);
			return false;
		}
	}

	/**
	 * @param name
	 *            String
	 * @param pass
	 *            String
	 */
	public boolean setFrom(String from) {
		try {
			mimeMsg.setFrom(new InternetAddress(from)); // 设置发信人
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param name
	 *            String
	 * @param pass
	 *            String
	 */
	public boolean setTo(String to) {
		if (to == null || to.length() == 0)
			return false;
		try {
			String lastChar=to.substring(to.length()-1, to.length());
			if(COMMON_FILE_SPLIT.equals(lastChar))
			{
				to=to.substring(0,to.length()-1);
			}
			String[]   arrReceiver   =   to.split(COMMON_FILE_SPLIT);
			InternetAddress[]   address=new   InternetAddress[arrReceiver.length];   
			for   (int   i=0;i<arrReceiver.length;i++){   
				address[i]=new   InternetAddress(arrReceiver[i]);   
			}   
			mimeMsg.setRecipients(Message.RecipientType.TO, address);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param name
	 *            String
	 * @param pass
	 *            String
	 */
	public boolean setCopyTo(String copyto) {
		if (copyto == null || copyto.length() == 0)
			return false;
		try {
			String lastChar=copyto.substring(copyto.length()-1, copyto.length());
			if(COMMON_FILE_SPLIT.equals(lastChar))
			{
				copyto=copyto.substring(0,copyto.length()-1);
			}
			String[]   arrReceiver   =   copyto.split(COMMON_FILE_SPLIT);
			InternetAddress[]   address=new   InternetAddress[arrReceiver.length];   
			for   (int   i=0;i<arrReceiver.length;i++){   
				address[i]=new   InternetAddress(arrReceiver[i]);   
			}
			mimeMsg.setRecipients(Message.RecipientType.CC, address);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * @param name
	 *            String
	 * @param pass
	 *            String
	 */
	public boolean sendout() {
		Transport transport=null;
		boolean bClosed=true;
		try {
			mimeMsg.setContent(mp);
			mimeMsg.saveChanges();
			System.out.println("正在发送邮件....");

			Session mailSession = Session.getInstance(props, null);
			bClosed=false;
			transport = mailSession.getTransport("smtp");
			transport.connect((String) props.get("mail.smtp.host"), username, password);
			transport.sendMessage(mimeMsg, mimeMsg.getAllRecipients());

			System.out.println("发送邮件成功！");
			transport.close();
			bClosed=true;

			return true;
		} catch (Exception e) {
			System.err.println("邮件发送失败！" + e);
			try
			{
				if(!bClosed)
				{	
					transport.close();
					transport=null;
				}
			}
			catch(Exception exinner)
			{
				;
			}
			return false;
		}
	}
}
