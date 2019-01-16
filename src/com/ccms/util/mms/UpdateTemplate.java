package com.ccms.util.mms;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.tools.zip.ZipOutputStream;


import com.ccms.context.InitializerServlet;
import com.ccms.util.UploadRecordToFtp;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

public class UpdateTemplate extends GenericTransaction {
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		ZipOutputStream zos = null;
		try {
			Db db = getDb();

			String update = getResource("update.sql");
			
			String queryAttachs = getSQL(getResource("query-attachs.sql"), inputParams);
			Recordset queryRecordset = db.get(queryAttachs);
			
			//模板
			String templateXml = getResource("/WEB-INF/action/util/mms/send/insert/template.xml");
			
			// 打包
			UploadRecordToFtp ftp = new UploadRecordToFtp();
			//ftp.setFtpFileFolder("mms");
			HashMap<String, String> parsMap = new HashMap<String, String>();
			
			Date d = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
			String _timestamp = sdf.format(d);
			String zip_path = InitializerServlet.getContext().getInitParameter("upload-dir") + _timestamp + "_mms.zip";
			zos = new ZipOutputStream(new FileOutputStream(zip_path));

			queryRecordset.top();
			ArrayList<String> readyStore = new ArrayList<String>();
			ArrayList<String> pageStore = new ArrayList<String>();
			while (queryRecordset.next()) {
				String pic_name = queryRecordset.getString("pic_name");
				String pic_type = queryRecordset.getString("pic_type");
				String page = queryRecordset.getString("page");
				String tempStr = "";
				if (!pageStore.contains(page)) {
					pageStore.add(page);
				}
				if (parsMap.containsKey(page)) {
					tempStr = parsMap.get(page);
				}
				// 拼接彩信配置文件smil
				if ("txt".endsWith(pic_type)) {
					tempStr = tempStr + "<text src=\"" + pic_name + "\" region=\"Text\" />";
				} else if ("img".endsWith(pic_type)) {
					tempStr = tempStr + "<img src=\"" + pic_name + "\" region=\"Image\" />";
				} else {
					tempStr = tempStr + "<audio src=\"" + pic_name + "\"/>";
				}
				parsMap.put(page, tempStr);
				// 禁止重复打包相同文件
				if (!readyStore.contains(pic_name)) {
					MmsUtil.writeToZipFile(zos, ftp.downFileFromFTPServer(pic_name), pic_name);
				}
				readyStore.add(pic_name);
			}
			String pars = "";
			for (int i = 0; i < pageStore.size(); i++) {
				String key = parsMap.get(pageStore.get(i));
				pars = pars + "<par dur=\"50000ms\">" + key + "</par>";
			}

			// 将发送的配置文件压缩到包中
			templateXml = StringUtil.replace(templateXml, "${pars}", pars);
			MmsUtil.writeToZipFile(zos, templateXml.getBytes(), _timestamp + "_sm.smil");
			zos.setEncoding("gbk");
			zos.close();
			zos = null;

			//上传到FTP
			byte[] zipContent = MmsUtil.getZipContent(zip_path);
			String fileName = _timestamp + "_mms.zip";
			String real_zip_path = ftp.getFtpUploadFolder()+"mms"+File.separator+fileName;
			ftp.uploadFileToFTPServer(zipContent, fileName);
			
			inputParams.setValue("zip_path", real_zip_path);
			inputParams.setValue("zip_size", zipContent.length+"");
			
			//更新到库
			update = getSQL(update,inputParams);
			db.exec(update);
			
		} catch (Throwable e) {
			e.printStackTrace();
			throw new Exception(e.getMessage(), e);
		} finally {
			if (null != zos) {
				zos.close();
			}
		}
		return rc;
	}
}
