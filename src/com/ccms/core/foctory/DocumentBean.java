package com.ccms.core.foctory;

import java.io.Serializable;

import dinamica.Recordset;

/**
 * @author zhangchuan 定义文档
 * 
 */
public class DocumentBean implements Serializable {

	private static final long serialVersionUID = 3852031291935408477L;

	private Integer form_id = null;
	private Integer subject_id = null;
	private Integer table_id = null;
	private Integer document_id = null;
	private String document_name = null;

	private Recordset rsDocument = null;

	private Integer report_id = null;
	private String action_type = null;
	private String url = null;

	private String doc_width = null;
	private String doc_hight = null;
	private String nav_url = null;
	private String nav_url_width = null;
	private String nav_url_right = null;
	private String nav_right_width = null;
	private String nav_url_top = null;
	private String nav_url_hight = null;
	private String nav_url_bottom = null;
	private String nav_bottom_hight = null;

	/**
	 * 文档参数传递
	 */
	private Recordset rsDocumentParams = null;

	public DocumentBean(Recordset rsDocument) throws Throwable {
		this.form_id = rsDocument.getString("form_id") == null ? null : rsDocument.getInteger("form_id");
		this.subject_id = rsDocument.getInteger("subject_id");
		this.table_id = rsDocument.getString("table_id") == null ? null : rsDocument.getInteger("table_id");
		this.document_id = rsDocument.getInteger("document_id");
		this.document_name = rsDocument.getString("document_name");
		this.report_id = rsDocument.getString("report_id") == null ? null : rsDocument.getInteger("report_id");
		this.action_type = rsDocument.getString("action_type");
		this.url = rsDocument.getString("url");
		this.doc_width = rsDocument.getString("doc_width");
		this.doc_hight = rsDocument.getString("doc_hight");
		this.nav_url = rsDocument.getString("nav_url");
		this.nav_url_width = rsDocument.getString("nav_url_width");
		this.nav_url_right = rsDocument.getString("nav_url_right");
		this.nav_right_width = rsDocument.getString("nav_right_width");
		this.nav_url_top = rsDocument.getString("nav_url_top");
		this.nav_url_hight = rsDocument.getString("nav_url_hight");
		this.nav_url_bottom = rsDocument.getString("nav_url_bottom");
		this.nav_bottom_hight = rsDocument.getString("nav_bottom_hight");

		this.rsDocument = rsDocument;
	}

	public Integer getForm_id() {
		return form_id;
	}

	public void setForm_id(Integer form_id) {
		this.form_id = form_id;
	}

	public Integer getDocument_id() {
		return document_id;
	}

	public void setDocument_id(Integer document_id) {
		this.document_id = document_id;
	}

	public String getDocument_name() {
		return document_name;
	}

	public void setDocument_name(String document_name) {
		this.document_name = document_name;
	}

	public Recordset getRsDocument() {
		return rsDocument;
	}

	public void setRsDocument(Recordset rsDocument) {
		this.rsDocument = rsDocument;
	}

	public Integer getSubject_id() {
		return subject_id;
	}

	public void setSubject_id(Integer subjectId) {
		subject_id = subjectId;
	}

	public Integer getTable_id() {
		return table_id;
	}

	public void setTable_id(Integer tableId) {
		table_id = tableId;
	}

	public Recordset getRsDocumentParams() {
		return rsDocumentParams;
	}

	public void setRsDocumentParams(Recordset rsDocumentParams) {
		this.rsDocumentParams = rsDocumentParams;
	}

	public Integer getReport_id() {
		return report_id;
	}

	public void setReport_id(Integer reportId) {
		report_id = reportId;
	}

	public String getAction_type() {
		return action_type;
	}

	public void setAction_type(String actionType) {
		action_type = actionType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDoc_width() {
		return doc_width;
	}

	public void setDoc_width(String docWidth) {
		doc_width = docWidth;
	}

	public String getDoc_hight() {
		return doc_hight;
	}

	public void setDoc_hight(String docHight) {
		doc_hight = docHight;
	}

	public String getNav_url() {
		return nav_url;
	}

	public void setNav_url(String navUrl) {
		nav_url = navUrl;
	}

	public String getNav_url_width() {
		return nav_url_width;
	}

	public void setNav_url_width(String navUrlWidth) {
		nav_url_width = navUrlWidth;
	}

	public String getNav_url_right() {
		return nav_url_right;
	}

	public void setNav_url_right(String navUrlRight) {
		nav_url_right = navUrlRight;
	}

	public String getNav_right_width() {
		return nav_right_width;
	}

	public void setNav_right_width(String navRightWidth) {
		nav_right_width = navRightWidth;
	}

	public String getNav_url_top() {
		return nav_url_top;
	}

	public void setNav_url_top(String navUrlTop) {
		nav_url_top = navUrlTop;
	}

	public String getNav_url_hight() {
		return nav_url_hight;
	}

	public void setNav_url_hight(String navUrlHight) {
		nav_url_hight = navUrlHight;
	}

	public String getNav_url_bottom() {
		return nav_url_bottom;
	}

	public void setNav_url_bottom(String navUrlBottom) {
		nav_url_bottom = navUrlBottom;
	}

	public String getNav_bottom_hight() {
		return nav_bottom_hight;
	}

	public void setNav_bottom_hight(String navBottomHight) {
		nav_bottom_hight = navBottomHight;
	}
}
