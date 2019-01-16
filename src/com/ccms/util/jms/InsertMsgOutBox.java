package com.ccms.util.jms;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class InsertMsgOutBox extends GenericTableManager {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		
		String send_flag = inputParams.getString("send_flag");
		Db db = getDb();
		String insert = getSQL(getResource("insert-outbox.sql"),inputParams);
		String insert_outbox_staff = getSQL(getResource("insert-outbox_staff.sql"),inputParams);
		if("1".equals(send_flag)){//发送
			
			String queryInfo = getSQL(getResource("query-info.sql"),inputParams);
			Recordset rsInfo = db.get(queryInfo);
			rsInfo.next();
			String sender = rsInfo.getString("sender");
			String send_date = rsInfo.getString("send_date");
			String msg_title = inputParams.getString("msg_title");
			String msg_content = inputParams.getString("msg_content");
			String send_type = inputParams.getString("send_type");
			
			MsgBean msg = new MsgBean();
			msg.setMsg_title(msg_title);
			msg.setMsg_content(msg_content);
			msg.setSender(sender);
			msg.setSend_date(send_date);
			if("0".equals(send_type)){//点对点
				String[] staffs = getRequest().getParameterValues("staff_id");
				if(staffs != null){
					String errors = "";
					boolean sended = false;
					for(String destination : staffs){
						boolean sendFlag = ReceiveSendJMS.sendMsg(msg, destination);
						if(sendFlag == false){
							errors += destination + " ";
						}else{
							sended = sended?true:sendFlag;
							if(sended){
								db.exec(insert);
							}
							db.exec(getSQL(StringUtil.replace(insert_outbox_staff,"${userlogin}",destination),inputParams));
						}
					}
					if(errors.length() > 0){
						throw new Throwable(errors+"的信息发送失败！");
					}
				}
			}else{
				String topic_name = inputParams.getString("topic_name");
				boolean sendFlag = ReceiveSendJMS.sendTopicMsg(msg, topic_name+sender, topic_name);
				if(sendFlag == true){
					db.exec(insert);
				}else{
					throw new Throwable("发送失败！");
				}
			}
		}else{
			db.exec(insert);
			String send_type = inputParams.getString("send_type");
			if("0".equals(send_type)){
				String[] staffs = getRequest().getParameterValues("staff_id");
				if(staffs != null){
					for(String destination : staffs){
						db.exec(StringUtil.replace(insert_outbox_staff, "${userlogin}", destination));
					}
				}
			}
		}
		
		return rc;
	}
}
