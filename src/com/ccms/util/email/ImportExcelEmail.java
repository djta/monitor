package com.ccms.util.email;

import java.io.File;
import java.io.FileInputStream;

import com.ccms.util.UploadRecordToFtp;

import dinamica.Config;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class ImportExcelEmail extends GenericTableManager {
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);

		if (inputParams.isNull("file.filename"))
			throw new Throwable("file.filename is null!");
		String sql = getResource("insert-attach_files.sql");
		
		// 获取要上传的文件信息
		String fileName = inputParams.getString("file.filename");
		fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
		fileName = formatRequestEncoding(fileName);
		inputParams.setValue("file.filename", fileName);
		String filePah = inputParams.getString("file");
		String fileType = inputParams.getString("file.content-type");
		String mail_id = inputParams.getString("mail_id");
		//邮件主键加上文件名，为了去重
		String deskFileName = mail_id+"_"+fileName;
		// 上传文件到FTP服务器
		String trueFileName = uploadFiles(filePah, deskFileName);
		if(trueFileName == null){
			throw new Throwable("上传文件失败！");
		}
		// 替换字符，拼接sql
		sql = StringUtil.replace(sql, "${seq_email_attach_files}","NEXTVAL('seq_email_attach_files')");
		sql = StringUtil.replace(sql, "${file.filename}", "'" + fileName + "'");
		sql = StringUtil.replace(sql, "${file}", "'" + trueFileName + "'");
		sql = StringUtil.replace(sql, "${file_type}", "'" + fileType + "'");
		sql = StringUtil.replace(sql, "${mail_id}", mail_id);
		Db db = getDb();
		db.exec(sql);
		return rc;

	}

	/**
	 * 解析文件路径名
	 */
	public String formatRequestEncoding(String str) throws Throwable {

		Config _config = getConfig();

		String encoding = getContext().getInitParameter("request-encoding");
		if (encoding != null && encoding.trim().equals(""))
			encoding = null;

		if (_config.requestEncoding != null)
			return new String(str.getBytes("ISO8859-1"),
					_config.requestEncoding);
		else if (encoding != null)
			return new String(str.getBytes("ISO8859-1"), encoding);
		else
			return str;
	}

	/**
	 * 向服务器上传文件 
	 * srcFile 源文件路径
	 * deskFileName 目的文件名
	 * 
	 * @return 返回FTP服务器的文件地址
	 * 
	 * @throws Exception
	 */
	public String uploadFiles(String srcFile, String deskFileName) throws Throwable {
		String trueFileName = null;
		FileInputStream in = null;
		try {
			File file = new File(srcFile);
			in = new FileInputStream(file);
			int fileLength = new Long(file.length()).intValue();
			byte[] fileData = new byte[fileLength];
			byte[] b = new byte[1024]; 
			//写入指定的文件中 
			int nRead=0;
			int nStartPos=0; 
			while ((nRead = in.read(b, 0, 1024)) > 0 && nStartPos < fileLength ) { 
				for(int j=0;j<nRead;j++){
					fileData[nStartPos+j] = b[j];
				}
				nStartPos += nRead;
			}
			//关闭流
			in.close();
			
			UploadRecordToFtp uploadFtp = new UploadRecordToFtp();
			boolean flag = uploadFtp.uploadFileToFTPServer(fileData, deskFileName);
			if(flag == true){//上传成功
				trueFileName = uploadFtp.getFtpUploadFolder()+deskFileName;
			}
		} catch (Throwable e) {
			throw new Throwable("文件读写错误");
		}
		return trueFileName;
	}
}
