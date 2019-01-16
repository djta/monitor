package com.ccms.core.engine;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

public class SearchClauseUtil{

	/**
	 * 拼查询条件
	 * 人群定义条件、活动包含、活动排除
	 * @param db
	 * @param queryFirst
	 * @param queryChildren
	 * @param table_code
	 * @param cust_code
	 * @return
	 * @throws Throwable
	 */
	public static String getFilters(Db db, String queryFirst, String queryChildren, String table_code, String cust_code) throws Throwable {
		String queryFirst1 = StringUtil.replace(queryFirst, "${filter_type}", "0");
		String queryFirst2 = StringUtil.replace(queryFirst, "${filter_type}", "1");
		String queryFirst3 = StringUtil.replace(queryFirst, "${filter_type}", "2");
		String queryFirst4 = StringUtil.replace(queryFirst, "${filter_type}", "3");
		
		String queryChildren1 = StringUtil.replace(queryChildren, "${filter_type}", "0");
		String queryChildren2 = StringUtil.replace(queryChildren, "${filter_type}", "1");
		String queryChildren3 = StringUtil.replace(queryChildren, "${filter_type}", "2");
		String queryChildren4 = StringUtil.replace(queryChildren, "${filter_type}", "3");
		
		String filter1 = getFirstFilter(db, queryFirst1, queryChildren1);//人群限定定义
		String filter2 = getFirstFilter(db, queryFirst2, queryChildren2);//活动包含
		String filter3 = getFirstFilter(db, queryFirst3, queryChildren3);//活动排除
		String filter4 = getFirstFilter(db, queryFirst4, queryChildren4);//人群追加定义
		
		StringBuffer sb = new StringBuffer();
		if(filter1.length() > 0){
			sb.append(" and ").append(filter1);
		}
		if(filter2.length() > 0){
			if(filter2.indexOf("cs_task_pool")>=0){	//关联到t_ob_task_pool表,查is_done状态
				sb.append(" and exists (select cc_incident.incident_code from cc_incident inner join cs_task_pool on cs_task_pool.cust_code=cc_incident.cust_code where cc_incident.cust_code = ").append(table_code).append(".").append(cust_code).append(" and ").append(filter2).append(" ) ");
			} else {
				sb.append(" and exists (select cc_incident.incident_code from cc_incident where cc_incident.cust_code = ").append(table_code).append(".").append(cust_code).append(" and ").append(filter2).append(" ) ");				
			}
		}
		if(filter3.length() > 0){
			if(filter3.indexOf("cs_task_pool")>=0){	//关联到t_ob_task_pool表,查is_done状态
				sb.append(" and not exists (select cc_incident.incident_code from cc_incident inner join cs_task_pool on cs_task_pool.cust_code=cc_incident.cust_code where cc_incident.cust_code = ").append(table_code).append(".").append(cust_code).append(" and ").append(filter3).append(" ) ");
			} else {
				sb.append(" and not exists (select cc_incident.incident_code from cc_incident where cc_incident.cust_code = ").append(table_code).append(".").append(cust_code).append(" and ").append(filter3).append(" ) ");
			}
		}
		if(filter4.length() > 0){
			sb.append(" or ( ").append(filter4).append(" ) ");
		}
		
		return sb.toString();
	}
	
