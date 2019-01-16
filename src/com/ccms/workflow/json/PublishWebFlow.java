package com.ccms.workflow.json;

import java.io.StringReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import org.json.JSONObject;

import com.opensymphony.workflow.config.DefaultConfiguration;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class PublishWebFlow extends GenericTableManager {

	public int service(Recordset inputParams) throws Throwable {

		String xmlStr = inputParams.getString("xml_value");
		if (xmlStr != null && xmlStr.length() > 10) {
			Db db = getDb();
			
			JSONObject jsonObj = new JSONObject(xmlStr);

			String deleteRule = getSQL(getResource("delete-rule.sql"), inputParams);
			String insertRule = getResource("insert-rule.sql");

			String deleteNodeto = getSQL(getResource("delete-nodeto.sql"), inputParams);
			String insertNodeto = getResource("insert-nodeto.sql");

			String deleteNodeHelp = getSQL(getResource("delete-node-help.sql"), inputParams);
			String insertNodeHelp = getResource("insert-node-help.sql");
			
			String deleteNode = getSQL(getResource("delete-node.sql"), inputParams);
			String insertNode = getResource("insert-node.sql");

			String deleteWfm = getSQL(getResource("delete-wfm.sql"), inputParams);
			String insertWfm = getSQL(getResource("insert-wfm.sql"), inputParams);

			Recordset rsNode = new Recordset();
			rsNode.append("tuid", Types.INTEGER);
			rsNode.append("wfm_id", Types.INTEGER);
			rsNode.append("node_name", Types.VARCHAR);
			rsNode.append("node_type", Types.VARCHAR);
			rsNode.append("document_id", Types.INTEGER);
			rsNode.append("remark", Types.VARCHAR);
			rsNode.append("step_type", Types.VARCHAR);
			rsNode.append("old_status", Types.VARCHAR);
			rsNode.append("status", Types.VARCHAR);

			rsNode.append("child_wfm_id", Types.INTEGER);
			rsNode.append("countersign_per", Types.INTEGER);
			rsNode.append("countersign_type", Types.VARCHAR);
			rsNode.append("countersign_post", Types.INTEGER);

			Recordset rsNodeHelp = new Recordset();
			rsNodeHelp.append("tuid", Types.INTEGER);
			rsNodeHelp.append("wfm_id", Types.INTEGER);
			rsNodeHelp.append("node_id", Types.INTEGER);

			Recordset rsNodeto = new Recordset();
			rsNodeto.append("tuid", Types.INTEGER);
			rsNodeto.append("node_id", Types.INTEGER);
			rsNodeto.append("next_node", Types.INTEGER);
			rsNodeto.append("action_name", Types.VARCHAR);
			rsNodeto.append("is_auto", Types.VARCHAR);
			rsNodeto.append("pre_class", Types.VARCHAR);
			rsNodeto.append("post_class", Types.VARCHAR);
			rsNodeto.append("condition_class", Types.VARCHAR);
			rsNodeto.append("authority_id", Types.INTEGER);
			rsNodeto.append("quartz_delay", Types.INTEGER);
			rsNodeto.append("quartz_cron", Types.VARCHAR);
			rsNodeto.append("quartz_class", Types.VARCHAR);
			rsNodeto.append("quartz_timeout", Types.INTEGER);
			/*rsNodeto.append("cs_campaign_id", Types.VARCHAR);
			rsNodeto.append("cs_job_id", Types.INTEGER);*/
			rsNodeto.append("email_skill_id", Types.VARCHAR);
			rsNodeto.append("email_skill_subject", Types.VARCHAR);
			rsNodeto.append("email_skill_template_id", Types.INTEGER);
			rsNodeto.append("sms_skill_id", Types.VARCHAR);
			rsNodeto.append("sms_skill_template_id", Types.INTEGER);
			rsNodeto.append("remind_skill_id", Types.VARCHAR);
			rsNodeto.append("remind_skill_subject", Types.VARCHAR);
			rsNodeto.append("remind_skill_template_id", Types.INTEGER);
			rsNodeto.append("email_assign_user", Types.VARCHAR);
			rsNodeto.append("sms_assign_user", Types.VARCHAR);
			rsNodeto.append("remind_assign_user", Types.VARCHAR);

			Recordset rsRule = new Recordset();
			rsRule.append("node_to_id", Types.INTEGER);
			rsRule.append("rule_field", Types.INTEGER);
			rsRule.append("rule_operator", Types.VARCHAR);
			rsRule.append("rule_value", Types.VARCHAR);
			rsRule.append("rule_logic", Types.VARCHAR);
			rsRule.append("left_prefix", Types.VARCHAR);
			rsRule.append("right_suffix", Types.VARCHAR);

			JSONObject actionObj = jsonObj.getJSONObject("lines");
			String[] actions = JSONObject.getNames(actionObj);
			if(actions != null && actions.length > 0){
				for(int i=0;i<actions.length;i++){
					JSONObject action = actionObj.getJSONObject(actions[i]);
					//当暂存节点或者没有完整配置信息时忽略该动作
					if(action.has("action_id") == false) continue;
					Integer action_id = action.getInt("action_id");
					if(action_id == null) continue;
					Integer from_node_id = action.getInt("from_node_id");
					Integer to_node_id = action.getInt("to_node_id");

					String action_name = action.getString("action_name");
					String is_auto = action.getString("is_auto");
					String pre_class = action.getString("pre_class");
					String post_class = action.getString("post_class");
					String condition_class = action.getString("condition_class");
					String authority_id = action.getString("authority_id");
					String quartz_delay = action.getString("quartz_delay");
					String quartz_cron = action.getString("quartz_cron");
					String quartz_class = action.getString("quartz_class");
					String quartz_timeout = action.getString("quartz_timeout");
					/*String cs_campaign_id = action.getString("cs_campaign_id");
					String cs_job_id = action.getString("cs_job_id");*/

					String email_skill_id = action.getString("email_skill_id");
					String email_skill_subject = action.getString("email_skill_subject");
					String email_skill_template_id = action.getString("email_skill_template_id");
					String sms_skill_id = action.getString("sms_skill_id");
					String sms_skill_template_id = action.getString("sms_skill_template_id");
					String remind_skill_id = action.getString("remind_skill_id");
					String remind_skill_subject = action.getString("remind_skill_subject");
					String remind_skill_template_id = action.getString("remind_skill_template_id");

					rsNodeto.addNew();
					rsNodeto.setValue("tuid", action_id);
					rsNodeto.setValue("node_id", from_node_id);
					rsNodeto.setValue("next_node", to_node_id);

					rsNodeto.setValue("action_name", action_name);
					rsNodeto.setValue("is_auto", is_auto);
					rsNodeto.setValue("pre_class", pre_class);
					rsNodeto.setValue("post_class", post_class);
					rsNodeto.setValue("condition_class", condition_class);
					rsNodeto.setValue("authority_id", (authority_id!=null && authority_id.length() > 0)?Integer.parseInt(authority_id):null);
					rsNodeto.setValue("quartz_delay", (quartz_delay == null || "".equals(quartz_delay)) ? null : Integer.parseInt(quartz_delay));
					rsNodeto.setValue("quartz_cron", quartz_cron);
					rsNodeto.setValue("quartz_class", quartz_class);
					rsNodeto.setValue("quartz_timeout", (quartz_timeout == null || "".equals(quartz_timeout)) ? null : Integer.parseInt(quartz_timeout));
					/*rsNodeto.setValue("cs_campaign_id", cs_campaign_id);
					rsNodeto.setValue("cs_job_id", (cs_job_id == null || "".equals(cs_job_id)) ? null : Integer.parseInt(cs_job_id));
*/
					rsNodeto.setValue("email_skill_id", email_skill_id);
					rsNodeto.setValue("email_skill_subject", email_skill_subject);
					rsNodeto.setValue("email_skill_template_id", (email_skill_template_id == null || "undefined".equals(email_skill_template_id) || "".equals(email_skill_template_id)) ? null : Integer
							.parseInt(email_skill_template_id));
					rsNodeto.setValue("sms_skill_id", sms_skill_id);
					rsNodeto.setValue("sms_skill_template_id", (sms_skill_template_id == null || "undefined".equals(sms_skill_template_id) ||  "".equals(sms_skill_template_id)) ? null : Integer
							.parseInt(sms_skill_template_id));
					rsNodeto.setValue("remind_skill_id", remind_skill_id);
					rsNodeto.setValue("remind_skill_subject", remind_skill_subject);
					rsNodeto.setValue("remind_skill_template_id", (remind_skill_template_id == null || "undefined".equals(remind_skill_template_id)  || "".equals(remind_skill_template_id)) ? null
							: Integer.parseInt(remind_skill_template_id));

					if(action.has("email_assign_user")){
						rsNodeto.setValue("email_assign_user", action.getString("email_assign_user"));
					}
					if(action.has("sms_assign_user")){
						rsNodeto.setValue("sms_assign_user", action.getString("sms_assign_user"));
					}
					if(action.has("remind_assign_user")){
						rsNodeto.setValue("remind_assign_user", action.getString("remind_assign_user"));
					}
					
					JSONObject ruleObj = action.getJSONObject("goto_rules");
					String[] rules = JSONObject.getNames(ruleObj);
					if(rules != null && rules.length > 0){
						for(int j=0;j<rules.length;j++){
							JSONObject rule = ruleObj.getJSONObject(rules[j]);
							String rule_field = rule.getString("rule_field");
							if (rule_field == null || "".equals(rule_field))
								continue;
							rsRule.addNew();
							rsRule.setValue("node_to_id", action_id);
							rsRule.setValue("rule_field", (rule_field == null || "".equals(rule_field)) ? null : Integer.parseInt(rule_field));
							rsRule.setValue("rule_operator", rule.getString("rule_operator"));
							rsRule.setValue("rule_value", rule.getString("rule_value"));
							rsRule.setValue("rule_logic", rule.getString("rule_logic"));
							rsRule.setValue("left_prefix", rule.getString("left_prefix"));
							rsRule.setValue("right_suffix", rule.getString("right_suffix"));
						}
					}
				}
			}

			// 删除node help
			db.addBatchCommand(deleteNodeHelp);
			db.exec();

			// step
			Integer wfm_id = inputParams.getInteger("wfm_id");
			JSONObject stepObj = jsonObj.getJSONObject("nodes");
			String[] steps = JSONObject.getNames(stepObj);
			if(steps != null && steps.length > 0){
				for(int i=0;i<steps.length;i++){
					JSONObject step = stepObj.getJSONObject(steps[i]);
					String type = step.getString("type");
					//当空白节点时忽略
					if("blank".equalsIgnoreCase(type)){
						continue;
					}
					Integer node_id = step.getInt("node_id");
					if(node_id == null){
						continue;
					}
					String node_name = step.getString("node_name");
					String old_status = step.getString("old_status");
					String curr_status = step.getString("status");
					Integer document_id = step.getInt("document_id");
					String remark = step.getString("remark");
					String node_help = null;
					if(step.has("node_help")){
						node_help = step.getString("node_help");
					}
					Integer node_type = step.getInt("node_type");
					Integer step_type = step.getInt("step_type");
					
					rsNode.addNew();
					rsNode.setValue("tuid", node_id);
					rsNode.setValue("wfm_id", wfm_id);
					rsNode.setValue("node_name", node_name);
					
					rsNode.setValue("node_type", node_type + "");
					rsNode.setValue("step_type", step_type + "");
					rsNode.setValue("old_status", old_status);
					rsNode.setValue("status", curr_status);
					rsNode.setValue("document_id", document_id);
					rsNode.setValue("remark", remark);
					
					if(rsNodeHelp.getRecordCount()==0){
						rsNodeHelp.addNew();
						rsNodeHelp.first();
					}
					rsNodeHelp.setValue("tuid", node_id);
					rsNodeHelp.setValue("wfm_id", wfm_id);
					rsNodeHelp.setValue("node_id", node_id);

					String dbType = getContext().getInitParameter("db");
					PreparedStatement p = null;
					try {
						String insertNodeHelpSql = getSQL(insertNodeHelp, rsNodeHelp);
						p = getConnection().prepareStatement(insertNodeHelpSql);
						if("postgresql".equalsIgnoreCase(dbType)){//postgresql 特殊处理
							p.setString(1, node_help);
						}else{
							StringReader sr = new StringReader(node_help);
							p.setClob(1, sr, node_help.length());
						}
						p.execute();
					} catch (SQLException sqe) {
						Throwable t = null;
						String msg = null;
						String date = StringUtil.formatDate(new java.util.Date(), "dd-MM-yyyy HH:mm:ss");

						if (sqe.getNextException() != null) {
							msg = sqe.getNextException().getMessage();
							t = sqe.getCause();
						} else {
							msg = sqe.getMessage();
							t = sqe;
						}
						System.err.println("[WARNING@" + date + "] Db.saveBlob failed: " + msg + " SQL: [" + insertWfm + "]");
						throw t;

					} catch (Throwable e) {
						throw e;
					} finally {
						if (p != null)
							p.close();
					}
					
					
					if(step_type == 3){//会签节点
						String countersign_type = step.getString("countersign_type");
						String countersign_per = step.getString("countersign_per");
						Integer child_wfm_id = step.getInt("child_wfm_id");
						
						Double countersignPer = (countersign_per != null && countersign_per.length() > 0) ? Double.parseDouble(countersign_per) : null;
						
						rsNode.setValue("child_wfm_id", child_wfm_id);
						rsNode.setValue("countersign_per", countersignPer);
						rsNode.setValue("countersign_type", countersign_type);
						
						if(step.has("countersign_post")){
							String countersign_post = step.getString("countersign_post");
							Integer cp = (countersign_post != null && countersign_post.length() > 0) ? Integer.parseInt(countersign_post) : null;
							rsNode.setValue("countersign_post", cp);
						}
						
					}else if(step_type == 4){//子流程节点
						Integer child_wfm_id = step.getInt("child_wfm_id");
						rsNode.setValue("child_wfm_id", child_wfm_id);
					}
					
				}
			}
			// 删除rule
			db.addBatchCommand(deleteRule);
			// 删除action
			db.addBatchCommand(deleteNodeto);
			// 删除node
			db.addBatchCommand(deleteNode);
			// 删除wfm
			db.addBatchCommand(deleteWfm);

			db.exec();

			// 添加wfm
			String dbType = getContext().getInitParameter("db");
			PreparedStatement p = null;
			try {
				p = getConnection().prepareStatement(insertWfm);
				if("postgresql".equalsIgnoreCase(dbType)){//postgresql 特殊处理
					p.setString(1, xmlStr);
					p.setString(2, xmlStr);
				}else{
					StringReader sr = new StringReader(xmlStr);
					StringReader sr2 = new StringReader(xmlStr);
					p.setClob(1, sr, xmlStr.length());
					p.setClob(2, sr2, xmlStr.length());
				}
				p.execute();
			} catch (SQLException sqe) {
				Throwable t = null;
				String msg = null;
				String date = StringUtil.formatDate(new java.util.Date(), "dd-MM-yyyy HH:mm:ss");

				if (sqe.getNextException() != null) {
					msg = sqe.getNextException().getMessage();
					t = sqe.getCause();
				} else {
					msg = sqe.getMessage();
					t = sqe;
				}
				System.err.println("[WARNING@" + date + "] Db.saveBlob failed: " + msg + " SQL: [" + insertWfm + "]");
				throw t;

			} catch (Throwable e) {
				throw e;
			} finally {
				if (p != null)
					p.close();
			}
			
			// 添加node
			rsNode.top();
			while (rsNode.next()) {
				String insert = getSQL(insertNode, rsNode);
				db.addBatchCommand(insert);
			}
			// 添加nodeto
			rsNodeto.top();
			while (rsNodeto.next()) {
				String insert = getSQL(insertNodeto, rsNodeto);
				db.addBatchCommand(insert);
			}
			// 添加rule
			rsRule.top();
			while (rsRule.next()) {
				String insert = getSQL(insertRule, rsRule);
				db.addBatchCommand(insert);
			}
			db.exec();
			
			Recordset rs = db.get(getSQL(getResource("query.sql"), inputParams));
			if(rs.next()){
				String wfm_name = rs.getString("wfm_name");
				//发布成功执行自动加载动作
				if(DefaultConfiguration.INSTANCE.saveWorkflow(wfm_name, null, true) == false){
					throw new Throwable("流程发布失败，请检查配置信息!");
				};
			}
		}

		return 0;

	}
}
