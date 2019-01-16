package transactions.project.weixin.service;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import transactions.project.weixin.common.WeixinUtil;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;

/**
 * 发送消息
 * @author C
 * 2016-02-25
 */
public class SendMessageService extends GenericTableManager {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Integer serviceId = (inputParams.containsField("service_id") ? inputParams.getInteger("service_id") : null);
		if ( null == serviceId || serviceId.intValue() <= 0 ) {
			throw new Throwable("service_id不能为空");
		}
		Db db = getDb();
		String insert = getResource("insert.sql");
		Recordset rsCorp = db.get(getSQL("SELECT appid, appsecret, api_url FROM wx_service WHERE tuid=${fld:service_id}", inputParams));
		if (null == rsCorp || rsCorp.getRecordCount() <= 0) {
			throw new Throwable("未找到微信服务号");
		}
		rsCorp.first();
		String appid = rsCorp.getString("appid");
		if ( StringUtils.isBlank(appid) ) {
			throw new Throwable("appid不能为空");
		}
		String appsecret = rsCorp.getString("appsecret");
		if ( StringUtils.isBlank(appsecret) ) {
			throw new Throwable("appsecret不能为空");
		}
		String touser = (inputParams.containsField("touser") ? inputParams.getString("touser") : "");
		if ( StringUtils.isBlank(touser) ) {
			throw new Throwable("touser不能为空");
		}
		String message = (inputParams.containsField("message") ? inputParams.getString("message") : "");
		if ( StringUtils.isBlank(message) ) {
			throw new Throwable("message不能为空");
		}
		String accessToken = WeixinUtil.getAccessTokenForService(db, String.valueOf(serviceId));
		JSONObject resultJson = WeixinUtil.sendWeixinMessageTextForService(accessToken, touser, message);
		int errcode = resultJson.getInt("errcode");
		if (0 != errcode) {
			throw new Throwable(resultJson.toString());
		}
		insert = getSQL(insert, inputParams);
		insert = StringUtils.replace(insert, "${send_result}", String.valueOf(1));
		insert = StringUtils.replace(insert, "${remark}", "");
		db.exec(insert);
		return rc;
	}

}
