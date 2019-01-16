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

public class UploadFileForTemplate extends GenericTransaction {
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		FileInputStream fs = null;
		try {
			if (inputParams.isNull("file.filename"))
				throw new Throwable("导入文件不能为空!");
			String filePath = inputParams.getString("file");
			Db db = getDb();
			// 获取扩展名
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
			String _timestamp = sdf.format(d);
			String fileName = MmsUtil.formatRequestEncoding(inputParams.getString("file.filename"));
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
			String insertSql = getSQL(getResource("insert_attach.sql"), inputParams);
			insertSql = StringUtil.replace(insertSql, "${pic_name}", fileName);
			insertSql = StringUtil.replace(insertSql, "${pic_path}", ftp.getFtpUploadFolder()+"mms"+File.separator+fileName);
			insertSql = StringUtil.replace(insertSql, "${pic_size}", fileSize + "");
			insertSql = StringUtil.replace(insertSql, "${pic_type}", fileType);
			String deleteAttachSql = getSQL(getResource("delete-attach.sql"), inputParams);
			// 文件上传
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

			// 校验压缩文件大小是否超过50k
			String totalSql = getSQL(getResource("query-total.sql"), inputParams);
			Recordset totalRecordset = db.get(totalSql);
			String zip_path = MmsUtil.getZipPath(totalRecordset);
			Long total = MmsUtil.getZipSize(zip_path);
			if (total > 1024*50) {
				throw new Throwable("彩信字节大小不得大于50K ");
			}
			
			Recordset _totalRecordset = new Recordset();
			_totalRecordset.append("zip_size", Types.VARCHAR);
			_totalRecordset.addNew();
			_totalRecordset.setValue("zip_size", total.toString());
			publish("_total", _totalRecordset);
		} catch (Exception e) {
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
