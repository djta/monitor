package transactions.project.weixin.service;

import java.sql.Types;
import org.json.JSONObject;
import transactions.project.weixin.common.WeixinUtil;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class GetSignatureTranstion extends GenericTransaction{
	public int service(Recordset inputParams) throws Throwable{
		int rc = super.service(inputParams);
		Db db=getDb();
		Recordset signatureRecordset=new Recordset();

		try{
			String weixin_user = "";
			String sid = getRequest().getParameter("sid");
			String code = getRequest().getParameter("code");
			String appid = "";
			if(sid==null || code==null ||sid.length()==0 || code.length()==0){
			}else{
				String query_weixin = "select appid,appsecret from public.wx_service where tuid="+sid;
				Recordset rs_weixin = db.get(query_weixin);
				
				String secret = "";
				if(rs_weixin.getRecordCount() > 0){
					rs_weixin.top();
					if(rs_weixin.next()){
						appid = rs_weixin.getString("appid");
						secret = rs_weixin.getString("appsecret");
					}
					try{
						weixin_user = WeixinUtil.getWeixinUserIdForService(appid,secret,code);
					}catch(Throwable e){
					}
				}
			}
			
			String searchParam=getRequest().getParameter("url");
			
			String urlString = searchParam.substring(0,7).concat(searchParam.substring(7).replace("//", "/"));
			String[] params=searchParam.substring(searchParam.indexOf("?")+1).split("&");
			
			StringBuffer sb=new StringBuffer();
			sb.append(urlString.substring(0,searchParam.indexOf("?")));
			for (int i = 0; i < params.length; i++) {
				String key=params[i].split("=")[0];
				String value=params[i].split("=")[1];
				if(sb.toString().contains("?")){
					sb.append("&"+key+"="+value);
				}else{
					sb.append("?"+key+"="+value);
				}
			}
			String jsapi_ticket = WeixinUtil.getJsTicket(db, sid);
			// 获得js初始化签名
			JSONObject jsonObject = WeixinUtil.signature(jsapi_ticket, sb.toString());
			String nonce_str = jsonObject.getString("noncestr");
			String timestamp = jsonObject.getString("timestamp");
			String signature = jsonObject.getString("signature");
			signatureRecordset.append("noncestr", Types.VARCHAR);
			signatureRecordset.append("timestamp", Types.VARCHAR);
			signatureRecordset.append("signature", Types.VARCHAR);
			signatureRecordset.append("appid", Types.VARCHAR);
			signatureRecordset.append("openid", Types.VARCHAR);
			signatureRecordset.addNew();
			signatureRecordset.setValue("noncestr", nonce_str);
			signatureRecordset.setValue("timestamp", timestamp);
			signatureRecordset.setValue("signature", signature);
			signatureRecordset.setValue("appid", appid);
			signatureRecordset.setValue("openid", weixin_user);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(signatureRecordset.getRecordCount()==0){
			signatureRecordset.append("noncestr", Types.VARCHAR);
			signatureRecordset.append("timestamp", Types.VARCHAR);
			signatureRecordset.append("signature", Types.VARCHAR);
			signatureRecordset.append("appid", Types.VARCHAR);
			signatureRecordset.append("openid", Types.VARCHAR);
			signatureRecordset.addNew();
			signatureRecordset.setValue("noncestr", "");
			signatureRecordset.setValue("timestamp", "");
			signatureRecordset.setValue("signature", "");
			signatureRecordset.setValue("appid", "");
			signatureRecordset.setValue("openid", "");
			publish("query.sql", signatureRecordset);
		}else{
			publish("query.sql", signatureRecordset);
		}
		
		return rc;
	}
}
