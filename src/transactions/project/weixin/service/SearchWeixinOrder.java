package transactions.project.weixin.service;

import java.sql.Types;

import org.w3c.dom.Element;

import transactions.project.weixin.common.WeixinUtil;
import transactions.project.weixin.common.XMLParse;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class SearchWeixinOrder extends GenericTransaction
{

	public int service(Recordset inputs) throws Throwable
	{

		int rc = super.service(inputs);
		String pagingName = getConfig().getConfigValue("paging-recordset","query.sql");
		String totalName = getConfig().getConfigValue("total-recordset","query-page.sql");
		
		Recordset rs=new Recordset();
		String nonce_str=WeixinUtil.create_nonce_str();
		Db db=getDb();
		rs.append("return_code", Types.VARCHAR);
		rs.append("result_code", Types.VARCHAR);
		rs.append("transaction_id", Types.VARCHAR);
		rs.append("out_trade_no", Types.VARCHAR);
		rs.append("return_msg", Types.VARCHAR);
		rs.append("appid", Types.VARCHAR);
		rs.append("mch_id", Types.VARCHAR);
		rs.append("nonce_str", Types.VARCHAR);
		rs.append("sign", Types.VARCHAR);
		rs.append("err_code", Types.VARCHAR);
		rs.append("err_code_des", Types.VARCHAR);
		rs.append("device_info", Types.VARCHAR);
		rs.append("openid", Types.VARCHAR);
		rs.append("is_subscribe", Types.VARCHAR);
		rs.append("trade_type", Types.VARCHAR);
		rs.append("trade_state", Types.VARCHAR);
		rs.append("bank_type", Types.VARCHAR);
		rs.append("total_fee", Types.VARCHAR);
		rs.append("fee_type", Types.VARCHAR);
		rs.append("cash_fee", Types.VARCHAR);
		rs.append("cash_fee_type", Types.VARCHAR);
		rs.append("coupon_fee", Types.VARCHAR);
		rs.append("coupon_count", Types.VARCHAR);
		rs.append("attach", Types.VARCHAR);
		rs.append("time_end", Types.VARCHAR);
		
		Recordset appRecordset=db.get(getSQL(getResource("query-service.sql"), inputs));
		if(appRecordset.getRecordCount()==0){
			Recordset rsPage = new Recordset();
			
			rsPage.append("total", Types.INTEGER);
			rsPage.append("pageno", Types.INTEGER);
			rsPage.append("pages", Types.INTEGER);
			rsPage.addNew();
			rsPage.setValue("total", 1);
			rsPage.setValue("pageno", 1);
			rsPage.setValue("pages", 1);
			
			publish(pagingName, rs);
			publish(totalName, rsPage);

			return rc;
		}
		appRecordset.first();
		String appid=appRecordset.getString("appid");
		String mch_id=appRecordset.getString("mchid");
		String transaction_id=inputs.getString("transaction_id");
		String partner_key=appRecordset.getString("partner_key");
		if(null!=transaction_id&&!"".equals(transaction_id)){
			String orderInfoString=WeixinUtil.orderQuery(appid, mch_id, transaction_id, null, nonce_str, partner_key);
			Element element = XMLParse.getElement(orderInfoString);
			String return_code = element
					.getElementsByTagName("return_code").item(0)
					.getTextContent();
			String return_msg = element
					.getElementsByTagName("return_msg").item(0)
					.getTextContent();
			if("SUCCESS".equals(return_code)){
				String result_code = element
						.getElementsByTagName("result_code").item(0)
						.getTextContent();
				if("SUCCESS".equals(result_code)){
					String transaction_id2=element.getElementsByTagName("transaction_id").item(0).getTextContent();
					String out_trade_no=element.getElementsByTagName("out_trade_no").item(0).getTextContent();
					String appid2=element.getElementsByTagName("appid").item(0).getTextContent();
					String mch_id2=element.getElementsByTagName("mch_id").item(0).getTextContent();
					String nonce_str2=element.getElementsByTagName("nonce_str").item(0).getTextContent();
					String sign=element.getElementsByTagName("sign").item(0).getTextContent();
					//String device_info=element.getElementsByTagName("device_info").item(0).getTextContent();
					String openid=element.getElementsByTagName("openid").item(0).getTextContent();
					String is_subscribe=element.getElementsByTagName("is_subscribe").item(0).getTextContent();
					String trade_type=element.getElementsByTagName("trade_type").item(0).getTextContent();
					String trade_state=element.getElementsByTagName("trade_state").item(0).getTextContent();
					String bank_type=element.getElementsByTagName("bank_type").item(0).getTextContent();
					String total_fee=element.getElementsByTagName("total_fee").item(0).getTextContent();
					String fee_type=element.getElementsByTagName("fee_type").item(0).getTextContent();
					String cash_fee=element.getElementsByTagName("cash_fee").item(0).getTextContent();
					//String cash_fee_type=element.getElementsByTagName("cash_fee_type").item(0).getTextContent();
					//String coupon_fee=element.getElementsByTagName("coupon_fee").item(0).getTextContent();
					//String coupon_count=element.getElementsByTagName("coupon_count").item(0).getTextContent();
					String attach=element.getElementsByTagName("attach").item(0).getTextContent();
					String time_end=element.getElementsByTagName("time_end").item(0).getTextContent();
					rs.addNew();
					rs.setValue("result_code",result_code);
					rs.setValue("transaction_id",transaction_id2);
					rs.setValue("out_trade_no",out_trade_no);
					rs.setValue("appid",appid2);
					rs.setValue("mch_id",mch_id2);
					rs.setValue("nonce_str",nonce_str2);
					rs.setValue("sign",sign);
					//rs.setValue("device_info",device_info);
					rs.setValue("openid",openid);
					rs.setValue("is_subscribe",is_subscribe);
					rs.setValue("trade_type",trade_type);
					rs.setValue("trade_state",trade_state);
					rs.setValue("bank_type",bank_type);
					rs.setValue("total_fee",total_fee);
					rs.setValue("fee_type",fee_type);
					rs.setValue("cash_fee",cash_fee);
					//rs.setValue("cash_fee_type",cash_fee_type);
					//rs.setValue("coupon_fee",coupon_fee);
					//rs.setValue("coupon_count",coupon_count);
					rs.setValue("attach",attach);
					rs.setValue("time_end",time_end);
					rs.setValue("return_code",return_code);
				}else{
					rs.addNew();
					String err_code=element.getElementsByTagName("err_code").item(0).getTextContent();
					String err_code_des=element.getElementsByTagName("err_code_des").item(0).getTextContent();
					rs.setValue("err_code",err_code);
					rs.setValue("err_code_des",err_code_des);
					rs.setValue("return_code", return_code);
					rs.setValue("return_msg", return_msg);
				}
			}else{
				rs.addNew();
				rs.setValue("return_code", return_code);
				rs.setValue("return_msg", return_msg);
			}
		}
		
		Recordset rsPage = new Recordset();
		
		rsPage.append("total", Types.INTEGER);
		rsPage.append("pageno", Types.INTEGER);
		rsPage.append("pages", Types.INTEGER);
		rsPage.addNew();
		rsPage.setValue("total", 1);
		rsPage.setValue("pageno", 1);
		rsPage.setValue("pages", 1);
		
		publish(pagingName, rs);
		publish(totalName, rsPage);
		
		return rc;
		
	}

}
