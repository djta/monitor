package com.ccms.validators;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import dinamica.*;

/**
 * 判断空值用固定值替换
 */
public class NullReplaceValidator extends AbstractValidator
{

    /* (non-Javadoc)
     * @see dinamica.AbstractValidator#isValid(javax.servlet.http.HttpServletRequest, dinamica.Recordset, java.util.HashMap)
     */
    public boolean isValid(HttpServletRequest req, Recordset inputParams,
            HashMap<String, String> attribs) throws Throwable
    {

		boolean bParam = attribs.containsKey("parameter");
		if (!bParam)
			throw new Throwable("[" + this.getClass().getName() + "] Missing attribute [parameter] in validator.xml");
		
		boolean replaceParam = attribs.containsKey("ifnull");
		if (!replaceParam)
			throw new Throwable("[" + this.getClass().getName() + "] Missing attribute [ifnull] in validator.xml");
		
		String paramName = (String)attribs.get("parameter");
		if (inputParams.isNull(paramName))
		{
			String replaceVal = (String)attribs.get("ifnull");
			inputParams.setValue(paramName, replaceVal);
		}
		
		return true;
		
    }

}
