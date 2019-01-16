package com.ccms.term;

import java.util.HashSet;
import java.util.Set;

public class TermItem {

	private String type_id;
	private String item_id;
	private String item_score;
	private String remark;
	private Set<TermList> lists = new HashSet<TermList>();
	private Set<TermMatrixItem> matrixItems = new HashSet<TermMatrixItem>();
	
	public String getType_id() {
		return type_id;
	}
	public void setType_id(String typeId) {
		type_id = typeId;
	}
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String itemId) {
		item_id = itemId;
	}
	public String getItem_score() {
		return item_score;
	}
	public void setItem_score(String itemScore) {
		item_score = itemScore;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Set<TermList> getLists() {
		return lists;
	}
	public void setLists(Set<TermList> lists) {
		this.lists = lists;
	}
	public Set<TermMatrixItem> getMatrixItems() {
		return matrixItems;
	}
	public void setMatrixItems(Set<TermMatrixItem> matrixItems) {
		this.matrixItems = matrixItems;
	}
}
