package com.ccms.core.foctory;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

/**
 * @author zhangchuan
 * 文档缓存管理
 *
 */
public class DocumentFactory {

	private Db db = null;
	/**
	 * 初始化Factory时需要给db赋值，调用结束时要记得关闭连接
	 * @return
	 */
	public Db getDb() {
		return db;
	}

	public void setDb(Db db) {
		this.db = db;
	}
	
	public DocumentBean loadDocument(Integer documentId, TemplateBean tmp) throws Throwable{
		DocumentBean document = getDocumentById(documentId, tmp);
		return document;
	}
	
	public DocumentBean getDocumentById(Integer documentId, TemplateBean tmp) throws Throwable{
		String queryDocument = tmp.getQueryDocument();
		queryDocument = StringUtil.replace(queryDocument, "${document_id}", documentId.toString());
		Recordset rsDocument = db.get(queryDocument);
		rsDocument.first();
		DocumentBean document = new DocumentBean(rsDocument);
		
		String queryDocumentParams = tmp.getQueryDocumentParams();
		queryDocumentParams = StringUtil.replace(queryDocumentParams, "${document_id}", documentId.toString());
		Recordset rsParmas = db.get(queryDocumentParams);
		document.setRsDocumentParams(rsParmas);
		
		return document;
	}
}
