package com.ccms.term;

public class TermMatrixItem {

	private String item_id;
	private String list_id;
	private String matrix_item_id;
	private String matrix_score;
	private String matrix_text = "";
	private String remark;
	
	public String getItem_id() {
		return item_id;
	}
	public void setItem_id(String itemId) {
		item_id = itemId;
	}
	public String getList_id() {
		return list_id;
	}
	public void setList_id(String listId) {
		list_id = listId;
	}
	public String getMatrix_item_id() {
		return matrix_item_id;
	}
	public void setMatrix_item_id(String matrixItemId) {
		matrix_item_id = matrixItemId;
	}
	public String getMatrix_score() {
		return matrix_score;
	}
	public void setMatrix_score(String matrixScore) {
		matrix_score = matrixScore;
	}
	public String getMatrix_text() {
		return matrix_text;
	}
	public void setMatrix_text(String matrixText) {
		matrix_text = matrixText;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
