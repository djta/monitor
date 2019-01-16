package transactions.project.weixin.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.activation.MimetypesFileTypeMap;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.security.DinamicaUser;

public class WeixinImportFile extends GenericTableManager{
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);

		String[] conf_type=getConfig().getConfigValue("sucai_type").split(",");
		int conf_size=Integer.valueOf(getConfig().getConfigValue("sucai_size"));
		String insertSql=getSQL(getResource("insert.sql"), inputParams);
		String file_name=inputParams.getString("file.filename");
		String file_type=null;
		if (inputParams.isNull("file.filename")){
			throw new Throwable("文件名不能为空！");
		}else{
			//限制文件类型
			boolean is_upload=false;
			for(int i=0;i<conf_type.length;i++){
				if(file_name.contains("."+conf_type[i])){
					is_upload=true;
					file_type=conf_type[i];
					break;
				}
			}
			if(!is_upload){
				throw new Throwable("文件类型不正确，请重新上传");
			}
		}
		
		
		String file = inputParams.getString("file");
		File uploadFile=new File(file);
		//限制文件大小
		int upload_size=(int) uploadFile.length();
		if(upload_size>conf_size){
			throw new Throwable("上传附件大小不能超过"+conf_size);
		}
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		
		/*String current_token=(String)getSession().getAttribute("token");
		if(null==current_token){
			throw new Throwable("当前用户token不能为空");
		}
		String dirHead=current_token.substring(0, 1)+"\\"+current_token;*/
		String dirHead="";
		String[] currentDateString=matter.format(new Date()).split("-");
		String year=currentDateString[0];
		String month=currentDateString[1];
		String day=currentDateString[2];
		
		DinamicaUser user = (DinamicaUser) getSession().getAttribute("dinamica.security.login");
		String userlogin=user.getName();
		
		String baseUploadDir=getRequest().getSession().getServletContext().getRealPath("/")+"\\upload";
		String realyDir=baseUploadDir+"\\"+year+"\\"+month+"\\"+day+"\\"+userlogin;
		new File(realyDir).mkdirs();
		String randomStr=getRandomString();
		String newFilePath=realyDir+"\\"+randomStr+"."+file_type;
		String newFileName=randomStr+"."+file_type;
		while(true){
			if(new File(newFilePath).isFile()){
				randomStr=getRandomString();
				newFilePath=realyDir+"\\"+randomStr+"."+file_type;
				newFileName=randomStr+"."+file_type;
			}else{
				break;
			}
		}
		
		FileInputStream fis=null;
		FileOutputStream fos=null;
		try{
			fis= new FileInputStream(uploadFile); 
			fos=new  FileOutputStream(newFilePath);  
			byte[] buf = new byte[1024];
			while(fis.read(buf)!=-1){
				fos.write(buf);
			}
			
		}catch(Throwable e){
			e.printStackTrace();
		}finally{
			if(null!=fis){
				fis.close();
			}
			if(null!=fos){
				fos.close();
				fos.flush();
			}
		}
		

		
		Db db=getDb();
		String path= "/upload"+"/"+year+"/"+month+"/"+day+"/"+userlogin+"/"+newFileName;
		insertSql=StringUtil.replace(StringUtil.replace(StringUtil.replace(insertSql, "${file_name}", newFileName), "${file_type}", file_type), "${path}",path);
		db.exec(insertSql);
		return rc;
	}
	public String getRandomString(){
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 16; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
}
