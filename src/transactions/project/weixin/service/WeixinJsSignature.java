package transactions.project.weixin.service;

import java.math.BigDecimal;
import java.sql.Types;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;

import transactions.project.weixin.common.WeixinUtil;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.security.DinamicaUser;

public class WeixinJsSignature extends GenericTableManager {
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		HttpSession s = getRequest().getSession(true);

		// get authenticated principal
		DinamicaUser user = (DinamicaUser) s.getAttribute("dinamica.security.login");
		String userlogin=user.getName();
		if(null==userlogin){
			throw new Throwable("userlogin not null");
		}
		String out_trade_no = inputParams.getString("out_trade_no");
		if (null == out_trade_no || "".equals(out_trade_no)) {
			throw new Throwable("订单号不能为空");
		}
		// 获得商品信息
		Db db = getDb();
		//更新订单状态为待付款
		//db.exec(getSQL(getResource("update-trade.sql"), inputParams));
		
		Recordset queryTradeRecordset = db.get(getSQL(
				getResource("query-trade.sql"), inputParams));
		queryTradeRecordset.first();
		String out_trade_name = queryTradeRecordset.getString("out_trade_name");
		String province = queryTradeRecordset.getString("province");
		String city = queryTradeRecordset.getString("city");
		String country = queryTradeRecordset.getString("country");
		String detail = queryTradeRecordset.getString("detail");
		String phone = queryTradeRecordset.getString("phone");
		String sale_order_code = queryTradeRecordset.getString("sale_order_code");
		if (null == out_trade_name || "".equals(out_trade_name)) {
			throw new Throwable("订单名称不能为空");
		}
		double out_trade_price = queryTradeRecordset
				.getDouble("out_trade_price");
		BigDecimal b = new BigDecimal(out_trade_price);
		out_trade_price = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

		if (out_trade_price <= 0) {
			throw new Throwable("订单价格不正确");
		}

		String realString=getRequest().getRequestURL().toString();
		String urlString = realString.substring(0,7).concat(realString.substring(7).replace("//", "/"));
		String codeString=getRequest().getParameter("code");
		urlString=urlString+"?out_trade_no="+out_trade_no+"&code="+codeString+"&state=1";//"+getRequest().getParameter("state");
		
		String getServiceSql = getSQL(getResource("query-service.sql"), null);
		Recordset serviceRecordset = db.get(getServiceSql);
		if(serviceRecordset.getRecordCount()==0){
			throw new Throwable("服务号配置不正确。");
		}
		serviceRecordset.first();
		String serviceId = serviceRecordset.getString("service_id");
		String appId = serviceRecordset.getString("app_id");
		String appsecret=serviceRecordset.getString("appsecret");	
		System.out.println("urlString:"+urlString);
		
		String jsapi_ticket = WeixinUtil.getJsTicket(db, serviceId);
		// 获得js初始化签名
		JSONObject jsonObject = WeixinUtil.signature(jsapi_ticket, urlString);
		String nonce_str = jsonObject.getString("noncestr");
		String timestamp = jsonObject.getString("timestamp");
		String signature = jsonObject.getString("signature");
		
		JSONObject jsObject = WeixinUtil.getWeixinAuthAccessTokenForService(appId, appsecret, codeString);
		String weixin_userId=jsObject.getString("openid");
		String accessToken = jsObject.getString("access_token");
		if(null==weixin_userId||"".equals(weixin_userId)){
			throw new Throwable("weixin_userId not null");
		}
		// 获得支付共享地址签名
		

		String addrSign = WeixinUtil.getAddrSign(appId, urlString, timestamp,
				nonce_str, accessToken);

		Recordset newRecordset = new Recordset();
		newRecordset.append("service_id", Types.VARCHAR);
		newRecordset.append("nonce_str", Types.VARCHAR);
		newRecordset.append("timestamp", Types.VARCHAR);
		newRecordset.append("signature", Types.VARCHAR);
		newRecordset.append("app_id", Types.VARCHAR);
		newRecordset.append("out_trade_no", Types.VARCHAR);
		newRecordset.append("sale_order_code", Types.VARCHAR);
		newRecordset.append("addrsign", Types.VARCHAR);
		newRecordset.append("out_trade_name", Types.VARCHAR);
		newRecordset.append("out_trade_price", Types.VARCHAR);
		
		newRecordset.append("province", Types.VARCHAR);
		newRecordset.append("city", Types.VARCHAR);
		newRecordset.append("country", Types.VARCHAR);
		newRecordset.append("detail", Types.VARCHAR);
		newRecordset.append("phone", Types.VARCHAR);
		newRecordset.append("weixin_userid", Types.VARCHAR);
		newRecordset.addNew();
		newRecordset.setValue("service_id", serviceId);
		newRecordset.setValue("nonce_str", nonce_str);
		newRecordset.setValue("timestamp", timestamp);
		newRecordset.setValue("signature", signature);
		newRecordset.setValue("app_id", appId);
		newRecordset.setValue("out_trade_no", out_trade_no);
		newRecordset.setValue("sale_order_code", sale_order_code);
		newRecordset.setValue("addrsign", addrSign);
		newRecordset.setValue("out_trade_name", out_trade_name);
		newRecordset.setValue("out_trade_price",
				String.valueOf(out_trade_price));
		newRecordset.setValue("province", province);
		newRecordset.setValue("city", city);
		newRecordset.setValue("country", country);
		newRecordset.setValue("detail", detail);
		newRecordset.setValue("phone", phone);
		newRecordset.setValue("weixin_userid", weixin_userId);
		publish("_jsRecordset", newRecordset);
		return rc;
	}
}
