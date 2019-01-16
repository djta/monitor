package com.ccms.core.formengine;

import com.ccms.caches.CacheProvider;
import com.ccms.core.foctory.FormBean;

import dinamica.GenericTableManager;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class FormLeadsDistribution extends GenericTableManager {

	FormBean form = null;//查询校验类、替换类和后处理类
	@Override
	public int service(Recordset inputs) throws Throwable {

		int rc = super.service(inputs);

		Integer form_id = inputs.getInteger("form_id");
		//每次都从缓存中重新复制一份
		CacheProvider cp = new FormProviderImpl();
		form = cp.getFormBeanById(form_id);

		//将新增之后的主键信息，table_id
		inputs.setValue("bpk_field_value", inputs.getString("leads_code"));
		inputs.setValue("table_id", form.getTable_id());
		
		//处理工单流界面
		Integer wfm_id = null;
		if(inputs.containsField("__wfm_id__") && inputs.getString("__wfm_id__")!=null){
			wfm_id = inputs.getInteger("__wfm_id__");
		}
		if(wfm_id != null){	/*是个工单流界面*/
			String strClassName = "com.ccms.workflow.CreateWorkflow";
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			GenericTransaction t = (GenericTransaction) loader.loadClass(strClassName).newInstance();
			t.init(this.getContext(), this.getRequest(), this.getResponse());
			t.setConfig(this.getConfig());
			t.setConnection(this.getConnection());
			t.service(inputs);
		}
		getRequest().setAttribute("inputParams", inputs);
		publish("_request", inputs);
		return rc;
	}

}
