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

public class FormViewLoadGen extends GenericTransaction {

	@Override
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);

		String curdParamsId = getConfig().getConfigValue("crud-params-id","crud_params");
		String suffix = getConfig().getConfigValue("suffix-col","uniquegen");
		String suffixValue = inputParams.getString(suffix);
		
		Recordset rsCrud = (Recordset)getSession().getAttribute(curdParamsId+"_"+suffixValue);
		
		if(rsCrud != null && rsCrud.getRecordCount() > 0){
			publish(curdParamsId+"_"+suffixValue, rsCrud);
		}
		
		CacheProvider cp = new CacheProviderImpl();
		DocumentBean document = cp.getDocumentBeanById(Integer.parseInt(suffixValue));
		Integer form_id = document.getForm_id();
		FormBean form = cp.getFormBeanById(form_id);
		
		DinamicaUser user = (DinamicaUser)getSession().getAttribute(Constants.DINAMICA_SECURITY_LOGIN);
		Recordset rsPriviledge = new Recordset();
		rsPriviledge.append("is_can_show", Types.VARCHAR);
		rsPriviledge.append("bpk_field_colname", Types.VARCHAR);
		rsPriviledge.append("form_id", Types.INTEGER);
		rsPriviledge.addNew();
		rsPriviledge.setValue("bpk_field_colname", "${fld:"+form.getBpk_field()+"}");
		rsPriviledge.setValue("form_id", form_id);
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_DETAIL)){
			rsPriviledge.setValue("is_can_show", "inline");
		}else{
			rsPriviledge.setValue("is_can_show", "none");
		}
		publish("query_priviledge.sql", rsPriviledge);
		return rc;
	}
}
