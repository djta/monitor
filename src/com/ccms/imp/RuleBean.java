package com.ccms.imp;

import java.io.Serializable;

import dinamica.Recordset;

public class RuleBean implements Serializable{

	private static final long serialVersionUID = -759698166884826689L;

	private Integer rule_id = null;
	/*0-除重,1-文件校验,2-入库校验*/
	private String filter_type = null;
	
	public static RuleBean transformRule(Recordset rs) throws Throwable{
		RuleBean bean = new RuleBean();
		bean.setRule_id(rs.getInteger("rule_id"));
		bean.setFilter_type(rs.getString("filter_type"));
		return bean;
	}
	
	
	public Integer getRule_id() {
		return rule_id;
	}
	public void setRule_id(Integer ruleId) {
		rule_id = ruleId;
	}
	public String getFilter_type() {
		return filter_type;
	}
	public void setFilter_type(String filterType) {
		filter_type = filterType;
	}
}
