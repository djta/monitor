package com.ccms.util.attachment;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.xml.Document;

public class ViewAttachFile  extends GenericOutput{
	public void print(GenericTransaction data) throws Throwable {
		
		Document doc = getConfig().getDocument();
		String file_recordset = doc.getElement("file-recordset").getValue();
		
		BufferedInputStream in =null ;
		ByteArrayOutputStream buf = null;
		try{
			Recordset rsFile = data.getRecordset(file_recordset);
			rsFile.first();
			String fileName = rsFile.getString("file_name");
			String filePath = rsFile.getString("file_path");
			String fileType = rsFile.getString("file_type");
			
			File file = new File(filePath);
			if (!file.exists() || file.isDirectory())
				throw new FileNotFoundException();
			
			byte[] temp = new byte[1024 * 1024];
			int size = -1;
			int nStartPos = 0;
			int fileLength = new Long(file.length()).intValue();
			in=new BufferedInputStream(new FileInputStream(file));
			buf=new ByteArrayOutputStream();
			while ((size = in.read(temp)) > 0 && nStartPos < fileLength) {
				buf.write(temp, 0, size);
				nStartPos += size;
			}
			in.close();
			byte[] b = buf.toByteArray();
			getResponse().setBufferSize(b.length);
			getResponse().setContentType(fileType==null?"application/zip":fileType);
			getResponse().setHeader("Content-Disposition", getAttachmentString(fileName));
			getResponse().setContentLength(b.length);
			ServletOutputStream out = getResponse().getOutputStream();
			out.write(b);
		}catch(Throwable e){
			throw new Throwable("下载文件失败");
		}
	}

	protected String getAttachmentString(String excelFileName) {
		String filename = excelFileName;
		try {
			if(getRequest().getHeader("user-agent").indexOf("MSIE") != -1) {   
				filename = java.net.URLEncoder.encode(filename,"utf-8");
				filename = filename.replaceAll("\\+", "%20"); //处理空格
			} else {   
				filename = new String(filename.getBytes("utf-8"),"iso-8859-1");   
			}
		} catch (UnsupportedEncodingException e) { }
		return "attachment; filename=\"" + filename + "\";";
	}
}
