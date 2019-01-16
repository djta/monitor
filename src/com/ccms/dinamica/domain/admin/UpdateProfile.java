package com.ccms.dinamica.domain.admin;

import com.ccms.dinamica.Login;

import dinamica.GenericTableManager;
import dinamica.Recordset;

public class UpdateProfile extends GenericTableManager
{
	public int service(Recordset inputParams) throws Throwable
	{
		super.service(inputParams);

		Login obj = (Login)getObject("com.ccms.dinamica.Login");
		obj.getUserPrefs(getDb(), inputParams);
			
		return 0;
	}
}
