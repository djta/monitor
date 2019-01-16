package com.ccms.util.mms;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.ccms.util.UploadRecordToFtp;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

public class UploadFileForSend extends GenericTransaction {
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		FileInputStream fs = null;
		try {
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
			String _timestamp = sdf.format(d);
			if (inputParams.isNull("file.filename"))
				throw new Throwable("导入文件不能为空!");
			String filePath = inputParams.getString("file");
			String fileName = MmsUtil.formatRequestEncoding(inputParams.getString("file.filename"));
			// 获取扩展名
			String extendName = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
			fileName = _timestamp + "." + extendName;
			String fileType = "";
			HashMap<String, String> typeMap = new HashMap<String, String>();
			typeMap.put("jpg", "img");
			typeMap.put("gif", "img");
			typeMap.put("txt", "txt");
			typeMap.put("mid", "mp3");
			typeMap.put("mp3", "mp3");
			typeMap.put("wav", "mp3");
			typeMap.put("arm", "mp3");
			if (typeMap.containsKey(extendName)) {
				fileType = typeMap.get(extendName);
			} else {
				throw new Throwable("文件类型不正确");
			}
			
			// 保存文件
			UploadRecordToFtp ftp = new UploadRecordToFtp();
			//ftp.setFtpFileFolder("mms");
			
			File file = new File(filePath);
			long fileSize = file.length();
			Db db = getDb();
			String deleteAttachSql = getSQL(getResource("delete-attach.sql"), inputParams);
			String insertSql = getResource("insert_attach.sql");
			insertSql = StringUtil.replace(insertSql, "${pic_size}", fileSize + "");
			insertSql = StringUtil.replace(insertSql, "${pic_path}", ftp.getFtpUploadFolder()+"mms"+File.separator+fileName);
			insertSql = StringUtil.replace(insertSql, "${pic_name}", fileName);
			insertSql = StringUtil.replace(insertSql, "${pic_type}", fileType);
			insertSql = getSQL(insertSql, inputParams);
			
			if ("txt".equals(fileType)) {
				String tempString = "";
				StringBuffer contentBuffer = new StringBuffer();
				BufferedReader bfs = new BufferedReader(new FileReader(file));
				while ((tempString = bfs.readLine()) != null) {
					contentBuffer.append(tempString);
				}
				bfs.close();
				bfs = null;
				if (contentBuffer.length() == 0) {
					throw new Throwable("文件内容不能为空");
				}
				insertSql = StringUtil.replace(insertSql, "${content}", "'" + StringUtil.replace(contentBuffer.toString(), "'", "''") + "'");
				ftp.uploadFileToFTPServer(contentBuffer.toString().getBytes("GBK"), fileName);
			} else {
				byte[] buff = new byte[1024*20];
				int count = 0;
				fs = new FileInputStream(file);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				while ((count=fs.read(buff)) != -1) {
					bos.write(buff,0,count);
				}
				fs.close();
				fs = null;
				ftp.uploadFileToFTPServer(bos.toByteArray(), fileName);
				insertSql = StringUtil.replace(insertSql, "${content}", null);
			}

			db.exec(deleteAttachSql);
			db.exec(insertSql);
			
			// 没通过模板进行加载，则返回task附件
			Recordset newRecordset = new Recordset();
			newRecordset.append("pic_name", Types.VARCHAR);
			newRecordset.append("pic_path", Types.VARCHAR);
			newRecordset.append("pic_type", Types.VARCHAR);
			newRecordset.append("position", Types.VARCHAR);
			newRecordset.append("content", Types.VARCHAR);
			newRecordset.append("page", Types.VARCHAR);

			String querySql = getSQL(getResource("query-upload.sql"), inputParams);
			Recordset totalRecordset = db.get(querySql);
			String zip_path = MmsUtil.getZipPath(totalRecordset);
			Long total = MmsUtil.getZipSize(zip_path);
			if (total > 1024*50) {
				throw new Throwable("彩信字节大小不得大于50K ");
			}
		} catch (Throwable e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (null != fs) {
				fs.close();
			}
		}
		return rc;
	}

}
