package transactions.project.weixin.service;

import java.sql.Types;

import transactions.project.weixin.common.WeixinUtil;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class GetWeixinIdTranstion extends GenericTransaction{
	public int service(Recordset inputParams) throws Throwable{
		int rc = super.service(inputParams);
		Db db=getDb();
		Recordset weixinRecordset=new Recordset();

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
					weixin_user = WeixinUtil.getWeixinUserIdForService(appid,secret,code);
					weixinRecordset.append("weixin_user", Types.VARCHAR);
					weixinRecordset.addNew();
					weixinRecordset.setValue("weixin_user", weixin_user);
				}
			}
			
		}catch(Throwable e){
			e.printStackTrace();
		}
		
		if(weixinRecordset.getRecordCount()==0){
			
			weixinRecordset.append("weixin_user", Types.VARCHAR);
			weixinRecordset.addNew();
			weixinRecordset.setValue("weixin_user", "");
			publish("query.sql", weixinRecordset);
		}else{
			publish("query.sql", weixinRecordset);
		}
		
		return rc;
	}
}
