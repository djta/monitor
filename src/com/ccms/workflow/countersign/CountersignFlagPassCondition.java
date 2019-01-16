package com.ccms.workflow.countersign;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ccms.Constants;
import com.ccms.authority.AuthorityBean;
import com.ccms.workflow.util.GenericTransactionForWF;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.WorkflowContext;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

public class CountersignFlagPassCondition extends GenericTransactionForWF implements Condition{
	
	@SuppressWarnings("unchecked")
	public boolean passesCondition(Map transientVars, Map args, PropertySet ps) {
        try {
            WorkflowContext context = (WorkflowContext) transientVars.get("context");
            String authority = (String) args.get("authority");
            
            String caller = context.getCaller();
            
            if(caller == null) return false;
            
            WorkflowEntry entry = (WorkflowEntry) transientVars.get("entry");
    		long wfid = entry.getId();
            int stepId = 0;
    		String stepIdVal = (String) args.get("stepId");
    		if (stepIdVal != null) {
                try {
                    stepId = Integer.parseInt(stepIdVal);
                } catch (Exception ex) {
                }
            }
    		Db db = getDb();
    		if (stepId != 0) {//首节点时step为null
    			String queryStepType = getLocalResource("/com/ccms/workflow/sql/query_step_type_byid.sql");
    			queryStepType = StringUtil.replace(queryStepType, "${id}", stepIdVal);
    			Recordset rsStep = db.get(queryStepType);
    			rsStep.first();
    			String stepType = rsStep.getString("step_type");
    			if("2".equals(stepType)){//如果是join节点的话需要判断其分支上的所有节点是否已完成
    				Set<Integer> splitStepId = new HashSet<Integer>();
    				String queryStep = getLocalResource("/com/ccms/workflow/sql/query_split_step.sql");
    				String queryCurrentStep = getLocalResource("/com/ccms/workflow/sql/query_all_current_steps.sql");
    				searchSplitSteps(db, queryStep, splitStepId, stepId);//递归查询出分支上所有节点
    				
    				queryCurrentStep = StringUtil.replace(queryCurrentStep, "${entry_id}", wfid+"");
    				Recordset rs = db.get(queryCurrentStep);
    				while(rs.next()){
    					if(splitStepId.contains(rs.getInteger("step_id"))){
                        	//join 节点有未完成的分支节点
                        	return false;
                        }
    				}
    			}
    			
    			//如果上一节点是会签节点，则该动作是由系统用户自动执行的，不需要判断数据所属人
    			if(!"3".equals(stepType)){
    				List currentSteps = (List) transientVars.get("currentSteps");
                    for (Iterator iterator = currentSteps.iterator();iterator.hasNext();) {
                        Step step = (Step) iterator.next();
                        if (stepId == step.getStepId() && step.getOwner() != null) {
                            if (!context.getCaller().equals(step.getOwner())) {
                                return false;
                            }
                            break;
                        }
                    }
    			}
            }
    		
    		//权限组解析
            if(authority != null && authority.length() > 0){
            	AuthorityBean auth = new AuthorityBean(db, caller);
            	Recordset rsUser = db.get(getSQL(auth.spliceGroupSql(Integer.parseInt(authority), "2", true), null));
            	rsUser.first();
            	if (rsUser.getInt("is_pass") ==0 ){
            		return false;
            	}
            }
    		
            //检验是否通过
			if (stepId != 0){
    			String queryStep = getLocalResource("/com/ccms/workflow/sql/countersign/query_step_flag_byid.sql");
    			queryStep = StringUtil.replace(queryStep, "${id}", stepIdVal);
    			Recordset rsStep = db.get(queryStep);
    			if(rsStep.next()){
	    			String countersign_type = rsStep.getString("countersign_type");
    				String flagSql = getLocalResource("/com/ccms/workflow/sql/countersign/query_flag_pass.sql");
    				
    				flagSql = StringUtil.replace(flagSql, "${id}", wfid+"");
    				flagSql = StringUtil.replace(flagSql, "${step_id}", stepIdVal);
    				
    				Recordset rsFlag = db.get(flagSql);
    				boolean flag = false;
    				if("1".equals(countersign_type)){//一票否决
    					while(rsFlag.next()){
    						String is_pass = rsFlag.getString("is_pass");
    						if(Constants.OPTIONAL_NO.equals(is_pass)){
    							flag = true;
    							break;
    						}
    					}
    				}else if("2".equals(countersign_type)){//一票通过
    					while(rsFlag.next()){
    						String is_pass = rsFlag.getString("is_pass");
    						if(Constants.OPTIONAL_YES.equals(is_pass)){
    							flag = true;
    							break;
    						}
    					}
    				}else if("3".equals(countersign_type)){//按比例通过
    					Double perVal = rsStep.getDouble("countersign_per");
    					Double count = 0d;
    					while(rsFlag.next()){
    						String is_pass = rsFlag.getString("is_pass");
    						if(Constants.OPTIONAL_YES.equals(is_pass)){
    							count++;
    						}
    					}
    					if((count/rsFlag.getRecordCount())>=(perVal/100)){
    						flag = true;
    					}
    				}
    				if(flag == false){
    					return false;
    				}
    			}
			}
            
    		String queryTable = getLocalResource("/com/ccms/workflow/sql/query_table.sql");
    		queryTable = StringUtil.replace(queryTable, "${id}", wfid+"");
    		Recordset rsTable = db.get(queryTable);
    		if(rsTable.next()){
    			String table_code = rsTable.getString("table_code");
    			String bpk_field = rsTable.getString("bpk_field");
    			String pk_value = rsTable.getString("pk_value");
    			
    			//通过业务数据过滤条件
    			Integer actionId = (Integer) transientVars.get("actionId");
    			String queryRule = getLocalResource("/com/ccms/workflow/sql/query_node_rule.sql");
    			String queryBusiRule = getLocalResource("/com/ccms/workflow/sql/query_rule_validator.sql");
    			
    			/*取得条件定义*/
    			queryRule = StringUtil.replace(queryRule, "${action_id}", actionId.toString());
    			Recordset rsRule = db.get(queryRule);
    			
    			/*取得条件子句*/
    			String strRuleFilter = buildArrayRecordsetFilter(rsRule);
    			queryBusiRule = StringUtil.replace(queryBusiRule, "${table}", table_code);
    			queryBusiRule = StringUtil.replace(queryBusiRule, "${bpk_field}", bpk_field);
    			queryBusiRule = StringUtil.replace(queryBusiRule, "${pk_value}", pk_value);
    			queryBusiRule = StringUtil.replace(queryBusiRule, "${filter}", strRuleFilter);
    			/*查询条件是否成立 */
    			Recordset rsRuleValidator = db.get(queryBusiRule);
    			rsRuleValidator.first();
    			
    			if (rsRuleValidator.getInteger("is_pass").intValue() > 0)	/*本条规则满足条件*/
    			{
    				return true;
    			}
    		}else{//初始化时取不到业务信息
    			return true;
    		}
            return false;
        }catch (Throwable e) {
        	e.printStackTrace();
		}finally{
			try {
				super.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
        return false;
    }
	
	private String buildArrayRecordsetFilter (Recordset rs) throws Throwable
	{
		StringBuffer sql = new StringBuffer(256);
		int i = 0;

		rs.top();
		while (rs.next()){
		
			String clause_code = rs.getString("rule_field");
			String clause_filter = rs.getString("rule_operator");
			String clause_value = rs.getString("rule_value");
			//如果值里有引号,替换掉引号
			clause_value = StringUtil.replace(clause_value, "'", "");
			String clause_logic = rs.getString("rule_logic");
			String field_type = rs.getString("field_type");
			String left_prefix = rs.getString("left_prefix");
			String right_suffix = rs.getString("right_suffix");
			
			if(clause_code == null || clause_code.length() == 0 || clause_filter == null || 
					clause_filter.length() == 0 || field_type == null || field_type.length() == 0){
				continue;
			}
			//只有当连接符为 is null 或者 is not null 时 值可以为空
			if((clause_value == null || clause_value.length() == 0) && 
					(!"is null".equalsIgnoreCase(clause_filter) && !"is not null".equalsIgnoreCase(clause_filter))){
				continue;
			}
			
			i ++;
			if(i == 1){
				sql.append(" ").append(clause_logic).append(" ");
			}else{
				sql.append(" ").append(clause_logic).append(" ");
			}

			//左括号
			if(left_prefix!=null && !left_prefix.equals("")){
				sql.append(" ").append(left_prefix).append(" ");
			}
			
			if("is null".equalsIgnoreCase(clause_filter) || "is not null".equalsIgnoreCase(clause_filter)){
				sql.append(clause_code).append(" ").append(clause_filter).append(" ");
			}else if("in".equalsIgnoreCase(clause_filter) || "not in".equalsIgnoreCase(clause_filter)){
				String[] vs = clause_value.split(",");
				if(vs.length == 0) continue;
				sql.append(clause_code).append(" ").append(clause_filter).append(" (");
				for(int n=0;n<vs.length;n++){
					if(n != (vs.length-1)){
						if("int4".equalsIgnoreCase(field_type) || "int8".equalsIgnoreCase(field_type) || "numeric".equalsIgnoreCase(field_type)
								|| "day".equalsIgnoreCase(field_type)){
							sql.append(vs[n]).append(",");
						}else if("date".equalsIgnoreCase(field_type)){
							sql.append("{d '").append(vs[n]).append("'}").append(",");
						}else if("timestamp".equalsIgnoreCase(field_type)){
							sql.append("{ts '").append(vs[n]).append("'}").append(",");
						}else{
							sql.append("'").append(vs[n]).append("'").append(",");
						}
					}else{
						if("int4".equalsIgnoreCase(field_type) || "int8".equalsIgnoreCase(field_type) || "numeric".equalsIgnoreCase(field_type)
								|| "day".equalsIgnoreCase(field_type)){
							sql.append(vs[n]);
						}else if("date".equalsIgnoreCase(field_type)){
							sql.append("{d '").append(vs[n]).append("'}");
						}else if("timestamp".equalsIgnoreCase(field_type)){
							sql.append("{ts '").append(vs[n]).append("'}");
						}else{
							sql.append("'").append(vs[n]).append("'");
						}
					}
				}
				sql.append(") ");
			}else{
				sql.append(clause_code).append(" ").append(clause_filter);
				if("int4".equalsIgnoreCase(field_type) || "int8".equalsIgnoreCase(field_type) || "numeric".equalsIgnoreCase(field_type)
						|| "day".equalsIgnoreCase(field_type)){
					sql.append(" ").append(clause_value).append(" ");
				}else if("date".equalsIgnoreCase(field_type)){
					sql.append(" {d '").append(clause_value).append("'} ");
				}else if("timestamp".equalsIgnoreCase(field_type)){
					sql.append(" {ts '").append(clause_value).append("'} ");
				}else{
					sql.append(" '").append(clause_value).append("' ");
				}
			}

			//右括号
			if(right_suffix!=null && !right_suffix.equals("")){
				sql.append(" ").append(right_suffix).append(" ");
			}

		}

		return sql.toString();				
	}
	
	private void searchSplitSteps(Db db, String query, Set<Integer> stepSet, Integer step_id) throws Throwable{
		String queryStep = StringUtil.replace(query, "${id}", step_id.toString());
		Recordset rs = db.get(queryStep);
		while(rs.next()){
			Integer node_id = rs.getInteger("node_id");
			stepSet.add(node_id);
			searchSplitSteps(db, query, stepSet, node_id);
		}
	}

}
