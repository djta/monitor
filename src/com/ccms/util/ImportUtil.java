package com.ccms.util;

import dinamica.Db;
import dinamica.Recordset;

public class ImportUtil {
	
	public static String getSeq(String sql, String column, Db db) throws Throwable{
		Recordset rs = db.get(sql);
		rs.next();
		String seq = rs.getString(column);
		return seq;
	}

	public static String findInRecordset(Recordset rs, String nameSpace, String domainText) throws Throwable{
		if(domainText == null || "".equals(domainText)) return "";
		String value = "";
		rs.top();
		while(rs.next()){
			String ns = rs.getString("namespace");
			String dt = rs.getString("domain_text");
			String v = rs.getString("domain_value");
			if(nameSpace.equalsIgnoreCase(ns) && domainText.equalsIgnoreCase(dt)){
				value = v;
				break;
			}
		}
		return value;
	}
	
	public static String findInRecordsetByParent(Recordset rs, String parentValue, String nameSpace, String domainText) throws Throwable{
		if(domainText == null || "".equals(domainText) || "".equals(parentValue)) return "";
		String value = "";
		rs.top();
		while(rs.next()){
			String ns = rs.getString("namespace");
			String dt = rs.getString("domain_text");
			String pv = rs.getString("parent_domain_value");
			String v = rs.getString("domain_value");
			if(nameSpace.equalsIgnoreCase(ns) && domainText.equalsIgnoreCase(dt) && parentValue.equalsIgnoreCase(pv)){
				value = v;
				break;
			}
		}
		return value;
	}
}
