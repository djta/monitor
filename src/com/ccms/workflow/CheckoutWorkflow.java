package com.ccms.workflow;

import java.sql.Types;

import dinamica.GenericTableManager;
import dinamica.Recordset;

public class CheckoutWorkflow extends GenericTableManager
{

	public int service(Recordset inputParams) throws Throwable
	{

		int rc = super.service(inputParams);
		Recordset rsResult = new Recordset();
		rsResult.append("is_success", Types.VARCHAR);
		rsResult.append("checkout_user", Types.VARCHAR);
		rsResult.append("checkout_alias", Types.VARCHAR);
		rsResult.append("checkout_name", Types.VARCHAR);
		rsResult.addNew();
		String update = getSQL(getResource("update_owner.sql"),inputParams);
		int result = getDb().exec(update);
		if(result == 0){//如果更新所属人失败，则说明已经被别人签出处理
			rsResult.setValue("is_success", "0");
			String query = getSQL(getResource("query-current_owner.sql"),inputParams);
			Recordset rsOwner = getDb().get(query);
			if(rsOwner.next()){
				rsResult.setValue("checkout_user", rsOwner.getString("owner"));
				rsResult.setValue("checkout_alias", rsOwner.getString("alias"));
				rsResult.setValue("checkout_name", rsOwner.getString("name"));
			}
		}else{
			rsResult.setValue("is_success", "1");
		}
		
		publish("result_recordset", rsResult);
		return rc;
		
	}

}
