package com.ccms.util.attachment;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import dinamica.Config;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.xml.Document;

public class UploadAttachFile extends GenericTableManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see dinamica.GenericTransaction#service(dinamica.Recordset)
	 */
	public int service(Recordset inputParams) throws Throwable {

		if (inputParams.isNull("file.filename"))
			throw new Throwable("文件不能为空!");

		Document doc = getConfig().getDocument();
		String maxSize = doc.getElement("max-size").getValue();
		String filePre = doc.getElement("file-pre").getValue();
		//前缀为空值
		if(filePre==null){
			filePre = "";
		}
		
		String fileName = (String) inputParams.getValue("file.filename");
		fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
		fileName = formatRequestEncoding(fileName);

		// get temp file
		String path = (String) inputParams.getValue("file");

		File f = new File(path);

		// get file size
		Integer size = new Integer((int) f.length());
		inputParams.setValue("file_size", size);

		if (size.intValue() == 0)
			throw new Throwable("文件大小为0!");

		//判断文件大小
		Integer maxSizeVal = Integer.parseInt(maxSize);
		if (size.intValue() > (1024 * 1024 * maxSizeVal))
			throw new Throwable("文件大小限制为最大"+maxSizeVal+"M,您的文件较大，不能提交。");

		//写文件到FTP
		Date d = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
		SimpleDateFormat df_year = new SimpleDateFormat("yyyy");
		SimpleDateFormat df_month = new SimpleDateFormat("MM");
		SimpleDateFormat df_date = new SimpleDateFormat("dd");
		SimpleDateFormat df_hour = new SimpleDateFormat("HH");
		String timestamp = df.format(d);
		String directory_year = df_year.format(d);
		String directory_month = df_month.format(d);
		String directory_date = df_date.format(d);
		String directory_hour = df_hour.format(d);
		
		//把文件名拼在最前面.
		filePre = fileName.substring(0,fileName.lastIndexOf("."))+filePre;
		String filePathName = filePre+"_"+timestamp+fileName.substring(fileName.lastIndexOf("."), fileName.length());
		inputParams.setValue("file.filename", filePathName);
		inputParams.setValue("file.filerealname", fileName);

		String localDir = getContext().getInitParameter("upload-dir");
		localDir = getSpecificOutputPath(localDir);
		localDir = StringUtil.replace(localDir, "${year}", directory_year);
		localDir = StringUtil.replace(localDir, "${month}", directory_month);
		localDir = StringUtil.replace(localDir, "${date}", directory_date);
		localDir = StringUtil.replace(localDir, "${hour}", directory_hour);

		//如果目录不存在，则新建
		File file = new File(localDir);
		if(!file.exists()){
			file.mkdirs();
			file = null;
		}
		
		if(!localDir.endsWith(File.separator)){
			localDir += File.separator;
		}
		String fileLocalPath = localDir + filePathName;
		inputParams.setValue("file.path", fileLocalPath.replace("\\", "/"));
		
		byte[] b = new byte[1024*1024];
		//写入指定的文件中 

		int nRead=0;
		BufferedInputStream bis=null;
		BufferedOutputStream bos=null;
		try {
			bis= new BufferedInputStream(new FileInputStream(f));
			bos= new BufferedOutputStream(new FileOutputStream(fileLocalPath));
			while ((nRead = bis.read(b)) != -1) { 
				bos.write(b, 0, nRead);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			try {
				bos.flush();
				bis.close();
				bos.close();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
		
		//保存上传信息
		String insert = getSQL(getResource("insert.sql"),inputParams);
		getDb().exec(insert);
		String is_scale=getRequest().getParameter("is_scale");
		if(!"".equals(is_scale)&&null!=is_scale){
			String scalePath=fileLocalPath.substring(0,fileLocalPath.lastIndexOf(File.separator)+1);
			uploadScaleImg(scalePath+"50_"+filePathName, path, 50);
			uploadScaleImg(scalePath+"25_"+filePathName, path, 25);
		}
		//删除临时文件
		f.delete();
		
		int rc = super.service(inputParams);
		return rc;

	}

	protected String formatRequestEncoding(String str) throws Throwable {

		Config _config = getConfig();

		// global encoding?
		String encoding = getContext().getInitParameter("request-encoding");
		if (encoding != null && encoding.trim().equals(""))
			encoding = null;

		// load resource with appropiate encoding if defined
		if (_config.requestEncoding != null)
			return new String(str.getBytes("ISO8859-1"),
					_config.requestEncoding);
		else if (encoding != null)
			return new String(str.getBytes("ISO8859-1"), encoding);
		else
			return str;
	}
	
	public static String getSpecificOutputPath(String root){
		Calendar c = Calendar.getInstance(Locale.CHINA);
		return root+File.separator+c.get(Calendar.YEAR)+File.separator+(c.get(Calendar.MONTH)+1)+File.separator+c.get(Calendar.DAY_OF_MONTH)+File.separator+c.get(Calendar.HOUR_OF_DAY);
	}
	//写压缩图片
	public void uploadScaleImg(String fileout,String filein,int scale) throws IOException {    	
		OutputStream os=null;
		InputStream in=null;
			  try {
				  in = new FileInputStream(new File(filein));
			      Image src = javax.imageio.ImageIO.read(in);
			      int width = (int) (src.getWidth(null) * scale / 100.0);
			      int height = (int) (src.getHeight(null) * scale / 100.0);
			      BufferedImage bufferedImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
			      bufferedImage.getGraphics().drawImage(src.getScaledInstance(width, height, Image.SCALE_SMOOTH),0, 0, null);
			      os = new FileOutputStream(fileout);
			      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(os);
			      encoder.encode(bufferedImage);
			     
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				try {
					os.flush();
			        os.close();
			        in.close();
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
			}
	}
}
