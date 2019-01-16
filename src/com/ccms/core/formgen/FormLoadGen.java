package com.ccms.core.formgen;

import java.sql.Types;

import com.ccms.Constants;
import com.ccms.caches.CacheProvider;
import com.ccms.caches.impl.CacheProviderImpl;
import com.ccms.core.foctory.DocumentBean;
import com.ccms.core.foctory.FormBean;
import com.ccms.core.foctory.FormFactory;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.security.DinamicaUser;

public class FormLoadGen extends GenericTransaction {

	@Override
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);

		String curdParamsId = getConfig().getConfigValue("crud-params-id","crud_params");
		String suffix = getConfig().getConfigValue("suffix-col","document_id");
		String outputParams = getConfig().getConfigValue("output-params-id","formgen.filter.params");
		
		String suffixValue = inputParams.getString(suffix);
		Recordset params = (Recordset)getSession().getAttribute(curdParamsId+"_"+suffixValue);

		//操作权限判断
		CacheProvider cp = new CacheProviderImpl();
		DocumentBean document = cp.getDocumentBeanById(Integer.parseInt(suffixValue));
		Integer form_id = document.getForm_id();
		FormBean form = cp.getFormBeanById(form_id);
		DinamicaUser user = (DinamicaUser)getSession().getAttribute(Constants.DINAMICA_SECURITY_LOGIN);
		Recordset rsPriviledge = new Recordset();
		rsPriviledge.append("is_can_delete", Types.VARCHAR);
		rsPriviledge.append("is_can_update", Types.VARCHAR);
		rsPriviledge.addNew();
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_DELETE)){
			rsPriviledge.setValue("is_can_delete", "inline");
		}else{
			rsPriviledge.setValue("is_can_delete", "none");
		}
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_ADD) || FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_UPDATE)){
			rsPriviledge.setValue("is_can_update", "inline");
		}else{
			rsPriviledge.setValue("is_can_update", "none");
		}
		publish("query_priviledge.sql", rsPriviledge);
		if(params!=null){
			publish(outputParams, params);
		}else{
			publish(outputParams, inputParams);
		}
		return rc;
	}
}
