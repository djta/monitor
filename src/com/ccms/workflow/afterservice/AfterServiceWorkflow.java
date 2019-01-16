package com.ccms.workflow.afterservice;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ccms.authority.AuthorityBean;
import com.ccms.workflow.countersign.CountersignDoAction;
import com.ccms.workflow.quartz.AddTimeoutSchedule;
import com.opensymphony.workflow.Workflow;
import com.opensymphony.workflow.basic.BasicWorkflow;
import com.opensymphony.workflow.spi.WorkflowEntry;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.RecordsetField;
import dinamica.StringUtil;
import dinamica.security.DinamicaUser;

public class AfterServiceWorkflow extends GenericTableManager
{

	@SuppressWarnings("unchecked")
	public int service(Recordset inputParams) throws Throwable
	{
		int rc = 0;
		DinamicaUser user = (DinamicaUser) getSession().getAttribute("dinamica.security.login");
		
		Long wfentry_id = inputParams.getInteger("__wfentry_id__").longValue();
		Integer action_id = inputParams.getInteger("__action_id__");
		String pk_value = inputParams.getString("__pk_value__");
		Integer wfm_id = inputParams.getInteger("__wfm_id__");
		String assign_user = inputParams.getString("__assign_user__");
		
	    try{
	    	Workflow wf = new BasicWorkflow(user.getName());
	    	
	    	Db db = getDb();
	    	
	    	//判断该流程是否是子流程，如果是子流程则判断是否主流程的所有子流程都处理完成，然后触发主流程动作
	    	String queryEntry = getLocalResource("/com/ccms/workflow/sql/query-wfentry_byid.sql");
	    	queryEntry = StringUtil.replace(queryEntry, "${id}", wfentry_id.toString());
    		Recordset rsEntry = db.get(queryEntry);
    		rsEntry.first();
    		String parent_id = rsEntry.getString("parent_id");
    		if(parent_id != null){//存在主流程
    			//判断当前子流程是否结束，如果结束则判断其他子流程是否全结束，以此来触发主流程继续流转
    			int state = wf.getEntryState(wfentry_id);
    			if(state == WorkflowEntry.COMPLETED){
    				String parent_node_id = rsEntry.getString("parent_node_id");
    				if(parent_node_id != null){
    					String step_type = rsEntry.getString("step_type");
    					CountersignDoAction countersign = new CountersignDoAction();
        				countersign.doCountersign(Integer.parseInt(parent_id), Integer.parseInt(parent_node_id), step_type, db);
    				}
    			}
    		}
	    	
	    	//判断下个步骤中是否有超时自动提交的action，有的话启动一个定时任务，到超时时间执行
	    	List currentSteps = wf.getCurrentSteps(wfentry_id);
	    	if(currentSteps != null && currentSteps.size() > 0){
	    		AddTimeoutSchedule timeout = new AddTimeoutSchedule();
		    	timeout.addTimeoutSchedule(currentSteps, db);
	    	}
	    	
	    	String queryAction = getLocalResource("/com/ccms/workflow/sql/query-action.sql");
	    	queryAction = StringUtil.replace(queryAction, "${action_id}", action_id.toString());
	    	Recordset rsAction = db.get(queryAction);
	    	rsAction.first();

	    	String cs_campaign_id = rsAction.getString("cs_campaign_id");
	    	String cs_job_id = rsAction.getString("cs_job_id");
	    	String wfm_step_id = rsAction.getString("next_node");

	    	//CS流程
	    	if(cs_campaign_id != null && cs_campaign_id.length() > 0 && cs_job_id != null && cs_job_id.length() > 0  && pk_value != null && pk_value.length() > 0){//跳单
	    		Recordset inputs = new Recordset();
	    		inputs.append("wfm_id", Types.INTEGER);
	    		inputs.append("wfm_entry_id", Types.INTEGER);
	    		inputs.append("wfm_action_id", Types.INTEGER);
	    		inputs.append("wfm_step_id", Types.INTEGER);
	    		inputs.append("pk_value", Types.VARCHAR);
	    		inputs.append("campaign_id", Types.VARCHAR);
	    		inputs.append("job_id", Types.INTEGER);

	    		inputs.addNew();
	    		
	    		inputs.setValue("wfm_id", wfm_id);
	    		inputs.setValue("wfm_entry_id", wfentry_id.intValue());
	    		inputs.setValue("wfm_action_id", action_id);
	    		inputs.setValue("wfm_step_id", Integer.parseInt(wfm_step_id));
	    		inputs.setValue("pk_value", pk_value);
	    		inputs.setValue("campaign_id", cs_campaign_id);
	    		inputs.setValue("job_id", Integer.parseInt(cs_job_id));
	    		
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				GenericTransaction t = (GenericTransaction) loader.loadClass("com.ccms.workflow.cs.InjectCSTaskPoolForWfm").newInstance();
							
				t.init(this.getContext(), this.getRequest(), this.getResponse());
				t.setConfig(this.getConfig());
				t.setConnection(this.getConnection());
				t.service(inputs);
	    	}

	    	//发技能组邮件
	    	String p_pk_value = null;
			if(inputParams.containsField("__p_pk_value__") && inputParams.getString("__p_pk_value__")!=null){
				p_pk_value = "'"+inputParams.getString("__p_pk_value__")+"'";
			}

			String email_skill_id = rsAction.getString("email_skill_id");
			String email_skill_subject = rsAction.getString("email_skill_subject");
    		String email_skill_template_id = rsAction.getString("email_skill_template_id");
    		String email_assign_user = rsAction.getString("email_assign_user");
	    	//技能组邮件不为空
	    	if(((email_skill_id != null && email_skill_id.length() > 0) || (email_assign_user != null && email_assign_user.equals("1"))) 
	    			&& email_skill_template_id != null && email_skill_template_id.length() > 0){
	    		String tempEmail = getLocalResource("/com/ccms/workflow/sql/remind/query_email_template.sql");
	    		String insertEmail = getLocalResource("/com/ccms/workflow/sql/remind/insert-email.sql");
	    		ArrayList<String> userList=new ArrayList<String>();
	    		if(email_skill_id != null && email_skill_id.length() > 0){
	    			String skillEmail = getLocalResource("/com/ccms/workflow/sql/remind/query_skill_email.sql");
					AuthorityBean auth = new AuthorityBean(getDb(), user.getName());
					skillEmail = StringUtil.replace(skillEmail, "${table}", getSQL(auth.spliceGroupSql(Integer.parseInt(email_skill_id), "2", false),null));
					
					Recordset rsSkillReciver = db.get(skillEmail);
					while(rsSkillReciver.next()){
						String strReciver = rsSkillReciver.getString("reciver");
						String userlogin = rsSkillReciver.getString("userlogin");
						if(!"".equals(userlogin)&&null!=userlogin){
							userList.add(userlogin);
						}
						sendRemind(inputParams,strReciver,email_skill_subject,email_skill_template_id,p_pk_value,pk_value,action_id,wfm_id,tempEmail,insertEmail,user.getName(),assign_user);
					}
	    		}
				if(email_assign_user != null && email_assign_user.equals("1") && assign_user != null && assign_user.indexOf("_post") < 0){//分配人不为空且是分到具体人头上
					if(!userList.contains(assign_user)){
						String queryStaff = getLocalResource("/com/ccms/workflow/sql/remind/query_staff.sql");
						queryStaff = StringUtil.replace(queryStaff, "${assign_user}", assign_user);
						queryStaff = StringUtil.replace(queryStaff, "${field}", "email");
						Recordset rsReciver = db.get(queryStaff);
						if(rsReciver.next()){
							String strReciver = rsReciver.getString("reciver");
							sendRemind(inputParams,strReciver,email_skill_subject,email_skill_template_id,p_pk_value,pk_value,action_id,wfm_id,tempEmail,insertEmail,user.getName(),assign_user);
						}
					}
				}
	    	}
	    	
	    	String sms_skill_id = rsAction.getString("sms_skill_id");
	    	String sms_skill_template_id = rsAction.getString("sms_skill_template_id");
	    	String sms_assign_user = rsAction.getString("sms_assign_user");
	    	//技能组短信不为空
	    	if(((sms_skill_id != null && sms_skill_id.length() > 0) || (sms_assign_user != null && sms_assign_user.equals("1")))
	    			&& sms_skill_template_id != null && sms_skill_template_id.length() > 0){
	    		String tempSms = getLocalResource("/com/ccms/workflow/sql/remind/query_sms_template.sql");
				String insertSms = getLocalResource("/com/ccms/workflow/sql/remind/insert-sms.sql");
				ArrayList<String> userList=new ArrayList<String>();
				if(sms_skill_id != null && sms_skill_id.length() > 0){
					String skillSms = getLocalResource("/com/ccms/workflow/sql/remind/query_skill_sms.sql");
					AuthorityBean auth = new AuthorityBean(getDb(), user.getName());
					skillSms = StringUtil.replace(skillSms, "${table}", getSQL(auth.spliceGroupSql(Integer.parseInt(sms_skill_id), "2", false),null));
	
					Recordset rsSkillReciver = db.get(skillSms);
					while(rsSkillReciver.next()){
						String strReciver = rsSkillReciver.getString("reciver");
						String userlogin = rsSkillReciver.getString("userlogin");
						if(!"".equals(userlogin)&&null!=userlogin){
							userList.add(userlogin);
						}
						sendRemind(inputParams,strReciver,"",sms_skill_template_id,p_pk_value,pk_value,action_id,wfm_id,tempSms,insertSms,user.getName(),assign_user);
					}
				}
				if(sms_assign_user != null && sms_assign_user.equals("1") && assign_user != null && assign_user.indexOf("_post") < 0){//分配人不为空且是分到具体人头上
					if(!userList.contains(assign_user)){
						String queryStaff = getLocalResource("/com/ccms/workflow/sql/remind/query_staff.sql");
						queryStaff = StringUtil.replace(queryStaff, "${assign_user}", assign_user);
						queryStaff = StringUtil.replace(queryStaff, "${field}", "mobile");
						Recordset rsReciver = db.get(queryStaff);
						if(rsReciver.next()){
							String strReciver = rsReciver.getString("reciver");
							sendRemind(inputParams,strReciver,"",sms_skill_template_id,p_pk_value,pk_value,action_id,wfm_id,tempSms,insertSms,user.getName(),assign_user);
						}
					}
				}
	    	}
	    	
	    	String remind_skill_id = rsAction.getString("remind_skill_id");
	    	String remind_skill_subject = rsAction.getString("remind_skill_subject");
	    	String remind_skill_template_id = rsAction.getString("remind_skill_template_id");
	    	String remind_assign_user = rsAction.getString("remind_assign_user");
	    	//技能组提醒不为空
	    	if(((remind_skill_id != null && remind_skill_id.length() > 0) ||(remind_assign_user != null && remind_assign_user.equals("1")))
	    			&& remind_skill_template_id != null && remind_skill_template_id.length() > 0){
	    		String tempRemind = getLocalResource("/com/ccms/workflow/sql/remind/query_remind_template.sql");
				String insertRemind = getLocalResource("/com/ccms/workflow/sql/remind/insert-remind.sql");
				ArrayList<String> userList=new ArrayList<String>();
				if(remind_skill_id != null && remind_skill_id.length() > 0){
					String skillRemind = getLocalResource("/com/ccms/workflow/sql/remind/query_skill_remind.sql");
					AuthorityBean auth = new AuthorityBean(getDb(), user.getName());
					skillRemind = StringUtil.replace(skillRemind, "${table}", getSQL(auth.spliceGroupSql(Integer.parseInt(remind_skill_id), "2", false),null));
	
					Recordset rsSkillReciver = db.get(skillRemind);
					while(rsSkillReciver.next()){
						String strReciver = rsSkillReciver.getString("reciver");
						String userlogin = rsSkillReciver.getString("userlogin");
						if(!"".equals(userlogin)&&null!=userlogin){
							userList.add(userlogin);
						}
						sendRemind(inputParams,strReciver,remind_skill_subject,remind_skill_template_id,p_pk_value,pk_value,action_id,wfm_id,tempRemind,insertRemind,user.getName(),assign_user);
					}
				}
				if(remind_assign_user != null && remind_assign_user.equals("1") && assign_user != null && assign_user.indexOf("_post") < 0){//分配人不为空且是分到具体人头上
					if(!userList.contains(assign_user)){
						String queryStaff = getLocalResource("/com/ccms/workflow/sql/remind/query_staff.sql");
						queryStaff = StringUtil.replace(queryStaff, "${assign_user}", assign_user);
						queryStaff = StringUtil.replace(queryStaff, "${field}", "userlogin");
						Recordset rsReciver = db.get(queryStaff);
						if(rsReciver.next()){
							String strReciver = rsReciver.getString("reciver");
							sendRemind(inputParams,strReciver,remind_skill_subject,remind_skill_template_id,p_pk_value,pk_value,action_id,wfm_id,tempRemind,insertRemind,user.getName(),assign_user);
						}
					}
				}
	    	}
	    }catch(Throwable e){
	    	throw e;
	    }
		return rc;
		
	}