	public static String getFirstFilter(Db db, String queryFirst, String queryChildren) throws Throwable {
		StringBuffer sb = new StringBuffer();
		Recordset firstRs = db.get(queryFirst);
		while(firstRs.next()){
			String is_node = firstRs.getString("is_node");
			if("0".equals(is_node)){//节点
				String logic_type = firstRs.getString("logic_type");
				if(logic_type == null || logic_type.length() == 0){
					continue;
				}
				StringBuffer node = new StringBuffer();
				String p_id = firstRs.getString("tuid");
				getChildren(db, queryChildren, logic_type, p_id, node);
				if(node.length() > 0){
					if(sb.length() > 0){
						sb.append(" and ");
					}
					sb.append(" (").append(node.toString()).append(") ");
				}
			}else if("1".equals(is_node)){//叶子节点
				String clause_code = firstRs.getString("clause_code");
				String clause_filter = firstRs.getString("clause_filter");
				String clause_value = firstRs.getString("clause_value");
				String field_type = firstRs.getString("field_type");
				if(clause_code == null || clause_code.length() == 0 || clause_filter == null || 
						clause_filter.length() == 0 || field_type == null || field_type.length() == 0){
					continue;
				}
				
				//只有当连接符为 is null 或者 is not null 时 值可以为空
				if((clause_value == null || clause_value.length() == 0) && 
						(!"is null".equalsIgnoreCase(clause_filter) && !"is not null".equalsIgnoreCase(clause_filter))){
					continue;
				}
				
				if(sb.length() > 0){
					sb.append(" and ");
				}
				if("is null".equalsIgnoreCase(clause_filter) || "is not null".equalsIgnoreCase(clause_filter)){
					sb.append(clause_code).append(" ").append(clause_filter).append(" ");
				}else if("in".equalsIgnoreCase(clause_filter) || "not in".equalsIgnoreCase(clause_filter)){
					String[] vs = clause_value.split(",");
					if(vs.length == 0) continue;
					sb.append(clause_code).append(" ").append(clause_filter).append(" (");
					for(int n=0;n<vs.length;n++){
						if(n != (vs.length-1)){
							if("int4".equalsIgnoreCase(field_type) || "int8".equalsIgnoreCase(field_type) || "numeric".equalsIgnoreCase(field_type) 
									|| "day".equalsIgnoreCase(field_type) || "integer".equalsIgnoreCase(field_type)){
								sb.append(vs[n]).append(",");
							}else if("date".equalsIgnoreCase(field_type)){
								sb.append("{d '").append(vs[n]).append("'}").append(",");
							}else if("timestamp".equalsIgnoreCase(field_type)){
								sb.append("{ts '").append(vs[n]).append("'}").append(",");
							}else{
								sb.append("'").append(vs[n]).append("'").append(",");
							}
						}else{
							if("int4".equalsIgnoreCase(field_type) || "int8".equalsIgnoreCase(field_type) || "numeric".equalsIgnoreCase(field_type)
									|| "day".equalsIgnoreCase(field_type) || "integer".equalsIgnoreCase(field_type)){
								sb.append(vs[n]);
							}else if("date".equalsIgnoreCase(field_type)){
								sb.append("{d '").append(vs[n]).append("'}");
							}else if("timestamp".equalsIgnoreCase(field_type)){
								sb.append("{ts '").append(vs[n]).append("'}");
							}else{
								sb.append("'").append(vs[n]).append("'");
							}
						}
					}
					sb.append(") ");
				}else{
					sb.append(clause_code).append(" ").append(clause_filter);
					if("int4".equalsIgnoreCase(field_type) || "int8".equalsIgnoreCase(field_type) || "numeric".equalsIgnoreCase(field_type)
							|| "day".equalsIgnoreCase(field_type) || "integer".equalsIgnoreCase(field_type)){
						sb.append(" ").append(clause_value).append(" ");
					}else if("date".equalsIgnoreCase(field_type)){
						sb.append(" {d '").append(clause_value).append("'} ");
					}else if("timestamp".equalsIgnoreCase(field_type)){
						sb.append(" {ts '").append(clause_value).append("'} ");
					}else{
						sb.append(" '").append(clause_value).append("' ");
					}
				}
			}
		}
		return sb.toString();
	}
	
	public static void getChildren(Db db, String queryChildren, String p_logic_type, String p_id, StringBuffer sql) throws Throwable {
		String query = StringUtil.replace(queryChildren, "${p_id}", p_id);
		Recordset rs = db.get(query);
		int i = 0;
		while(rs.next()){
			String is_node = rs.getString("is_node");
			if("0".equals(is_node)){//节点
				String logic_type = rs.getString("logic_type");
				if(logic_type == null || logic_type.length() == 0){
					continue;
				}
				String tuid = rs.getString("tuid");
				StringBuffer sb = new StringBuffer();
				getChildren(db, queryChildren, logic_type, tuid, sb);
				if(sb.length() > 0){
					i ++;
					if(i == 1){
						sql.append(" (").append(sb.toString()).append(") ");
					}else{
						sql.append(" ").append(p_logic_type).append(" (").append(sb.toString()).append(") ");
					}
				}
			}else if("1".equals(is_node)){//叶子节点
				String clause_code = rs.getString("clause_code");
				String clause_filter = rs.getString("clause_filter");
				String clause_value = rs.getString("clause_value");
				String field_type = rs.getString("field_type");
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
					sql.append(" ");
				}else{
					sql.append(" ").append(p_logic_type).append(" ");
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
									|| "day".equalsIgnoreCase(field_type) || "integer".equalsIgnoreCase(field_type)){
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
									|| "day".equalsIgnoreCase(field_type) || "integer".equalsIgnoreCase(field_type)){
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
							|| "day".equalsIgnoreCase(field_type) || "integer".equalsIgnoreCase(field_type)){
						sql.append(" ").append(clause_value).append(" ");
					}else if("date".equalsIgnoreCase(field_type)){
						sql.append(" {d '").append(clause_value).append("'} ");
					}else if("timestamp".equalsIgnoreCase(field_type)){
						sql.append(" {ts '").append(clause_value).append("'} ");
					}else{
						sql.append(" '").append(clause_value).append("' ");
					}
				}
			}
		}
	}
}
