package com.ccms.util.email;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Record;
import dinamica.Recordset;
import dinamica.StringUtil;

public class ReceiveMail extends GenericTableManager {
	public int service(Recordset inputParams) throws Throwable {
		String accountId=inputParams.getString("accountId");
		String insert = getSQL(getResource("insert-email.sql"),inputParams);
		String insertAttach = getResource("insert-attach.sql");
		String emailInfoSQL = getResource("email-info.sql");
		emailInfoSQL = StringUtil.replace(emailInfoSQL, "${accountId}", "'"+ accountId + "'");
		Db db = getDb();
		Recordset rs=db.get(emailInfoSQL);
		
		String pop3_server="";
		int pop3_port=0;
		String userName="";
		String pwd="";
		int overTimeLen=0;
		int bakTimeLen=0;

		for(Record r:rs.getData()){
			pop3_server=(String)r.getFieldValue("pop3_server");
			pop3_port=(Integer)r.getFieldValue("pop3_port");
			userName=(String)r.getFieldValue("username");
			pwd=(String)r.getFieldValue("password");
			overTimeLen=(Integer)r.getFieldValue("over_time_len");
			bakTimeLen=(Integer)r.getFieldValue("bak_time_len");
		}
		
		int rc = super.service(inputParams);
		RecvMail rm=new RecvMail();
		Set<MailRecordBean> recordSet=rm.recvMail("101", pop3_server, pop3_port, userName, pwd, overTimeLen, bakTimeLen+"", null, 1, null, null);
		Iterator<MailRecordBean> it=recordSet.iterator();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df1=new SimpleDateFormat("yyyyMMddHHmmss");
		String batch_id=df1.format(new Date());

		if (recordSet.size()>0) {
			while (it.hasNext()) {
				MailRecordBean mb = it.next();
				insert = StringUtil.replace(insert, "${send_date}", "'"+ df.format(mb.getSendTime()) + "'");
				insert = StringUtil.replace(insert, "${batch_id}", "'"+ batch_id + "'");
				insert = StringUtil.replace(insert, "${email_title}", "'"+ mb.getMailSubject() + "'");
				insert = StringUtil.replace(insert, "${email_content}", "'"+ mb.getMailBody() + "'");
				insert = StringUtil.replace(insert, "${sender_address}", "'"+ mb.getSender() + "'");
				db.exec(insert);
				
				insertAttach = getSQL(insertAttach,inputParams);
				
				Set<MailFileBean> fileBeanSet=mb.getMailFiles();
				if(fileBeanSet.size()>0){
					Iterator<MailFileBean> it1=fileBeanSet.iterator();
					while(it1.hasNext()){
						MailFileBean mfb=it1.next();
						String file_path=mfb.getSaveFileName().replace("\\", "/");
						insertAttach = StringUtil.replace(insertAttach, "${file_name}", "'"+ mfb.getFileName() + "'");
						insertAttach = StringUtil.replace(insertAttach, "${file_path}", "'"+ file_path + "'");
						insertAttach = StringUtil.replace(insertAttach, "${file_type}", "'"+ mfb.getFileType() + "'");
						
						db.exec(insertAttach);
					}
				}
			
			}
			
		}
		
		return rc;
	}

}
