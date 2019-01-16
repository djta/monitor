package transactions.project.weixin.service;

import dinamica.GenericTransaction;
import dinamica.Recordset;

public class SetWeixinLocationTranstion extends GenericTransaction{
	public int service(Recordset inputParams) throws Throwable{
		int rc = super.service(inputParams);
		String latitude=inputParams.getString("latitude");
		String longitude=inputParams.getString("longitude");
		getSession().setAttribute("latitude", latitude);
		System.out.println("set sesson latitude--->"+latitude);
		getSession().setAttribute("longitude", longitude);
		System.out.println("set sesson longitude--->"+longitude);
		return rc;
	}
}
