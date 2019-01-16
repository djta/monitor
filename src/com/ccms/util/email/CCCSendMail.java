package com.ccms.util.email;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import dinamica.Config;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Record;
import dinamica.Recordset;
import dinamica.StringUtil;


public class CCCSendMail extends GenericTableManager {
	
	public int service(Recordset inputParams) throws Throwable {
		
//		String pagingRecordsetName = getConfig().getConfigValue("paging-recordset","query.sql");    //get paging recordset name
		String insert = getSQL(getResource("insert-email.sql"),inputParams);
		String insertAttach = getResource("insert-attach.sql");
		String emailInfoSQL = getResource("email-info.sql");
		
		Db db = getDb();
		int rc = super.service(inputParams);
		
		String accountId=inputParams.getString("account_id");
		emailInfoSQL = StringUtil.replace(emailInfoSQL, "${accountId}", "'"+ accountId + "'");
        Recordset rs=db.get(emailInfoSQL);
		
		String smtp_server="";
		int smtp_port=0;
		String userName="";
		String pwd="";

		for(Record r:rs.getData()){
			smtp_server=(String)r.getFieldValue("smtp_server");
			smtp_port=(Integer)r.getFieldValue("smtp_port");
			userName=(String)r.getFieldValue("verify_username");
			pwd=(String)r.getFieldValue("verify_password");
		}
		if (inputParams.isNull("file.filename"))
			System.out.println(111111111);
	    String fileName=inputParams.getString("file.filename");
		String user_email=inputParams.getString("account_id");
		
		String title=formatRequestEncoding(inputParams.getString("email_title"));
		String receiver=inputParams.getString("receiver_addr");
		String copyTo=inputParams.getString("copy_to");
		String content=formatRequestEncoding(inputParams.getString("content"));
		content=content.replaceAll("<p>", "").replaceAll("</p>", "");
		
		
	    String filePath = formatRequestEncoding(inputParams.getString("file"));
		
		
		SendMail sm=new SendMail(smtp_server);
		sm.setSmtpPort(smtp_port+"");
		sm.setSubject(title);
		sm.setBody(content);
		sm.setNamePass(userName, pwd);
		if (copyTo!=null && copyTo.length()>0) {
			sm.setCopyTo(copyTo);
		}
		sm.setFrom(accountId);
		sm.setTo(receiver);
		
		byte[] fileData=null;
		String fileType="";
		if (filePath!=null && filePath.length()>0) {
			
			String fileExt=fileName.substring(fileName.lastIndexOf(".")+1);
			
			FileInputStream in = null;
			fileData = null;
			try {
				File file = new File(filePath);
				in = new FileInputStream(file);
				int fileLength = new Long(file.length()).intValue();
				fileData = new byte[fileLength];
				byte[] b = new byte[1024];
				//写入指定的文件中 
				int nRead = 0;
				int nStartPos = 0;
				while ((nRead = in.read(b, 0, 1024)) > 0 && nStartPos < fileLength) {
					for (int j = 0; j < nRead; j++) {
						fileData[nStartPos + j] = b[j];
					}
					nStartPos += nRead;
				}
				//关闭流
				in.close();

				if (fileExt.equalsIgnoreCase("doc"))
					fileType="application/msword;charset=GBK";
				else if (fileExt.equalsIgnoreCase("xls"))
					fileType="application/vnd.ms-excel";
				else if (fileExt.equalsIgnoreCase("gif"))
					fileType="image/gif";
				else if (fileExt.equalsIgnoreCase("pdf"))
					fileType="application/pdf";
				else if (fileExt.equalsIgnoreCase("JPG"))
					fileType="image/gif";
				else if (fileExt.equalsIgnoreCase("jepg"))
					fileType="image/gif";
				else if (fileExt.equalsIgnoreCase("bmp"))
					fileType="image/gif";
				else
					fileType="text/html; charset=UTF-8";
				sm.addFileAffix(fileName, fileData, fileType);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
			
		if(!sm.sendout()){
			rc=1;
		}else{
			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			insert = StringUtil.replace(insert, "${send_date}", "'"+ df.format(new Date()) + "'");
			if(copyTo!=null){
				insert = StringUtil.replace(insert, "${copy_to}", "'"+ copyTo + "'");
			}else{
				insert = StringUtil.replace(insert, "${copy_to}", ""+ "null" + "");
			}	
			insert = StringUtil.replace(insert, "${email_title}", "'"+ title + "'");
			insert = StringUtil.replace(insert, "${email_content}", "'"+ content + "'");
			insert = StringUtil.replace(insert, "${receive_address}", "'"+ receiver + "'");
			insert = StringUtil.replace(insert, "${user_address}", "'"+ user_email + "'");
			db.exec(insert);
			
			String serverFilePath=new File("").getCanonicalPath()+"\\"+fileName;
			SimpleDateFormat df1=new SimpleDateFormat("yyyyMMddHHmmss");
			int index=serverFilePath.lastIndexOf(".");
			String s1=serverFilePath.substring(0,index);
			String s2=serverFilePath.substring(index);
			serverFilePath=s1+df1.format(new Date())+s2;
			if(filePath!=null && filePath.length()>0){
				insertAttach = getSQL(insertAttach,inputParams);  //******************
				insertAttach = StringUtil.replace(insertAttach, "${file_name}", "'"+ fileName + "'");
				insertAttach = StringUtil.replace(insertAttach, "${file_path}", "'"+ serverFilePath.replace("\\", "/") + "'");
				insertAttach = StringUtil.replace(insertAttach, "${file_type}", "'"+ fileType + "'");
				
				try {
					db.exec(insertAttach);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				RecvMail.saveToLocal(fileData, serverFilePath);
			}
			
			//上传
			
		}
		return rc;
	}
	
	public String formatRequestEncoding(String str) throws Throwable {

		Config _config = getConfig();

		String encoding = getContext().getInitParameter("request-encoding");
		if (encoding != null && encoding.trim().equals(""))
			encoding = null;

		if (_config.requestEncoding != null)
			return new String(str.getBytes("ISO8859-1"),
					_config.requestEncoding);
		else if (encoding != null)
			return new String(str.getBytes("ISO8859-1"), encoding);
		else
			return str;
	}

}
