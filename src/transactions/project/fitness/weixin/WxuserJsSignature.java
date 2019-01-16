package transactions.project.fitness.weixin;

import java.sql.Types;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONObject;

import transactions.project.weixin.common.WeixinUtil;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

/***
 * 获取微信id+签名
 * @author C
 * 2018-05-03
 */
public class WxuserJsSignature extends GenericTransaction {
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = getDb();

		String userlogin = "";
		String wxuserid = "";
		String appid = "";
		String accessAddress = "";
		String nonce_str = "";
		String timestamp = "";
		String signature = "";
		String sid = getRequest().getParameter("sid");
		String code = getRequest().getParameter("code");
		Recordset r = new Recordset();
		r.append("userlogin", java.sql.Types.VARCHAR);
		r.append("wxuserid", java.sql.Types.VARCHAR);
		if( sid != null && !"".equals(sid) && null != code && !"".equals(code) ){
			// 获取微信id
			String query_weixin = "select tuid, appid, appsecret, access_address from wx_service where tuid = " + sid + " and is_deleted = '0'";
			Recordset rsWx = db.get(query_weixin);
			String secret = "";
			if (rsWx.getRecordCount() > 0) {
				rsWx.first();
				appid = rsWx.getString("appid");
				secret = rsWx.getString("appsecret");
				accessAddress = !rsWx.isNull("access_address") ? rsWx.getString("access_address") : null;
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

			// 获取签名
			String realString = getRequest().getRequestURL().toString();
			String urlString = realString.substring(0, 7).concat(realString.substring(7).replace("//", "/"));
			StringBuffer sb = new StringBuffer();
			sb.append(urlString);
			Map<?, ?> map = getRequest().getParameterMap();
			Iterator<?> it = map.keySet().iterator();
			while (it.hasNext()) {
				String key = (String) it.next();
				String value = getRequest().getParameter(key);
				if (sb.toString().contains("?")) {
					sb.append("&" + key + "=" + value);
				} else {
					sb.append("?" + key + "=" + value);
				}
			}
			String jsapi_ticket = WeixinUtil.getJsTicket(db, sid);
			// 获得js初始化签名
			JSONObject jsonObject = WeixinUtil.signature(jsapi_ticket, sb.toString());
			nonce_str = jsonObject.getString("noncestr");
			timestamp = jsonObject.getString("timestamp");
			signature = jsonObject.getString("signature");
		}
		r.addNew();
		r.setValue("userlogin", userlogin);
		r.setValue("wxuserid", wxuserid);
		publish("_rswxuser", r);
		
		Recordset rsSignature = new Recordset();
		rsSignature.append("noncestr", Types.VARCHAR);
		rsSignature.append("timestamp", Types.VARCHAR);
		rsSignature.append("signature", Types.VARCHAR);
		rsSignature.append("service_tuid", Types.VARCHAR);
		rsSignature.append("org_id", Types.VARCHAR);
		rsSignature.append("appid", Types.VARCHAR);
		rsSignature.append("access_address", Types.VARCHAR);

		String org_id = "";
		if( null != getSession().getAttribute("dinamica.user.org") ){
			org_id = String.valueOf(getSession().getAttribute("dinamica.user.org"));
		}
		rsSignature.addNew();
		rsSignature.setValue("noncestr", nonce_str);
		rsSignature.setValue("timestamp", timestamp);
		rsSignature.setValue("signature", signature);
		rsSignature.setValue("service_tuid", sid);
		rsSignature.setValue("org_id", org_id);
		rsSignature.setValue("appid", appid);
		rsSignature.setValue("access_address", accessAddress);
		publish("_rsSignature", rsSignature);
		return rc;
	}
}
