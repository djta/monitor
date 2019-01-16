package com.ccms.util.email;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;

import dinamica.GenericOutput;
import dinamica.GenericTransaction;

public class DownloadForSms  extends GenericOutput{
	public void print(GenericTransaction data) throws Throwable {
		BufferedInputStream in =null ;
		ByteArrayOutputStream buf = null;
		try{
		String filePath=getRequest().getParameter("filePath");
		
		String fileName="";
		if(null==filePath||"".equals(filePath)){
			throw new Throwable("下载文件失败");
		}else{
			fileName=filePath.substring(filePath.lastIndexOf("/")+1);
		}
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
		
		byte[] b = buf.toByteArray();
		getResponse().setBufferSize(b.length);
		String fileExt = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
		
		if (fileExt.equalsIgnoreCase("doc"))
			getResponse().setContentType("application/msword;charset=GBK");
		else if (fileExt.equalsIgnoreCase("xls"))
			getResponse().setContentType("application/vnd.ms-excel");
		else if (fileExt.equalsIgnoreCase("gif"))
			getResponse().setContentType("image/gif");
		else if (fileExt.equalsIgnoreCase("pdf"))
			getResponse().setContentType("application/pdf");
		else if (fileExt.equalsIgnoreCase("JPG"))
			getResponse().setContentType("image/gif");
		else if (fileExt.equalsIgnoreCase("jepg"))
			getResponse().setContentType("image/gif");
		else if (fileExt.equalsIgnoreCase("bmp"))
			getResponse().setContentType("image/gif");
		else
			getResponse().setContentType("text/html; charset=UTF-8");
		
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
//		if (filename.toLowerCase().endsWith(".xls")) {
//			filename = filename.substring(0, filename.lastIndexOf("."));
//		}
		
		try {
			if(getRequest().getHeader("user-agent").indexOf("MSIE") != -1) {   
				filename = java.net.URLEncoder.encode(filename,"utf-8");
				filename = filename.replaceAll("\\+", "%20"); //处理空格
			} else {   
				filename = new String(filename.getBytes("utf-8"),"iso-8859-1");   
			}
		} catch (UnsupportedEncodingException e) { }
		return "attachment; filename=\"" + filename + ";";
	}
}
