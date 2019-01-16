package com.ccms.util;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import dinamica.AbstractValidator;
import dinamica.Recordset;

public class ChineseFilterDecode extends AbstractValidator {

	@Override
	public boolean isValid(HttpServletRequest req, Recordset inputParams,
			HashMap<String, String> attribs) throws Throwable {
		boolean bParam = attribs.containsKey("parameter");
		if (!bParam)
			throw new Throwable("[" + this.getClass().getName() + "] Missing attribute [parameter] in validator.xml");
		String paramName = (String)attribs.get("parameter");
		if (!inputParams.isNull(paramName))
		{
			String value = inputParams.getString(paramName);
			value = java.net.URLDecoder.decode(value,"UTF-8");
			inputParams.setValue(paramName, value);
		}
		return true;
	}

}
