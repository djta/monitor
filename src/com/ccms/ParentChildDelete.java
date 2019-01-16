package com.ccms;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.RecordsetField;
import dinamica.StringUtil;
import dinamica.xml.Element;

public class ParentChildDelete extends GenericTableManager {

	dinamica.xml.Document docXML = null;
	String query = " select ${key} from ${table} where ${pkey}=${id} ";
	String delete = " delete from ${table} where ${pkey}=${id} ";

	public int service(Recordset inputParams) throws Throwable
	{
		int r = super.service(inputParams);
		
		docXML = getConfig().getDocument();
		dinamica.xml.Element e = docXML.getElement("//group-master");
		
		if (e==null)
			throw new Throwable("没有发现元素 <group-master></group-master>");
		
		String deleteMain = e.getAttribute("sql");
		if (deleteMain==null)
			throw new Throwable("<group-master></group-master> 中没有属性 'sql'");
		
		String idParam = e.getAttribute("id");
		if (idParam==null)
			throw new Throwable("<group-master></group-master> 中没有属性 'id'");
		
		String deleteMainSql = getSQL(getResource(deleteMain),inputParams);
		RecordsetField id_field = inputParams.getField(idParam);
		String id = inputParams.getString(idParam);
		//如果为字符型
		if(id_field.getType()==Types.VARCHAR)
			id = "'"+id+"'";
		
		String childTag = e.getAttribute("child");
		if (childTag==null){
			getDb().exec(deleteMainSql);
		}else{
			List<String> delList = new ArrayList<String>();
			//删除最外层表
			delList.add(deleteMainSql);
			String[] childs = childTag.split(",");
			for(int i=0;i<childs.length;i++){
				String tagName = "//group-master/"+childs[i];
				Element e2 = docXML.getElement(tagName);
				if (e2==null)
					throw new Throwable("没有发现元素 "+tagName);
				
				String table = e2.getAttribute("table");
				String key = e2.getAttribute("key");
				String pkey = e2.getAttribute("pkey");
				
				String delSql = StringUtil.replace(delete, "${table}", table);
				delSql = StringUtil.replace(delSql, "${pkey}", pkey);
				delSql = StringUtil.replace(delSql, "${id}", id);
				
				delList.add(delSql);
				
				String querySql = StringUtil.replace(query, "${table}", table);
				querySql = StringUtil.replace(querySql, "${key}", key);
				querySql = StringUtil.replace(querySql, "${pkey}", pkey);
				querySql = StringUtil.replace(querySql, "${id}", id);
				
				String childTag2 = e2.getAttribute("child");
				if (childTag2!=null){
					empaquetaRecordset(delList, querySql, key, tagName, childTag2);
				}
			}
			Db db = getDb();
			for(int j=(delList.size()-1);j>=0;j--){
				db.addBatchCommand(delList.get(j));
			}
			db.exec();
		}
		
		return r; 
	}
	
	public void empaquetaRecordset (List<String> delList, String query2, String key2, String tagName, String childTag) throws Throwable {
		
		Recordset rs = getDb().get(query2);
		
		String[] childs = childTag.split(",");
		for(int i=0;i<childs.length;i++){
			String tagName2 = tagName+"/"+childs[i];
			Element e2 = docXML.getElement(tagName2);
			if (e2!=null) {
				rs.top();
				while (rs.next()) {
						String table = e2.getAttribute("table");
						String key = e2.getAttribute("key");
						String pkey = e2.getAttribute("pkey");
						
						String id = rs.getString(key2);
						String delSql = StringUtil.replace(delete, "${table}", table);
						delSql = StringUtil.replace(delSql, "${pkey}", pkey);
						delSql = StringUtil.replace(delSql, "${id}", id);
						
						delList.add(delSql);
						
						String querySql = StringUtil.replace(query, "${table}", table);
						querySql = StringUtil.replace(querySql, "${key}", key);
						querySql = StringUtil.replace(querySql, "${pkey}", pkey);
						querySql = StringUtil.replace(querySql, "${id}", id);
						
						String childTag2 = e2.getAttribute("child");
						if (childTag2!=null){
							empaquetaRecordset(delList, querySql, key, tagName2, childTag2);
						}
					}
				}
		}
	}
}