	private void sendRemind (Recordset rs,String receiver,String subject,String template_id,String p_pk_value,String pk_value,Integer action_id,Integer wfm_id,String templateSql,String insertSql,String strCaller,String assign_user) throws Throwable
	{
    	Db db = getDb();
		//读模版,插入语句
		String tempRemind = templateSql;
		String insertRemind = insertSql;
		//替换地址,主题,模版

		String queryTemplate = StringUtil.replace(tempRemind, "${tuid}", template_id.toString());
		Recordset rsTemplate = db.get(queryTemplate);
		if(rsTemplate.next()){
			//读邮件模版,处理任务
			String body = getMarkString(rsTemplate.getString("body"),rs);
			body = StringUtil.replace(body, "'", "''");
			body = StringUtil.replace(body, "${DEF", "${def");
			body = StringUtil.replace(body, "${caller}", strCaller);
			body = StringUtil.replace(body, "${assign_user}", assign_user);
			subject = getMarkString(subject,rs);

			String insert = getSQL(insertRemind,rs);
			insert = StringUtil.replace(insert, "${p_pk_value}", p_pk_value);
			insert = StringUtil.replace(insert, "${pk_value}", pk_value);
			insert = StringUtil.replace(insert, "${wfm_id}", wfm_id.toString());
			insert = StringUtil.replace(insert, "${action_id}", action_id.toString());
			insert = StringUtil.replace(insert, "${receiver}", receiver);
			insert = StringUtil.replace(insert, "${subject}", subject);
			insert = StringUtil.replace(insert, "${body}", body);
			insert = StringUtil.replace(insert, "${account_id}", rsTemplate.getString("account_id"));
			insert = StringUtil.replace(insert, "${template_id}", template_id.toString());

			db.exec(getSQL(insert,rs));
		}
		return;			
	}

	
	private String getMarkString(String _template, Recordset rs) throws Throwable {
		if(_template == null) _template="";
		if (rs!=null)
		{
			/* get recordset metadata */
			HashMap<String, RecordsetField> flds = rs.getFields();
		
			/* for each field try to replace value */
			Iterator<RecordsetField> i = flds.values().iterator();
			while (i.hasNext())
			{
			
				RecordsetField f = i.next();
				String fname = f.getName();
				Object value = rs.getValue(fname);
				String marker = "${fld:" + fname + "}";
				String marker_addon = "${" + fname + "}";
			
				if (value==null)
				{
					_template = StringUtil.replace(_template, marker, "");
					_template = StringUtil.replace(_template, marker_addon, "");
				}
				else
				{
					switch (f.getType())
					{
						case Types.VARCHAR:
						case Types.CHAR:
						case Types.LONGVARCHAR:
							String v = (String)value;
							v = StringUtil.replace(v,"'","");
							_template = StringUtil.replace(_template, marker, v);
							_template = StringUtil.replace(_template, marker_addon, v);
							break;
						
						case Types.DATE:
							java.util.Date d = (java.util.Date)value;
							_template = StringUtil.replace(_template, marker, StringUtil.formatDate(d, "yyyy-MM-dd"));
							_template = StringUtil.replace(_template, marker_addon, StringUtil.formatDate(d, "yyyy-MM-dd"));
							break;
						
						case Types.TIMESTAMP:
							java.util.Date d1 = (java.util.Date)value;
							_template = StringUtil.replace(_template, marker, StringUtil.formatDate(d1, "yyyy-MM-dd HH:mm:ss"));
							_template = StringUtil.replace(_template, marker_addon, StringUtil.formatDate(d1, "yyyy-MM-dd HH:mm:ss"));
							break;
						
						default:
							String n = dinamica.StringUtil.formatNumber(value, "#.######");
							n = dinamica.StringUtil.replace(n, ",", ".");									
							_template = StringUtil.replace(_template, marker, n);
							_template = StringUtil.replace(_template, marker_addon, n);
							break;
							
					}
				}
			
			}
		}
		return _template;
	}
}
