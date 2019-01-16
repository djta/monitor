package com.ccms.faq;

import com.ccms.InsertMaster;
import com.ccms.util.jms.MsgBean;
import com.ccms.util.jms.ReceiveSendJMS;
import com.ccms.util.lunece.core.IndexManage;

import dinamica.Recordset;
import dinamica.StringUtil;

public class ManageFaqIndex extends InsertMaster {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		
		String tuid = inputParams.getString("tuid");
		String show_name = inputParams.getString("show_name");
		String is_tip = inputParams.getString("is_tip");
		String topic_name = inputParams.getString("topic_name");
		String path = getContext().getInitParameter("index-path")+"/faq/"+getSession().getAttribute("dinamica.user.subject");
		
		if(tuid != null && show_name == null){//删除
			IndexManage.deleteIndex(path, "tuid", tuid);
		}else{
			//String[] columns = {"show_name","lable","content"};

			if(tuid != null){//修改
				//IndexManage.deleteIndex(path, "tuid", tuid);
				//IndexManage.createIndex(path, columns, "tuid", inputParams);
				IndexManage.modifyIndex(path, inputParams);
			}else{//添加
				String seq = getSQL(getResource("query-seq.sql"),inputParams);
				Recordset rs = getDb().get(seq);
				rs.next();
				Integer tuidValue = rs.getInteger("seq");
				inputParams.setValue("tuid", tuidValue);
				inputParams.setValue("superior", rs.getString("superior"));
				inputParams.setValue("create_date", rs.getString("create_date"));
				IndexManage.addIndex(path, inputParams);
				//IndexManage.createIndex(path, columns, "tuid", inputParams);
			}
			if("1".equals(is_tip) && topic_name != null){//发送广播
				String queryInfo = getSQL(getResource("query-info.sql"),inputParams);
				Recordset rsInfo = getDb().get(queryInfo);
				rsInfo.next();
				String title = rsInfo.getString("title");
				String sender = rsInfo.getString("sender");
				String send_date = rsInfo.getString("send_date");
				String content = getSQL(getResource("tip-info.sql"),inputParams);
				content = StringUtil.replace(content, "${tuid}", inputParams.getString("tuid"));
				MsgBean msg = new MsgBean();
				msg.setMsg_title(title);
				msg.setMsg_content(content);
				msg.setSender(sender);
				msg.setSend_date(send_date);
				ReceiveSendJMS.sendTopicMsg(msg, topic_name+sender, topic_name);
			}
		}
		
		return rc;
	}
}
