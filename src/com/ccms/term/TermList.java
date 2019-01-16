package com.ccms.term;

public class TermList {

	private String item_id = null;
	private String list_id = null;
	private String list_score = null;
	private String list_text = null;
	private String remark = null;
	private String list_dropdown_value = null;
	
	public String getList_dropdown_value() {
		return this.list_dropdown_value;
	}
	public void setList_dropdown_value(String listDropdownValue) {
		this.list_dropdown_value = (listDropdownValue!=null&&listDropdownValue.length()==0)?null:listDropdownValue;;
	}
	public String getItem_id() {
		return this.item_id;
	}
	public void setItem_id(String itemId) {
		this.item_id = itemId;
	}
	public String getList_id() {
		return this.list_id;
	}
	public void setList_id(String listId) {
		this.list_id = listId;
	}
	public String getList_score() {
		return this.list_score;
	}
	public void setList_score(String listScore) {
		this.list_score = listScore;
	}
	public String getList_text() {
		return this.list_text;
	}
	public void setList_text(String listText) {
		this.list_text = (listText!=null&&listText.length()==0)?null:listText;
	}
	public String getRemark() {
		return this.remark;
	}
	public void setRemark(String remark) {
		this.remark = (remark!=null&&remark.length()==0)?null:remark;
	}
}
