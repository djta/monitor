package com.ccms.dinamica.domain.admin;

import javax.servlet.http.HttpSession;

import dinamica.GenericTableManager;
import dinamica.Recordset;

public class ChangeLocale extends GenericTableManager {
	
	public int service(Recordset inputParams) throws Throwable 
	{
		
		int rc = super.service(inputParams);
		
		String locale = inputParams.getString("locale");
		
		HttpSession s =  getRequest().getSession();
		if(s != null && locale != null){
			java.util.Locale l = new java.util.Locale(locale);
			s.setAttribute("dinamica.user.locale", l);
		}
		
		return rc;
	}
}
