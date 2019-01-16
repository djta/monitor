package com.ccms.workflow;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

public class CopyWorkflowByTemplate extends GenericTransaction{
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		
		String queryWfm = getConfig().getConfigValue("query-wfm","query-wfm.sql");
		String queryWfmSQL = getSQL(getResource(queryWfm),inputParams);
		Recordset wfm_rs = getDb().get(queryWfmSQL);
		if(wfm_rs.getRecordCount() <= 0){
			return 1;
		}
		wfm_rs.next();
		Integer wfm_id = wfm_rs.getInteger("tuid");
		String xmlStr = wfm_rs.getString("xml_value");
		
		String insertWfm = getConfig().getConfigValue("insert-wfm","insert-wfm.sql");
		String insertWfmSQL = getSQL(getSQL(getResource(insertWfm),wfm_rs),inputParams);
		getDb().exec(insertWfmSQL);
		
		
		if (xmlStr != null && xmlStr.length() > 10) {
			Db db = getDb();
			
			//新旧主键映射<原主键,新主键>，由序列循环构造新主键
			Map<Integer,Integer> nodeTuidMap = new HashMap<Integer,Integer>();
			Map<Integer,Integer> actionTuidMap = new HashMap<Integer,Integer>();
			List<String> authorityIdList = new ArrayList<String>();
			
			JSONObject jsonObj = new JSONObject(xmlStr);
			
			JSONObject stepObj_cnt = jsonObj.getJSONObject("nodes");
			String[] steps_cnt = JSONObject.getNames(stepObj_cnt);
			if(steps_cnt != null && steps_cnt.length > 0){
				for(int i=0;i<steps_cnt.length;i++){
					JSONObject step_cnt = stepObj_cnt.getJSONObject(steps_cnt[i]);
					String type_cnt = step_cnt.getString("type");
					//当空白节点时忽略
					if("blank".equalsIgnoreCase(type_cnt)){
						continue;
					}
					Integer node_id_cnt = step_cnt.getInt("node_id");
					if(node_id_cnt == null){
						continue;
					}
					if(!nodeTuidMap.containsKey(node_id_cnt)){
						int node_id_new = getDefaultSequenceValue(db);
						nodeTuidMap.put(node_id_cnt, node_id_new);
					}
				}
			}
			
			JSONObject actionObj_cnt = jsonObj.getJSONObject("lines");
			String[] actions_cnt = JSONObject.getNames(actionObj_cnt);
			if(actions_cnt != null && actions_cnt.length > 0){
				for(int i=0;i<actions_cnt.length;i++){
					JSONObject action_cnt = actionObj_cnt.getJSONObject(actions_cnt[i]);
					//当暂存节点或者没有完整配置信息时忽略该动作
					if(action_cnt.has("action_id") == false) continue;
					Integer action_id_cnt = action_cnt.getInt("action_id");
					if(action_id_cnt == null) continue;
					if(!actionTuidMap.containsKey(action_id_cnt)){
						int action_id_new = getDefaultSequenceValue(db);
						actionTuidMap.put(action_id_cnt, action_id_new);
					}
				}
			}
			
			JSONObject wfmObj = jsonObj.getJSONObject("flow");
			Integer wfm_id_json = wfmObj.getInt("wfm_id");
			String wfm_name_json = wfmObj.getString("wfm_name");
			String wfm_status_json = wfmObj.getString("wfm_status");
			String is_template_json = wfmObj.getString("is_template");
			String remark_json = wfmObj.getString("remark");
			
			
			String insertRule = getResource("insert-rule.sql");

			String insertNodeto = getResource("insert-nodeto.sql");

			String insertNode = getResource("insert-node.sql");

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
					if(actionTuidMap.containsKey(action_id)){
						action_id = actionTuidMap.get(action_id);
					}else{
						throw new Throwable("新动作匹配原动作ID失败");
					}
					Integer from_node_id = action.getInt("from_node_id");
					if(nodeTuidMap.containsKey(from_node_id)){
						from_node_id = nodeTuidMap.get(from_node_id);
					}else{
						throw new Throwable("新节点匹配原节点ID失败");
					}
					Integer to_node_id = action.getInt("to_node_id");
					if(nodeTuidMap.containsKey(to_node_id)){
						to_node_id = nodeTuidMap.get(to_node_id);
					}else{
						throw new Throwable("新节点匹配原节点ID失败");
					}

					String action_name = action.getString("action_name");
					String is_auto = action.getString("is_auto");
					String pre_class = action.getString("pre_class");
					String post_class = action.getString("post_class");
					String condition_class = action.getString("condition_class");
					String authority_id = action.getString("authority_id");
					if(authority_id!=null && authority_id.length() > 0){
						if(!authorityIdList.contains(authority_id)){
							authorityIdList.add(authority_id);
						}
					}
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
					rsNodeto.setValue("authority_id", null);
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

			// step
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
					if(nodeTuidMap.containsKey(node_id)){
						node_id = nodeTuidMap.get(node_id);
					}else{
						throw new Throwable("新节点匹配原节点ID失败");
					}
					String node_name = step.getString("node_name");
					String old_status = step.getString("old_status");
					String curr_status = step.getString("status");
					Integer document_id = step.getInt("document_id");
					String remark = step.getString("remark");
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
			
			//更新wfm的xml_value字段
			String updateWfmSQL = getResource("update-wfm.sql");
			String xmlValueNew = "";
			xmlValueNew = StringUtil.replace(xmlStr, "\"wfm_id\":\""+wfm_id_json+"\"", "\"wfm_id\":\""+wfm_id+"\"");
			xmlValueNew = StringUtil.replace(xmlValueNew, "\"wfm_name\":\""+wfm_name_json+"\"", "\"wfm_name\":\""+wfm_rs.getString("wfm_name")+"\"");
			xmlValueNew = StringUtil.replace(xmlValueNew, "\"wfm_status\":\""+wfm_status_json+"\"", "\"wfm_status\":\"1\"");
			xmlValueNew = StringUtil.replace(xmlValueNew, "\"is_template\":\""+is_template_json+"\"", "\"is_template\":\"0\"");
			xmlValueNew = StringUtil.replace(xmlValueNew, "\"remark\":\""+remark_json+"\"", "\"remark\":\""+wfm_rs.getString("remark")+"\"");
			Iterator<Integer> node_tuid_it = nodeTuidMap.keySet().iterator();
			while(node_tuid_it.hasNext()){
				Integer node_id_value = node_tuid_it.next();
				xmlValueNew = StringUtil.replace(xmlValueNew, "\"node_id\":\""+node_id_value+"\"", "\"node_id\":\""+nodeTuidMap.get(node_id_value)+"\"");
				xmlValueNew = StringUtil.replace(xmlValueNew, "\"from_node_id\":\""+node_id_value+"\"", "\"from_node_id\":\""+nodeTuidMap.get(node_id_value)+"\"");
				xmlValueNew = StringUtil.replace(xmlValueNew, "\"to_node_id\":\""+node_id_value+"\"", "\"to_node_id\":\""+nodeTuidMap.get(node_id_value)+"\"");
			}
			Iterator<Integer> action_tuid_it = actionTuidMap.keySet().iterator();
			while(action_tuid_it.hasNext()){
				Integer action_id_value = action_tuid_it.next();
				xmlValueNew = StringUtil.replace(xmlValueNew, "\"action_id\":\""+action_id_value+"\"", "\"action_id\":\""+actionTuidMap.get(action_id_value)+"\"");
			}
			for(String authority_id_value:authorityIdList){
				xmlValueNew = StringUtil.replace(xmlValueNew, "\"authority_id\":\""+authority_id_value+"\"", "\"authority_id\":\"\"");
			}
			updateWfmSQL = StringUtil.replace(updateWfmSQL, "${tuid}", String.valueOf(wfm_id));
			updateWfmSQL = StringUtil.replace(updateWfmSQL, "${xml_value}", xmlValueNew);
			db.exec(updateWfmSQL);
		}

		return rc;

	}
	private int getDefaultSequenceValue(Db db) throws Throwable{
		return db.getIntColValue(getSQL("select ${seq:nextval@${schema}seq_default} as tuid from dual",null), "tuid");
	}
}
