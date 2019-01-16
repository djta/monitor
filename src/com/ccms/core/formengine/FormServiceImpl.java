package com.ccms.core.formengine;

import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccms.caches.CacheConst;
import com.ccms.caches.CacheFactory;
import com.ccms.caches.CacheService;
import com.ccms.context.InitializerServlet;
import com.ccms.core.foctory.DocumentBean;
import com.ccms.core.foctory.DocumentFactory;
import com.ccms.core.foctory.FormBean;
import com.ccms.core.foctory.FormFactory;
import com.ccms.jdbc.JDBC;

import dinamica.Db;

public final class FormServiceImpl implements CacheService {
	
	private final Log log = LogFactory.getLog(getClass());

	public void destroy() {
		CacheFactory factory = CacheFactory.getInstance();
		factory.removeAll();
	}

	@Override
	public DocumentBean loadDocument(int documentId) {
		CacheFactory factory = CacheFactory.getInstance();
		DocumentBean docBean = null;
		Connection conn = null;
		try {
			conn = JDBC.getConnection();
			Db db = new Db(conn);
			FormTemplateEngine tmp = new FormTemplateEngine(InitializerServlet.getContext());
			DocumentFactory documentFactory = new DocumentFactory();
			documentFactory.setDb(db);
			docBean = documentFactory.loadDocument(documentId, tmp);
			DocumentBean docBean_old = factory.getValue(CacheConst.DOCUMENT_CACHE_PREFIX + documentId);
			if(docBean_old != null){
				factory.replace(CacheConst.DOCUMENT_CACHE_PREFIX + documentId, docBean_old, docBean);
			}else{
				factory.addCache(CacheConst.DOCUMENT_CACHE_PREFIX + documentId, docBean);
			}
			
			Integer formId = docBean.getForm_id();
			if(formId != null){
				FormFactory formFactory = new FormFactoryEngine();
				formFactory.setDb(db);
				FormBean bean = formFactory.loadForm(formId, tmp);
				FormBean bean_old = factory.getValue(CacheConst.FORM_CACHE_PREFIX + formId);
				if(bean_old != null){
					factory.replace(CacheConst.FORM_CACHE_PREFIX + formId, bean_old, bean);
				}else{
					factory.addCache(CacheConst.FORM_CACHE_PREFIX + formId, bean);
				}
			}
		} catch (Throwable e) {
		    e.printStackTrace();
			log.error("加载文档失败" + e);
		}finally{
			if(conn != null){
				try{
					conn.close();
				}catch(Throwable e){
					log.error("关闭连接失败" + e);
				}
			}
		}
		return docBean;
	}

	@Override
	public FormBean loadForm(int formId) {
		CacheFactory factory = CacheFactory.getInstance();
		FormBean bean = null;
		Connection conn = null;
		try {
			conn = JDBC.getConnection();
			Db db = new Db(conn);
			FormTemplateEngine tmp = new FormTemplateEngine(InitializerServlet.getContext());
			FormFactory formFactory = new FormFactoryEngine();
			formFactory.setDb(db);
			bean = formFactory.loadForm(formId, tmp);
			FormBean bean_old = factory.getValue(CacheConst.FORM_CACHE_PREFIX + formId);
			if(bean_old != null){
				factory.replace(CacheConst.FORM_CACHE_PREFIX + formId, bean_old, bean);
			}else{
				factory.addCache(CacheConst.FORM_CACHE_PREFIX + formId, bean);
			}
		} catch (Throwable e) {
			log.error("加载表单失败" + e);
		}finally{
			if(conn != null){
				try{
					conn.close();
				}catch(Throwable e){
					log.error("关闭连接失败" + e);
				}
			}
		}
		return bean;
	}
}
