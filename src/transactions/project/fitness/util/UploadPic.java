package transactions.project.fitness.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dinamica.Base64;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class UploadPic extends GenericTableManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see dinamica.GenericTransaction#service(dinamica.Recordset)
	 */
	public int service(Recordset inputParams) throws Throwable {
        String imgbest = (String) inputParams.getValue("imgbest");
        String pic = imgbest.substring(imgbest.indexOf(";base64,")+";base64,".length());

		byte[] asBytes = Base64.decode(pic);
		InputStream in = new ByteArrayInputStream(asBytes);
		
		Date d = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
		String timestamp = df.format(d);
		
		//把文件名拼在最前面.
		String filePathName = inputParams.getValue("pk_value")+"_"+timestamp + ".jpg";
		String localDir = getContext().getInitParameter("upload-dir");

		Integer org_id = (Integer)getSession().getAttribute("dinamica.user.org");
		if( null != org_id ){
			localDir = localDir + File.separator + org_id;
		}
		localDir = getSpecificOutputPath(localDir);

		SimpleDateFormat df_year = new SimpleDateFormat("yyyy");
		SimpleDateFormat df_month = new SimpleDateFormat("MM");
		SimpleDateFormat df_date = new SimpleDateFormat("dd");
		SimpleDateFormat df_hour = new SimpleDateFormat("HH");
		String directory_year = df_year.format(d);
		String directory_month = df_month.format(d);
		String directory_date = df_date.format(d);
		String directory_hour = df_hour.format(d);
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
		
		byte[] b = new byte[1024*1024];
		//写入指定的文件中 

		int nRead=0;
		BufferedInputStream bis=null;
		BufferedOutputStream bos=null;
		try {
			bis= new BufferedInputStream(in);
			bos= new BufferedOutputStream(new FileOutputStream(fileLocalPath));
			while ((nRead = bis.read(b)) != -1) { 
				bos.write(b, 0, nRead);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				bos.flush();
				bis.close();
				bos.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		inputParams.setValue("file.filename", filePathName);
		inputParams.setValue("file_size", 0);
		inputParams.setValue("file.content-type", "image/jpg");
		inputParams.setValue("file.path", fileLocalPath.replace("\\", "/"));
		//保存上传信息
		String insert = getSQL(getResource("insert.sql"),inputParams);
		getDb().exec(insert);
		
		int rc = super.service(inputParams);
		return rc;
	}
	
	public static String getSpecificOutputPath(String root){
		Calendar c = Calendar.getInstance(Locale.CHINA);
		return root+File.separator+c.get(Calendar.YEAR)+File.separator+(c.get(Calendar.MONTH)+1)+File.separator+c.get(Calendar.DAY_OF_MONTH)+File.separator+c.get(Calendar.HOUR_OF_DAY);
	}
}
