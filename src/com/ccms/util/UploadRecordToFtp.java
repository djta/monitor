package com.ccms.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;

import org.apache.commons.net.ftp.FTPClient;

import com.ccms.context.InitializerServlet;

public class UploadRecordToFtp {

	private String ftpServer = null;
	private int ftpPort = 21;
	private String ftpUser = null;
	private String ftpPasswd = null;
	private String ftpFileFolder = null;
	private String ftpUploadFolder = null;
	
	public String getFtpServer() {
		return ftpServer;
	}
	public void setFtpServer(String ftpServer) {
		this.ftpServer = ftpServer;
	}
	public int getFtpPort() {
		return ftpPort;
	}
	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}
	public String getFtpUser() {
		return ftpUser;
	}
	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}
	public String getFtpPasswd() {
		return ftpPasswd;
	}
	public void setFtpPasswd(String ftpPasswd) {
		this.ftpPasswd = ftpPasswd;
	}
	public String getFtpFileFolder() {
		return ftpFileFolder;
	}
	public void setFtpFileFolder(String ftpFileFolder) {
		this.ftpFileFolder = ftpFileFolder;
	}
	public String getFtpUploadFolder() {
		return ftpUploadFolder;
	}
	public void setFtpUploadFolder(String ftpUploadFolder) {
		this.ftpUploadFolder = ftpUploadFolder;
	}
	
	private void init(){
		ServletContext context = InitializerServlet.getContext();
		this.ftpServer = context.getInitParameter("ftp-server");
		this.ftpPort = Integer.parseInt(context.getInitParameter("ftp-port"));
		this.ftpUser = context.getInitParameter("ftp-user");
		this.ftpPasswd = context.getInitParameter("ftp-passwd");
		this.ftpFileFolder = context.getInitParameter("ftp-folder");
		this.ftpUploadFolder = context.getInitParameter("ftp-upload");
	}
	
	public UploadRecordToFtp(){
		//初始化参数
		init();
	}
	
	/**
	 * 将文件数据上传到FTP服务器
	 * 
	 * @throws Exception
	 */
	public boolean uploadFileToFTPServer(byte[] srcFileData, String destFileName) {

		FTPClient ftpClient = null;
		boolean bTransferFlag = false;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(this.ftpServer, this.ftpPort);// 参数为ftp服务器IP和端口
			
			ftpClient.login(this.ftpUser, this.ftpPasswd);// 参数为用户名和密码
			ftpClient.changeWorkingDirectory(this.ftpFileFolder==null?"/":this.ftpFileFolder);
			ftpClient.enterLocalPassiveMode();
            ftpClient.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE); 
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			
			OutputStream os = ftpClient.storeFileStream(destFileName);
			os.write(srcFileData, 0, srcFileData.length);
			os.close();
			ftpClient.logout();
			ftpClient.disconnect();
			bTransferFlag = true;
		}catch (Throwable ex) {
			ex.printStackTrace();
		}
		return bTransferFlag;
	}
	
	/**
	 * 根据文件名称下载文件
	 * @param destFileName
	 * @return
	 * @throws Exception
	 */
	public byte[] downFileFromFTPServer(String destFileName) throws Exception {

		FTPClient ftpClient = null;
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(this.ftpServer, this.ftpPort);// 参数为ftp服务器IP和端口
			
			ftpClient.login(this.ftpUser, this.ftpPasswd);// 参数为用户名和密码
			ftpClient.changeWorkingDirectory(this.ftpFileFolder==null?"/":this.ftpFileFolder);
			
			ftpClient.enterLocalPassiveMode();
            ftpClient.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE); 
			ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
			
			InputStream ftpIn = ftpClient.retrieveFileStream(destFileName);
			byte[] buf = new byte[1024 * 1024];
			int nRead = 0;
			int nStartPos=0;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			while ((nRead = ftpIn.read(buf)) != -1) {
				bos.write(buf, 0, nRead);
				nStartPos += nRead;
			}
			ftpIn.close();
			ftpClient.logout();
			ftpClient.disconnect();
			return bos.toByteArray();
		} catch (Exception ex) {
			throw ex;
		}
	}
}
