package com.ccms.core.formgen;

import java.sql.Types;

import com.ccms.Constants;
import com.ccms.caches.CacheProvider;
import com.ccms.caches.impl.CacheProviderImpl;
import com.ccms.core.foctory.FormBean;
import com.ccms.core.foctory.FormFactory;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.security.DinamicaUser;

public class FormPageControlsGen extends GenericTransaction {

	@Override
	public int service(Recordset inputParams) throws Throwable {

		//操作权限判断
		Integer form_id = inputParams.getInteger("form_id");
		CacheProvider cp = new CacheProviderImpl();
		FormBean form = cp.getFormBeanById(form_id);
		DinamicaUser user = (DinamicaUser)getSession().getAttribute(Constants.DINAMICA_SECURITY_LOGIN);
		Recordset rsPriviledge = new Recordset();
		rsPriviledge.append("is_can_query", Types.VARCHAR);
		rsPriviledge.append("is_can_create", Types.VARCHAR);
		rsPriviledge.append("is_can_export", Types.VARCHAR);
		rsPriviledge.append("is_can_print", Types.VARCHAR);
		rsPriviledge.addNew();
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_SEARCH)){
			rsPriviledge.setValue("is_can_query", "inline");
		}else{
			rsPriviledge.setValue("is_can_query", "none");
		}
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_ADD)){
			rsPriviledge.setValue("is_can_create", "inline");
		}else{
			rsPriviledge.setValue("is_can_create", "none");
		}
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_EXCEL)){
			rsPriviledge.setValue("is_can_export", "inline");
		}else{
			rsPriviledge.setValue("is_can_export", "none");
		}
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_PDF)){
			rsPriviledge.setValue("is_can_print", "inline");
		}else{
			rsPriviledge.setValue("is_can_print", "none");
		}
		publish("query_priviledge.sql", rsPriviledge);
		return 0;
	}
}
