package com.ccms.util.mms;

import javax.servlet.ServletOutputStream;

import com.ccms.util.UploadRecordToFtp;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class ShouPicFromFtp  extends GenericOutput{

	public void print(GenericTransaction data) throws Throwable {
		Recordset _request = data.getRecordset("_request");
		UploadRecordToFtp ftp = new UploadRecordToFtp();
		//ftp.setFtpFileFolder("mms");
		String picName = _request.getString("pic_name");
		byte[] picData = ftp.downFileFromFTPServer(picName);
		getResponse().setContentType("image/png");
		getResponse().setHeader("Cache-Control","no-cache");
		getResponse().setHeader("Pragma","no-cache");
		getResponse().setContentLength(picData.length);
		ServletOutputStream out = getResponse().getOutputStream();
		out.write(picData);
		out.close();
	}
}
