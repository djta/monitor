package com.ccms.imp;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

public class SearchClauseImp{

	public static String getFirstFilter(Db db, String queryFirst, String queryChildren, Map<String, String> titleMap, String filter_type) throws Throwable {
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
				getChildrenFilter(db, queryChildren, logic_type, p_id, node, titleMap, filter_type);
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
				String col_name = firstRs.getString("col_name");
				String is_save_code = firstRs.getString("is_save_code");
				
				if(col_name != null && !titleMap.containsKey(col_name)){
					throw new Throwable("条件的列头("+col_name+")在Excel中不存在，请检查！");
				}
				
				String excelCode = null;
				if(col_name != null){
					if("1".equals(is_save_code)){//是否保存代码值
						excelCode = "${fld:"+titleMap.get(col_name)+"_code}";
					}else{
						excelCode = "${fld:"+titleMap.get(col_name)+"}";
					}
				}
				
				if("0".endsWith(filter_type)){//除重
					if(clause_code == null || clause_code.length() == 0 || clause_filter == null || 
							clause_filter.length() == 0 || field_type == null || field_type.length() == 0){
						continue;
					}
					
					//只有当连接符为 is null 或者 is not null 时 或者 col_name 有值时 值可以为空
					if(col_name == null && ((clause_value == null || clause_value.length() == 0) && 
							(!"is null".equalsIgnoreCase(clause_filter) && !"is not null".equalsIgnoreCase(clause_filter)))){
						continue;
					}
				}else{//表格限定或者入库限定

					//只有当连接符为 is null 或者 is not null 时 或者 col_name 有值时 值可以为空
					if(col_name == null && ((clause_value == null || clause_value.length() == 0) && 
							(!"is null".equalsIgnoreCase(clause_filter) && !"is not null".equalsIgnoreCase(clause_filter)))){
						continue;
					}
				}
				
				if(sb.length() > 0){
					sb.append(" and ");
				}
				if(excelCode == null){
					if("is null".equalsIgnoreCase(clause_filter) || "is not null".equalsIgnoreCase(clause_filter)){
						sb.append(clause_code).append(" ").append(clause_filter).append(" ");
					}else if("in".equalsIgnoreCase(clause_filter) || "not in".equalsIgnoreCase(clause_filter)){
						String[] vs = clause_value.split(",");
						if(vs.length == 0) continue;
						sb.append(clause_code).append(" ").append(clause_filter).append(" (");
						for(int n=0;n<vs.length;n++){
							if(n != (vs.length-1)){
								if("int4".equalsIgnoreCase(field_type) || "int8".equalsIgnoreCase(field_type) || "numeric".equalsIgnoreCase(field_type) 
										|| "day".equalsIgnoreCase(field_type)){
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
										|| "day".equalsIgnoreCase(field_type)){
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
								|| "day".equalsIgnoreCase(field_type)){
							sb.append(" ").append(clause_value).append(" ");
						}else if("date".equalsIgnoreCase(field_type)){
							sb.append(" {d '").append(clause_value).append("'} ");
						}else if("timestamp".equalsIgnoreCase(field_type)){
							sb.append(" {ts '").append(clause_value).append("'} ");
						}else{
							//替换DEF
							clause_value = StringUtil.replace(clause_value, "${DEF:", "${def:");
							//首先判断输入值中是否存在特殊标记符
							Set<String> mark = getMarkers(clause_value);
							if(mark.size() > 0){
								//替换标记
								for(String fld : mark){
									if(titleMap.containsKey(fld)){
										clause_value = StringUtil.replace(clause_value, "${"+fld+"}", "${fld:"+titleMap.get(fld)+"_code}");
									}
								}
								sb.append(" ").append(clause_value).append(" ");
							}else{
								//单引号需要在界面上面自己写上去
								sb.append(" ").append(clause_value).append(" ");
							}
						}
					}
				}else{//比较excel与数据库字段(in 和 not in 排除)
					if("is null".equalsIgnoreCase(clause_filter) || "is not null".equalsIgnoreCase(clause_filter)){
						sb.append(excelCode).append(" ").append(clause_filter).append(" ");
					}else{
						if("0".endsWith(filter_type)){//除重
							sb.append(clause_code).append(" ").append(clause_filter).append(" ").append(excelCode);
						}else{
							if("in".equalsIgnoreCase(clause_filter) || "not in".equalsIgnoreCase(clause_filter)){
								throw new Throwable("入库校验不支持 in 或者 not in 的查询，请检查！");
							}
							sb.append(excelCode).append(" ").append(clause_filter);
							if("int4".equalsIgnoreCase(field_type) || "int8".equalsIgnoreCase(field_type) || "numeric".equalsIgnoreCase(field_type)
									|| "day".equalsIgnoreCase(field_type)){
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
			}
		}
		return sb.toString();
	}
	
	public static void getChildrenFilter(Db db, String queryChildren, String p_logic_type, String p_id, StringBuffer sql, Map<String, String> titleMap, String filter_type) throws Throwable {
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
				getChildrenFilter(db, queryChildren, logic_type, tuid, sb, titleMap, filter_type);
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
				String col_name = rs.getString("col_name");
				String is_save_code = rs.getString("is_save_code");
				
				if(col_name != null && !titleMap.containsKey(col_name)){
					throw new Throwable("条件的列头("+col_name+")在Excel中不存在，请检查！");
				}
				
				String excelCode = null;
				if(col_name != null){
					if("1".equals(is_save_code)){//是否保存代码值
						excelCode = "${fld:"+titleMap.get(col_name)+"_code}";
					}else{
						excelCode = "${fld:"+titleMap.get(col_name)+"}";
					}
				}
				
				if("0".endsWith(filter_type)){//除重
					if(clause_code == null || clause_code.length() == 0 || clause_filter == null || 
							clause_filter.length() == 0 || field_type == null || field_type.length() == 0){
						continue;
					}
					
					//只有当连接符为 is null 或者 is not null 时 或者 col_name 有值时 值可以为空
					if(col_name == null && ((clause_value == null || clause_value.length() == 0) && 
							(!"is null".equalsIgnoreCase(clause_filter) && !"is not null".equalsIgnoreCase(clause_filter)))){
						continue;
					}
				}else{//表格限定或者入库限定

					//只有当连接符为 is null 或者 is not null 时 或者 col_name 有值时 值可以为空
					if(col_name == null && ((clause_value == null || clause_value.length() == 0) && 
							(!"is null".equalsIgnoreCase(clause_filter) && !"is not null".equalsIgnoreCase(clause_filter)))){
						continue;
					}
				}
				
				i ++;
				if(i == 1){
					sql.append(" ");
				}else{
					sql.append(" ").append(p_logic_type).append(" ");
				}
				
				if(excelCode == null){
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
							//替换DEF
							clause_value = StringUtil.replace(clause_value, "${DEF:", "${def:");
							//首先判断输入值中是否存在特殊标记符
							Set<String> mark = getMarkers(clause_value);
							if(mark.size() > 0){
								//替换标记
								for(String fld : mark){
									if(titleMap.containsKey(fld)){
										clause_value = StringUtil.replace(clause_value, "${"+fld+"}", "${fld:"+titleMap.get(fld)+"_code}");
									}
								}
								sql.append(" ").append(clause_value).append(" ");
							}else{
								//单引号需要在界面上面自己写上去
								sql.append(" ").append(clause_value).append(" ");
							}
						}
					}
				}else{//比较excel与数据库字段(in 和 not in 排除)
					if("is null".equalsIgnoreCase(clause_filter) || "is not null".equalsIgnoreCase(clause_filter)){
						sql.append(excelCode).append(" ").append(clause_filter).append(" ");
					}else{
						if("0".endsWith(filter_type)){//除重
							sql.append(clause_code).append(" ").append(clause_filter).append(" ").append(excelCode);
						}else{
							if("in".equalsIgnoreCase(clause_filter) || "not in".equalsIgnoreCase(clause_filter)){
								throw new Throwable("入库校验不支持 in 或者 not in 的查询，请检查！");
							}
							sql.append(excelCode).append(" ").append(clause_filter);
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
					}
				}
			}
		}
	}
	
	public static Set<String> getMarkers(String _template) throws Throwable
	{	
		
		int pos = 0;
		Set<String> array = new HashSet<String>();
		
		while ( pos >= 0 )
		{
			int pos1 = 0;
			int pos2 = 0;
			int newPos = 0;

			pos1 = _template.indexOf("${", pos);
			if (pos1>=0)
			{
				newPos = pos1+2;
				pos2 = _template.indexOf("}", newPos);
				
				if (pos2>0)
				{
					String fld = _template.substring(newPos, pos2);
					array.add(fld);
				}
				else
				{
					throw new Throwable( "标记未正确关闭！" );
				}
				pos = pos2 + 1;
			}
			else
			{
				pos = -1;
			}
		}
		return array;
		
	}
}
