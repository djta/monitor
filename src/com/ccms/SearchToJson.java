package com.ccms;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import dinamica.*;

public class SearchToJson extends GenericTransaction
{

	public int service(Recordset inputs) throws Throwable
	{

		int rc = super.service(inputs);

		String sqlFile = getConfig().getConfigValue("sql-template","query.sql");

		String qBase = getSQL(getResource(sqlFile), inputs);

		
		Db db=getDb();
		
		Recordset rsRecordset= db.get(qBase);
		HashMap<String, RecordsetField> fieldsMap=rsRecordset.getFields();
		ArrayList<String> alField=new ArrayList<String>();
		
		Iterator iterator=fieldsMap.entrySet().iterator();
		while(iterator.hasNext()){
			String ssString=iterator.next().toString();
			alField.add(ssString.substring(0,ssString.indexOf("=")));
		}
		
		StringBuffer JsonFinalString = new StringBuffer();
		rsRecordset.top();
		while(rsRecordset.next()){
			
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < alField.size(); i++) {
				String value="";
				if(rsRecordset.getValue(alField.get(i))!=null){
					value=String.valueOf(rsRecordset.getValue(alField.get(i))) ;
				}
				if(sb.length()==0){
					sb.append("{").append("\"").append(alField.get(i)).append("\":").append("\"").append(value).append("\"");
				}else{
					sb.append(",").append("\"").append(alField.get(i)).append("\":").append("\"").append(value).append("\"");
				}
			}
			if(sb.length()!=0){
				sb.append("}");
			}
			if (JsonFinalString.length()==0) {
				JsonFinalString.append("[").append(sb.toString());
			}else{
				JsonFinalString.append(",").append("").append(sb.toString());
			}
		}
		
		
		if(rsRecordset.getRecordCount()==0){
			JsonFinalString=new StringBuffer();
		}else{
			if (JsonFinalString.length()!=0) {
				JsonFinalString.append("]");
			}
		}
		Recordset pubshRecordset=new Recordset();
		pubshRecordset.append("json", Types.VARCHAR);
		pubshRecordset.addNew();
		if(rsRecordset.getRecordCount()==0){
			pubshRecordset.setValue("json", "{}");
		}else
		{
			pubshRecordset.setValue("json", JsonFinalString.toString());
		}
		
		publish(sqlFile, pubshRecordset);
		return rc;
		
	}

}
