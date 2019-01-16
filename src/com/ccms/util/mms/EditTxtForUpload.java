package com.ccms.util.mms;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ccms.util.UploadRecordToFtp;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

public class EditTxtForUpload extends GenericTransaction {
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		try {
			Db db = getDb();
			String deleteSql = getSQL(getResource("delete-attach.sql"), inputParams);
			String insertSql = getSQL(getResource("insert_attach.sql"), inputParams);
			String content = inputParams.getString("content");
			insertSql = StringUtil.replace(insertSql, "${pic_size}", content.getBytes().length + "");
			
			UploadRecordToFtp ftp = new UploadRecordToFtp();
			//ftp.setFtpFileFolder("mms");
			// 创建文本文件
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
			String fileName = sdf.format(d) + ".txt";
			
			ftp.uploadFileToFTPServer(content.getBytes("GBK"), fileName);

			insertSql = StringUtil.replace(insertSql, "${pic_path}", ftp.getFtpUploadFolder()+"mms"+File.separator+fileName);
			insertSql = StringUtil.replace(insertSql, "${pic_name}", fileName);
			insertSql = StringUtil.replace(insertSql, "${content}", content);

			db.exec(deleteSql);
			db.exec(insertSql);
			
			String querySql = getSQL(getResource("query-upload.sql"), inputParams);
			Recordset totalRecordset = db.get(querySql);
			String zip_path = MmsUtil.getZipPath(totalRecordset);
			Long total = MmsUtil.getZipSize(zip_path);
			if (total > 1024*50) {
				throw new Throwable("彩信字节大小不得大于50K ");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return rc;
	}
}
