package com.ccms.core.formengine;

import java.sql.Types;

import com.ccms.Constants;
import com.ccms.caches.CacheProvider;
import com.ccms.core.foctory.FormBean;
import com.ccms.core.foctory.FormFactory;

import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.RecordsetField;
import dinamica.ValidatorUtil;
import dinamica.security.DinamicaUser;

public class FormEditEngine extends GenericTableManager {

	@Override
	public int service(Recordset inputParams) throws Throwable {

		int rc = super.service(inputParams);
		
		String printRecordset = getConfig().getConfigValue("print-recordset","query.sql");

		Integer form_id = inputParams.getInteger("form_id");
		CacheProvider cp = new FormProviderImpl();
		FormBean form = cp.getFormBeanById(form_id);
		
		String query = getSQL(form.getEdit_sql(), inputParams);
		
		Recordset rsQuery = getDb().get(query);
		if(rsQuery.getRecordCount() == 0){
			throw new Throwable("要访问的数据不存在，请刷新界面后重试!");
		}
		rsQuery.first();
		
		//判断当前人是否拥有操作权限
		String ower_field = form.getOwner_field();
		Recordset rsPriviledge = new Recordset();
		rsPriviledge.append("is_can_delete", Types.VARCHAR);
		rsPriviledge.append("is_can_update", Types.VARCHAR);
		rsPriviledge.addNew();
		DinamicaUser user = (DinamicaUser)getSession().getAttribute(Constants.DINAMICA_SECURITY_LOGIN);
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_DELETE)){
			if(ower_field != null && Constants.OPERATION_TYPE_OWNER.equals(form.getOperation_type()) && rsQuery.containsField(ower_field)){
				String createdby = rsQuery.getString(ower_field);
				if(createdby != null && createdby.equals(user.getName())){
					rsPriviledge.setValue("is_can_delete", "inline");
				}else{
					rsPriviledge.setValue("is_can_delete", "none");
				}
			}else{
				rsPriviledge.setValue("is_can_delete", "inline");
			}
		}else{
			rsPriviledge.setValue("is_can_delete", "none");
		}
		if(FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_ADD) || FormFactory.hasPriviledge(form, user, Constants.PRIVILEDGE_UPDATE)){
			if(ower_field != null && Constants.OPERATION_TYPE_OWNER.equals(form.getOperation_type()) && rsQuery.containsField(ower_field)){
				String createdby = rsQuery.getString(ower_field);
				if(createdby != null && createdby.equals(user.getName())){
					rsPriviledge.setValue("is_can_update", "inline");
				}else{
					rsPriviledge.setValue("is_can_update", "none");
				}
			}else{
				rsPriviledge.setValue("is_can_update", "inline");
			}
		}else{
			rsPriviledge.setValue("is_can_update", "none");
		}
		publish("query_priviledge.sql", rsPriviledge);
		
		//判断是否是版本还原，如果是则从历史痕迹表中查询数据，并在当前结果集中替换值
		String snapshot = inputParams.getString("snapshot");
		if(snapshot != null && snapshot.length() > 0){
			Recordset rsDataLog = getDb().get(getSQL(form.getEdit_query_data_log(), inputParams));
			rsQuery.first();
			String dateFormat = "yyyy-MM-dd";
			String datetimeFormat = "yyyy-MM-dd HH:mm:ss";
			while(rsDataLog.next()){
				String field_code = rsDataLog.getString("field_code");
				if(rsQuery.containsField(field_code)){
					RecordsetField field = rsQuery.getField(field_code);
					String value = rsDataLog.getString("value");
					if(value != null && value.length() > 0){
						switch(field.getType()){
							case Types.INTEGER:
								rsQuery.setValue(field_code, ValidatorUtil.testInteger(value));
							case Types.DOUBLE:
								rsQuery.setValue(field_code, ValidatorUtil.testDouble(value));
							case Types.DATE:
								rsQuery.setValue(field_code, ValidatorUtil.testDate(value, dateFormat));
							case Types.TIMESTAMP:
								rsQuery.setValue(field_code, ValidatorUtil.testDate(value, datetimeFormat));
							default:
								rsQuery.setValue(field_code, value);
						}
					}else{
						rsQuery.setValue(field_code, null);
					}
				}
			}
		}
		
		publish(printRecordset, rsQuery);

		return rc;
	}
}