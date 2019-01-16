package com.ccms.util.mms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;


import com.ccms.context.InitializerServlet;
import com.ccms.util.UploadRecordToFtp;

import dinamica.Recordset;

public class MmsUtil {

	/**
	 * 解析文件路径名
	 */
	public static String formatRequestEncoding(String str) throws Throwable {
		if (null == str || "".equals(str)) {
			return "";
		}

		ServletContext context = InitializerServlet.getContext();

		String encoding = context.getInitParameter("request-encoding");
		if (encoding != null && encoding.trim().equals(""))
			encoding = null;

		if (encoding != null)
			return new String(str.getBytes("ISO8859-1"), encoding);
		else
			return str;
	}

	public static void writeToZipFile(ZipOutputStream zos, File file, String fileName) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			zos.putNextEntry(new ZipEntry(fileName));
			int len;
			while ((len = fis.read()) != -1) {
				zos.write(len);
			}
			fis.close();
			fis = null;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != fis) {
				fis.close();
			}
		}
	}
	
	public static void writeToZipFile(ZipOutputStream zos, byte[] data, String fileName) throws IOException {
		try {
			zos.putNextEntry(new ZipEntry(fileName));
			zos.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 读zip文件
	public static byte[] getZipContent(String zipPath) throws Exception {
		FileInputStream fs = null;
		byte[] content = null;
		try {
			fs = new FileInputStream(zipPath);
			content = new byte[fs.available()];
			fs.read(content, 0, content.length);
			fs.close();
			fs = null;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage(), ex);
		} finally {
			if (null != fs) {
				fs.close();
			}
		}
		return content;
	}

	public static long getZipSize(String zipPath) throws Exception {
		long size = 0;
		try {
			File file = new File(zipPath);
			size = file.length();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
		return size;
	}
	
	public static String getZipPath(Recordset fileSet) throws Throwable{
		UploadRecordToFtp ftp = new UploadRecordToFtp();
		//ftp.setFtpFileFolder("mms");
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
		String _timestamp = sdf.format(d);
		String zip_path = InitializerServlet.getContext().getInitParameter("upload-dir") + _timestamp + "_mms.zip";
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip_path));
		fileSet.top();
		while (fileSet.next()) {
			String pic_name_total = fileSet.getString("pic_name");
			MmsUtil.writeToZipFile(zos, ftp.downFileFromFTPServer(pic_name_total), pic_name_total);
		}
		zos.close();
		zos = null;
		return zip_path;
	}
}
