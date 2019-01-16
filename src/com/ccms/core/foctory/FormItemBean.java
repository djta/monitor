package com.ccms.core.foctory;

import java.io.Serializable;

import dinamica.Recordset;

/**
 * @author zhangchuan
 * 定义表单分栏信息
 *
 */
public class FormItemBean implements Serializable{

	private static final long serialVersionUID = 3852031291935408478L;
	
	private Integer item_id = null;
	private String item_html = null;

//	private Integer page_size = null;
	
	public Recordset getFormItemRecordset(String locale) throws Throwable{
		Recordset rs = new Recordset();
		
		return rs;
	}
	public FormItemBean(Integer item_id,String item_html) throws Throwable{
		this.item_id = item_id;
		this.item_html = item_html;
	}
	public Integer getItem_id() {
		return item_id;
	}
	public void setItem_id(Integer item_id) {
		this.item_id = item_id;
	}
	public String getItem_html() {
		return item_html;
	}
	public void setItem_html(String item_html) {
		this.item_html = item_html;
	}

	
}
