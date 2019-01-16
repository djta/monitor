package transactions.project.fitness.weixin;

import transactions.project.weixin.common.WeixinUtil;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class WxuserTransaction extends GenericTransaction {
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = getDb();

		String userlogin = "";
		String wxuserid = "";
		String sid = getRequest().getParameter("sid");
		String code = getRequest().getParameter("code");
		Recordset r = new Recordset();
		r.append("userlogin", java.sql.Types.VARCHAR);
		r.append("wxuserid", java.sql.Types.VARCHAR);
		r.addNew();
		if( sid != null && !"".equals(sid) && null != code && !"".equals(code) ){
			String query_weixin = "select appid, appsecret from wx_service where tuid = " + sid + " and is_deleted = '0'";
			Recordset rsWx = db.get(query_weixin);
			String appid = "";
			String secret = "";
			if (rsWx.getRecordCount() > 0) {
				rsWx.first();
				appid = rsWx.getString("appid");
				secret = rsWx.getString("appsecret");
				try {
					wxuserid = WeixinUtil.getWeixinUserIdForService(appid, secret, code);
					
					if( null != wxuserid && !"".equals(wxuserid) ){
						String queryStaff = "select userlogin from hr_staff_weixin where weixin_userid = '" + wxuserid + "' order by tuid desc limit 1";
						Recordset rsStaff = db.get(queryStaff);
						if (rsStaff.getRecordCount() > 0) {
							rsStaff.first();
							userlogin = rsStaff.getString("userlogin");
						}
					}
				} catch (Throwable e) {
				}
			}
		}
		r.setValue("userlogin", userlogin);
		r.setValue("wxuserid", wxuserid);
		publish("_rswxuser", r);
		return rc;
	}
}
