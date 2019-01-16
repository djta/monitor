package com.ccms.util.jms;

import java.sql.Types;
import java.util.List;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;

public class ReceiveMsgInBox extends GenericTableManager {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = getDb();
		String queryInfo = getSQL(getResource("query-info.sql"),inputParams);
		String querySeq = getSQL(getResource("query-seq.sql"),inputParams);
		Recordset rsInfo = db.get(queryInfo);
		rsInfo.next();
		String destination = rsInfo.getString("destination");
		
		String queryTopic = getSQL(getResource("query-topic.sql"),inputParams);
		Recordset rsTopic = db.get(queryTopic);
		Recordset rsInBox = new Recordset();
		rsInBox.append("tuid", Types.INTEGER);
		rsInBox.append("sender", Types.VARCHAR);
		rsInBox.append("msg_title", Types.VARCHAR);
		rsInBox.append("msg_content", Types.VARCHAR);
		rsInBox.append("send_date", Types.VARCHAR);
		//先收取广播信息，再收取个人信息
		while(rsTopic.next()){
			String topic_name = rsTopic.getString("topic_name");
			List<MsgBean> msgList = ReceiveSendJMS.receiveTopicMsg(topic_name+destination, topic_name);
			if(msgList != null){
				for(MsgBean msg : msgList){
					rsInBox.addNew();
					rsInBox.setValue("tuid", getSeq(querySeq,"seq"));
					rsInBox.setValue("sender", msg.getSender());
					rsInBox.setValue("msg_title", msg.getMsg_title());
					rsInBox.setValue("msg_content", msg.getMsg_content());
					rsInBox.setValue("send_date", msg.getSend_date());
				}
			}
		}
		List<MsgBean> msgList = ReceiveSendJMS.receiveMsg(destination);
		if(msgList != null){
			for(MsgBean msg : msgList){
				rsInBox.addNew();
				rsInBox.setValue("tuid", getSeq(querySeq,"seq"));
				rsInBox.setValue("sender", msg.getSender());
				rsInBox.setValue("msg_title", msg.getMsg_title());
				rsInBox.setValue("msg_content", msg.getMsg_content());
				rsInBox.setValue("send_date", msg.getSend_date());
			}
		}
		
		if(rsInBox.getRecordCount() > 0){
			String insert = getResource("insert-inbox.sql");
			rsInBox.top();
			while(rsInBox.next()){
				String sql = getSQL(insert,rsInBox);
				db.exec(sql);
			}
		}
		
		String queryData = getSQL(getResource("query-inbox.sql"),inputParams);
		Recordset rsData = db.get(queryData);
		publish("query.sql", rsData);
		
		return rc;
	}
	
	public Integer getSeq(String sql, String column) throws Throwable{
		Recordset rs = getDb().get(sql);
		rs.next();
		Integer seq = rs.getInteger(column);
		return seq;
	}
}
