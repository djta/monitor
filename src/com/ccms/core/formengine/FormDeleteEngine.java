package com.ccms.core.formengine;


import com.ccms.Constants;
import com.ccms.caches.CacheProvider;
import com.ccms.core.foctory.FormBean;
import com.ccms.core.foctory.FormFactory;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.GenericTransaction;
import dinamica.IServiceWrapper;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.security.DinamicaUser;

public class FormDeleteEngine extends GenericTableManager implements IServiceWrapper{

	FormBean form = null;//查询校验类、替换类和后处理类
	
	@Override
	public int service(Recordset inputParams) throws Throwable {

		Integer form_id = inputParams.getInteger("form_id");
		CacheProvider cp = new FormProviderImpl();
		form = cp.getFormBeanById(form_id);
		
		//首先验证权限
		DinamicaUser user = (DinamicaUser)getSession().getAttribute(Constants.DINAMICA_SECURITY_LOGIN);
		if(!FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_DELETE)){
			throw new Throwable(getSQL(Constants.NO_PERMISION, null));
		}
		
		int rc = super.service(inputParams);
		
		Db db = getDb();
		
		//替换类处理
		boolean is_user_replace_class = false;
		
        String validatorClassName = form.getDelete_classname_validator();
        if(validatorClassName!=null && validatorClassName.length() > 0){
    	    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    		GenericTransaction t = (GenericTransaction) loader.loadClass(validatorClassName).newInstance();
			t.init(this.getContext(), this.getRequest(), this.getResponse());
			t.setConfig(this.getConfig());
			t.setConnection(this.getConnection());
            t.service(inputParams);
	    }
        
        String replaceClassName = form.getDelete_classname_replace();
	    if(replaceClassName!=null && replaceClassName.length() > 0){
    	    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    		GenericTransaction t = (GenericTransaction) loader.loadClass(replaceClassName).newInstance();
			t.init(this.getContext(), this.getRequest(), this.getResponse());
			t.setConfig(this.getConfig());
			t.setConnection(this.getConnection());
            t.service(inputParams);
            is_user_replace_class = true;
	    }
	    
		if(is_user_replace_class == false){
			
			//控制事务
			db.beginTrans();
			
			String delete = form.getDelete_sql();
			delete = getSQL(delete, inputParams);
			db.exec(delete);
			
			//记录审计数据
			String deleteAudit = form.getUpdate_audit_sql();
			deleteAudit = StringUtil.replace(deleteAudit, "${pk_value}", inputParams.getString("__pk_value__"));
			db.exec(getSQL(deleteAudit, null));
			
			//事务提交
			db.commit();
		}
		return rc;
	}

	public void afterService(Recordset inputParams) throws Throwable
	{
		
        String strClassName = form.getDelete_classname();
	    if(strClassName==null || strClassName.equalsIgnoreCase(""))  return;
	        
	    ClassLoader loader = Thread.currentThread().getContextClassLoader();
		GenericTransaction t = (GenericTransaction) loader.loadClass(strClassName).newInstance();
		t.init(this.getContext(), this.getRequest(), this.getResponse());
		t.setConfig(this.getConfig());
		t.setConnection(this.getConnection());
		
        t.service(inputParams);
	}
	
	public void beforeService(Recordset inputParams) throws Throwable
	{
	}

}
