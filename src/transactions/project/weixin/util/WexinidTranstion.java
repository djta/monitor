package transactions.project.weixin.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import transactions.project.weixin.common.WeixinUtil;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class WexinidTranstion extends GenericTransaction {
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = getDb();
		Recordset r = new Recordset();
		r.append("weixin_user", java.sql.Types.VARCHAR);
		r.append("appid", java.sql.Types.VARCHAR);
		r.append("sid", java.sql.Types.VARCHAR);
		String weixin_user = "";
		String appid = "";
		String sid = this.getRequestValue(getRequest(), "sid");
		String code = this.getRequestValue(getRequest(), "code");
		if( StringUtils.isNotBlank(sid) && StringUtils.isNotBlank(code) ){
			String queryWeixin = "select appid, appsecret from public.wx_service where tuid=" + sid;
			Recordset rsWeixin = db.get(queryWeixin);
			String secret = "";
			if (rsWeixin.getRecordCount() > 0) {
				rsWeixin.top();
				if (rsWeixin.next()) {
					appid = rsWeixin.getString("appid");
					secret = rsWeixin.getString("appsecret");
				}
				try {
					weixin_user = WeixinUtil.getWeixinUserIdForService(appid, secret, code);
				} catch (Throwable e) {
				}
			}
		}
		r.addNew();
		r.setValue("weixin_user", weixin_user);
		r.setValue("appid", appid);
		r.setValue("sid", sid);
		publish("weixin_user_r", r);
		return rc;
	}
	
	/***
	 * get value from request
	 * @param request
	 * @param paramname
	 * @return
	 */
	private String getRequestValue(HttpServletRequest request, String paramname){
		String value = "";
		if( null != request && null != request.getParameter(paramname) ){
			value = request.getParameter(paramname);
		}
		return value;
	}
}
