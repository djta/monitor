package com.ccms.term;

import java.util.HashSet;
import java.util.Set;

public class TermType {

	private String type_id;
	private String type_score;
	private String remark;
	private Set<TermItem> items = new HashSet<TermItem>();
	
	public String getType_id() {
		return type_id;
	}
	public void setType_id(String typeId) {
		type_id = typeId;
	}
	public String getType_score() {
		return type_score;
	}
	public void setType_score(String typeScore) {
		type_score = typeScore;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Set<TermItem> getItems() {
		return items;
	}
	public void setItems(Set<TermItem> items) {
		this.items = items;
	};
}
