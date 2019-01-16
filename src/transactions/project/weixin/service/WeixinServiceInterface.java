package transactions.project.weixin.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Element;

import transactions.project.util.order.OrderSplit;
import transactions.project.weixin.client.ClientCustomSSL;
import transactions.project.weixin.common.RequestHandler;
import transactions.project.weixin.common.WeixinUtil;
import transactions.project.weixin.common.XMLParse;
import transactions.project.weixin.common.XMLUtil;
import dinamica.Db;
import dinamica.Jndi;
import dinamica.Recordset;
import dinamica.StringUtil;
import dinamica.security.DinamicaUser;

/**
 * Servlet implementation class WeixinServiceInterface
 */
public class WeixinServiceInterface extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public WeixinServiceInterface() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession s = request.getSession(true);
		DinamicaUser user = (DinamicaUser) s
				.getAttribute("dinamica.security.login");

		String type = request.getParameter("type");
	/**/
		
		
		String _dataSource = getServletContext().getInitParameter(
				"def-datasource");

		String _jndiPrefix = getServletContext()
				.getInitParameter("jndi-prefix");
		String jndiName = _jndiPrefix + _dataSource;
		DataSource _ds = null;
		Connection con = null;
		Db db = null;
		try {
			_ds = Jndi.getDataSource(jndiName);
			con = _ds.getConnection();
			db = new Db(con);
		} catch (Throwable e1) {
			// TODO Auto-generated catch block
			throw new ServletException("获取数据库连接失败");
		}

		JSONObject jsonObject = null;
		String format = null;
		String formatXml = null;
		if ("1".equals(type)) {// 异步向客户发送信息
			String service_tuid = request.getParameter("service_tuid");
			if (null == service_tuid) {
				throw new ServletException("service_tuid不能为空");
			}
			String to_user = request.getParameter("to_user");
			if (null == to_user) {
				throw new ServletException("to_user不能为空");
			}
			// String message=WeixinUtil.getPostString(request);
			String message = request.getParameter("message");
			try {
				String accessToken = WeixinUtil.getAccessTokenForService(db,
						service_tuid);
				jsonObject = WeixinUtil.sendWeixinMessageTextForService(
						accessToken, to_user, message);

			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if ("2".equals(type)) {// 微信支付获取signature
			String serviceId = request.getParameter("service_tuid");
			if (null == serviceId || "".equals(serviceId)) {
				throw new ServletException("service_tuid not null");
			}
			try {
				String jsapi_ticket = WeixinUtil.getJsTicket(db, serviceId);
				// String url = request.getRequestURL().toString();
				String url = request.getParameter("url");
				jsonObject = WeixinUtil.signature(jsapi_ticket, url);
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				throw new ServletException("获取signature失败," + e.getMessage());
			}

		} else if ("3".equals(type)) {// 微信支付获取package
			try {
				String timestamp = request.getParameter("timestamp");
				if (null == timestamp || "".equals(timestamp)) {
					timestamp = WeixinUtil.create_timestamp();
				}
				String nonce_str = request.getParameter("noncestr");
				if (null == nonce_str || "".equals(nonce_str)) {
					nonce_str = WeixinUtil.create_nonce_str();
				}
				String out_trade_no = request.getParameter("out_trade_no");
				if (null == out_trade_no || "".equals(out_trade_no)) {
					throw new ServletException("out_trade_no not null");
				}
				Recordset queryTrade = db
						.get(StringUtil.replace(
								StringUtil.replace(
										(WeixinUtil
												.getLocalResource("/transactions/project/weixin/service/query-trade.sql")),
										"${userlogin}", user.getName()
												.toString()),
								"${out_trade_no}", out_trade_no));
				queryTrade.first();
				String appid = queryTrade.getString("appid");
				String mch_id = queryTrade.getString("mch_id");
				String openid=request.getParameter("weixin_userid");
				String body = queryTrade.getString("body");

				String out_trade_code = queryTrade.getString("out_trade_code");
				if (null == out_trade_code || "".equals(out_trade_code)) {
					throw new Throwable("订单编号不能为空");
				}
				if (null == body || "".equals(body)) {
					throw new Throwable("订单名称不能为空");
				}
				String notify_url = queryTrade.getString("notify_url");
				String trade_type = queryTrade.getString("trade_type");
				double total_fee = queryTrade.getDouble("total_fee");
				String partnerKey = queryTrade.getString("partnerkey");

				BigDecimal b = new BigDecimal(total_fee);
				total_fee = b.setScale(2, BigDecimal.ROUND_HALF_UP)
						.doubleValue();

				if (total_fee <= 0) {
					throw new Throwable("订单价格不正确");
				}

				String spbill_create_ip = WeixinUtil.getIpAddress(request);
				
				String packageStr = WeixinUtil.getPackage(appid, mch_id,
						spbill_create_ip, nonce_str, openid, body,
						out_trade_code, notify_url, trade_type,
						String.valueOf(total_fee), partnerKey, timestamp);

				String paySign = WeixinUtil.getPaySign(timestamp, nonce_str,
						packageStr, partnerKey, appid);
				
				System.out.println("paySign===="+paySign);
				jsonObject = new JSONObject();
				jsonObject.put("package", packageStr);
				jsonObject.put("paysign", paySign);
				jsonObject.put("timestamp", timestamp);
				jsonObject.put("noncestr", nonce_str);
			} catch (Throwable e) {
				throw new ServletException("获取package,paySign失败,"
						+ e.getMessage());
			}

		} else if("4".equals(type)){//微信获得二维码地址
			jsonObject = new JSONObject();

			try{
				String out_trade_no = request.getParameter("out_trade_no");
				String querySql=StringUtil.replace(WeixinUtil
						.getLocalResource("/transactions/project/weixin/service/query-scan.sql"), "${out_trade_no}", out_trade_no);
				Recordset queryRecordset=db.get(querySql);
				queryRecordset.first();
				String nonce_str = WeixinUtil.create_nonce_str();

				// 付款金额
				DecimalFormat df = new DecimalFormat("0");
				String spbill_create_ip = request.getRemoteAddr();
				String appId = queryRecordset.getString("appid");
				String mchid = queryRecordset.getString("mchid");
				String notify_url = queryRecordset.getString("notify_url");
				String partnerKey = queryRecordset.getString("partner_key");
				String amount = queryRecordset.getString("amount");
				String body=queryRecordset.getString("body");
				String trade_order_code=queryRecordset.getString("trade_order_code");
				
				String total_fee =df.format(Double.valueOf(amount)*100);
				String trade_type = "NATIVE";
				Map<String, String> packageParams = new HashMap<String, String>();
				packageParams.put("appid", appId);
				packageParams.put("mch_id", mchid);
				packageParams.put("nonce_str", nonce_str);
				packageParams.put("body", body);
				packageParams.put("out_trade_no", trade_order_code);
				packageParams.put("total_fee", total_fee);
				packageParams.put("spbill_create_ip", spbill_create_ip);
				packageParams.put("notify_url", notify_url);
				packageParams.put("trade_type", "NATIVE");

				String sign = WeixinUtil.getsign2(packageParams, partnerKey);
				String payXml = "<xml>" + "<appid>" + appId + "</appid>" + "<body>"
						+ body + "</body>" + "<mch_id>" + mchid + "</mch_id>"
						+ "<nonce_str>" + nonce_str + "</nonce_str>" + "<notify_url>"
						+ notify_url + "</notify_url>" + "<out_trade_no>"
						+ trade_order_code + "</out_trade_no>" + "<spbill_create_ip>"
						+ spbill_create_ip + "</spbill_create_ip>" + "<total_fee>"
						+ total_fee + "</total_fee>" + "<trade_type>" + trade_type
						+ "</trade_type>" + "<sign>" + sign + "</sign>" + "</xml>";
				System.out.println(payXml);
				String resultXml = (String) WeixinUtil.httpRequest(
						WeixinUtil.WX_PAY_UNIFIEDORDER_URL, "POST", payXml, "1");
				Element element = XMLParse.getElement(resultXml);
				String return_code = element.getElementsByTagName("return_code")
				.item(0).getTextContent();
				String result_code =element.getElementsByTagName("result_code")
						.item(0).getTextContent();

				if ("SUCCESS".equals(return_code)) {
					if ("SUCCESS".equals(result_code)) {
						String code_url=element.getElementsByTagName("code_url")
						.item(0).getTextContent();
						/*String prepay_id=element.getElementsByTagName("prepay_id")
						.item(0).getTextContent();*/
						jsonObject.put("flag", "1");
						jsonObject.put("code_url", code_url);
					} else {
						String err_code_des=element.getElementsByTagName("err_code_des")
								.item(0).getTextContent();
						jsonObject.put("flag", "0");
						jsonObject.put("code_url", err_code_des);
					}
				}else{
					String return_msg = element.getElementsByTagName("return_msg")
					.item(0).getTextContent();
					
					jsonObject.put("flag", "0");
					jsonObject.put("code_url", return_msg);
				}
				
			}catch(Throwable e){
				try {
					jsonObject.put("flag", "0");
					jsonObject.put("code_url", "");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
				e.printStackTrace();
			}
			
			
		}else if("5".equals(type)){//退款提交接口
		try {
			jsonObject = new JSONObject();
			String thdh=request.getParameter("thdh");
			if("".equals(thdh)||null==thdh){
				throw new Throwable("thdh is not null");
			}
			String querySql=StringUtil.replace(WeixinUtil
					.getLocalResource("/transactions/project/weixin/service/query-th.sql"), "${thdh}", thdh);
			Recordset queryRecordset=db.get(querySql);
			queryRecordset.first();
			String trade_no=queryRecordset.getString("trade_no");
			String total_money=queryRecordset.getString("total_money");
			String refund_fee=queryRecordset.getString("refund_fee");

			String out_refund_no = thdh;// 退款单号
			DecimalFormat df = new DecimalFormat("0");
			String total_fee_format =df.format(Double.valueOf(total_money)*100);
			String refund_fee_format =df.format(Double.valueOf(refund_fee)*100);

			String nonce_str = WeixinUtil.create_nonce_str();// 随机字符串

			String appid = queryRecordset.getString("appid");
			String appsecret = queryRecordset.getString("appsecret");
			String mch_id = queryRecordset.getString("mchid");
			String op_user_id = queryRecordset.getString("mchid");//就是MCHID
			String partnerkey = queryRecordset.getString("partner_key");//商户平台上的那个KEY
			String weixinCacertPath=queryRecordset.getString("cacert_path");
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			packageParams.put("appid", appid);
			packageParams.put("mch_id", mch_id);
			packageParams.put("nonce_str", nonce_str);
			packageParams.put("transaction_id", trade_no);
			packageParams.put("out_refund_no", out_refund_no);
			packageParams.put("total_fee", total_fee_format);
			packageParams.put("refund_fee", refund_fee_format);
			packageParams.put("op_user_id", op_user_id);

			RequestHandler reqHandler = new RequestHandler(
					null, null);
			reqHandler.init(appid, appsecret, partnerkey);

			String sign = reqHandler.createSign(packageParams);
			String xml = "<xml>" + "<appid>" + appid + "</appid>" + "<mch_id>"
					+ mch_id + "</mch_id>" + "<nonce_str>" + nonce_str
					+ "</nonce_str>" + "<sign><![CDATA[" + sign + "]]></sign>"
					+ "<transaction_id>" + trade_no + "</transaction_id>"
					+ "<out_refund_no>" + out_refund_no + "</out_refund_no>"
					+ "<total_fee>" + total_fee_format + "</total_fee>"
					+ "<refund_fee>" + refund_fee_format + "</refund_fee>"
					+ "<op_user_id>" + op_user_id + "</op_user_id>" + "</xml>";
			String createOrderURL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
			String return_code ="";
			ClientCustomSSL.setWEIXIN_PARTNER(partnerkey);
			ClientCustomSSL.setWEIXIN_CACERT_PATH(weixinCacertPath);
			String returnString= ClientCustomSSL.doRefund(createOrderURL, xml);
			Map returnMap=XMLUtil.doXMLParse(returnString);
			return_code=(String) returnMap.get("return_code");
			if("SUCCESS".equals(return_code)){
				jsonObject.put("flag", "1");
			}else{
				jsonObject.put("flag", "0");
			}
			}catch(Throwable e){
				e.printStackTrace();
			}
			
		}else {
			//微信付款成功
			String postString = WeixinUtil.getPostString(request);
			try {
				Element element = XMLParse.getElement(postString);
				String result_code = element
						.getElementsByTagName("result_code").item(0)
						.getTextContent();
				if ("SUCCESS".equals(result_code)) {
					try {
						String out_trade_no = element
								.getElementsByTagName("out_trade_no").item(0)
								.getTextContent();
						
						//查询订单状态
						String queryStateSql =StringUtil.replace(WeixinUtil
								.getLocalResource("/transactions/project/zhifubao/jiekou/query-state.sql"), "${out_trade_no}", out_trade_no) ;
						Recordset queryStateSet= db.get(queryStateSql);
						queryStateSet.first();
						int state=queryStateSet.getInteger("state");
						String user2=queryStateSet.getString ("createdby");
						if(1==state||0==state){
							//查询是否订金单
							String queryTrade =StringUtil.replace(WeixinUtil
									.getLocalResource("/transactions/project/zhifubao/jiekou/query_trade.sql"), "${out_trade_no}", out_trade_no) ;
							Recordset queryTrade_dopit = db.get(queryTrade);
							queryTrade_dopit.first();

							double total_price=queryTrade_dopit.getDouble("total_price");
							double deposit=queryTrade_dopit.getDouble("deposit");
							double price=total_price-deposit;
							double use_money=queryTrade_dopit.getDouble("use_money");
							int jifen=queryTrade_dopit.getInt ("jifen");
							int state2=queryTrade_dopit.getInt ("state");
							int is_queren=queryTrade_dopit.getInt ("payment_success");
								
							//补尾款
							if(deposit>0&&state2==2&&is_queren==1){
								//将原来交易单 订金单关联id字段  修改成i新增后的交易单号
								String sql="select nextval('seq_ws_trade_order') as trade_order_id";
								Recordset rsId = db.get(sql);
								rsId.first();
								String trade_order_id =rsId.getString("trade_order_id");
								
								String queryAccountSql = WeixinUtil.getLocalResource("/transactions/project/o2o/sql/ck/query_account.sql");
								queryAccountSql = StringUtil.replace(queryAccountSql, "${user}", String.valueOf(user));
								queryAccountSql = transactions.project.util.ServiceTools.getSQL(queryAccountSql,null,request);
								Recordset queryAccount = db.get(queryAccountSql);
								if(queryAccount.next()){
									//查询当前用户积分
									double account_balance=queryAccount.getDouble("account_balance");
									int integral=queryAccount.getInt("integral");
									//查询订单实际明细
									String queryOrderSql =StringUtil.replace(WeixinUtil
											.getLocalResource("/transactions/project/zhifubao/jiekou/query_order.sql"), "${out_trade_no}", out_trade_no) ;
									Recordset queryOrder = db.get(queryOrderSql);
									if(queryOrder.next()){
										int all_jifen=queryOrder.getInt("jifen");
										int actual_jifen=queryOrder.getInt("actual_jifen");
										
										double  final_price=  total_price-deposit;//应该付多少尾款
										int  final_integral=all_jifen-jifen;//可以抵用多少积分

										double actual_balance2=0;
										int actual_jifen2=0;
										
										if(final_integral>0&&final_integral>integral){
											final_price=final_price-integral;
											actual_jifen2=integral;
										}else	if(final_integral>0&&final_integral<integral){
											final_price=final_price-final_integral;
											actual_jifen2=final_integral;
										}
										
										if(account_balance>0&&final_price>0){
											 Double obj1 = new Double(account_balance);
										     Double obj2 = new Double(final_price);
										     int retval =  obj1.compareTo(obj2);
										     if(retval > 0) {
										    	 actual_balance2=  final_price;
										     }
										     else if(retval < 0) {
										    		actual_balance2=account_balance;
										     }
										}
										//新增一条交易单
													String insert_Trade = WeixinUtil.getLocalResource("/transactions/project/o2o/sql/ck/insertTrade.sql");
													insert_Trade = StringUtil.replace(insert_Trade, "${user}", String.valueOf(user));
													insert_Trade=StringUtils.replace(insert_Trade, "${final_price}", String.valueOf(final_price));
													insert_Trade=StringUtils.replace(insert_Trade, "${actual_jifen2}", String.valueOf(actual_jifen2));
													insert_Trade=StringUtils.replace(insert_Trade, "${actual_balance2}", String.valueOf(actual_balance2));
													insert_Trade=StringUtils.replace(insert_Trade, "${trade_order_id}", String.valueOf(trade_order_id));
													insert_Trade=StringUtils.replace(insert_Trade, "${price}", String.valueOf(price));
													insert_Trade = transactions.project.util.ServiceTools.getSQL(insert_Trade,null,request);
										db.exec(insert_Trade);
										
										//新增一条订单
										String insert_Sale = WeixinUtil.getLocalResource("/transactions/project/o2o/sql/ck/insertSalse.sql");
										insert_Sale = StringUtil.replace(insert_Sale, "${user}", String.valueOf(user));
										insert_Sale=StringUtils.replace(insert_Sale, "${trade_order_id}", String.valueOf(trade_order_id));
										insert_Sale=StringUtils.replace(insert_Sale, "${actual_jifen2}", String.valueOf(actual_jifen2));
										insert_Sale=StringUtils.replace(insert_Sale, "${actual_balance2}", String.valueOf(actual_balance2));
										insert_Sale=StringUtils.replace(insert_Sale, "${total_price}", String.valueOf(total_price));
										insert_Sale=StringUtils.replace(insert_Sale, "${deposit}", String.valueOf(deposit));
										insert_Sale = transactions.project.util.ServiceTools.getSQL(insert_Sale,null,request);
										db.exec(insert_Sale);
										String sql2="select currval('seq_ws_sale_order') as ws_sale_order_id";
										Recordset rsId2 = db.get(sql2);
										rsId2.first();
										String ws_sale_order_id =rsId2.getString("ws_sale_order_id");
										//新增一条订单详情
										String insert_Sale_detail =StringUtil.replace(WeixinUtil
												.getLocalResource("/transactions/project/o2o/sql/ck/insertSalseDetail.sql"), "${out_trade_no}", out_trade_no) ;
										insert_Sale_detail=StringUtils.replace(insert_Sale_detail, "${ws_sale_order_id}", String.valueOf(ws_sale_order_id));
										insert_Sale_detail=StringUtils.replace(insert_Sale_detail, "${actual_jifen2}", String.valueOf(actual_jifen2));
										insert_Sale_detail=StringUtils.replace(insert_Sale_detail, "${actual_balance2}", String.valueOf(actual_balance2));
										insert_Sale_detail=StringUtils.replace(insert_Sale_detail, "${total_price}", String.valueOf(total_price));
										insert_Sale_detail=StringUtils.replace(insert_Sale_detail, "${deposit}", String.valueOf(deposit));
										db.exec(insert_Sale_detail);
										//循环新增订单参数
										String sql3="select currval('seq_ws_sale_order_detail') as ws_sale_order_detail_id";
										Recordset rsId3 = db.get(sql3);
										rsId3.first();
										String ws_sale_order_detail_id =rsId3.getString("ws_sale_order_detail_id");
										String query_Sale_param_countSQL =StringUtil.replace(WeixinUtil
												.getLocalResource("/transactions/project/zhifubao/jiekou/queryParamCount.sql"), "${out_trade_no}", out_trade_no) ;
										Recordset query_Sale_param_count = db.get(query_Sale_param_countSQL);
										if(query_Sale_param_count.next())
										{
											int ct=query_Sale_param_count.getInt("ct");
											String query_Sale_param_SQL =StringUtil.replace(WeixinUtil
													.getLocalResource("/transactions/project/zhifubao/jiekou/queryParam.sql"), "${out_trade_no}", out_trade_no) ;
											Recordset query_Sale_param = db.get(query_Sale_param_SQL);
											while(query_Sale_param.next())
											{
												int tuid=query_Sale_param.getInt("tuid");
												String insertParam =StringUtil.replace(WeixinUtil
														.getLocalResource("/transactions/project/zhifubao/jiekou/insertParam.sql"), "${out_trade_no}", out_trade_no) ;
													insertParam=StringUtils.replace(insertParam, "${tuid}", String.valueOf(tuid));
													insertParam=StringUtils.replace(insertParam, "${ws_sale_order_id}", String.valueOf(ws_sale_order_id));
													insertParam=StringUtils.replace(insertParam, "${ws_sale_order_detail_id}", String.valueOf(ws_sale_order_detail_id));
													db.exec(insertParam);
											}
										}
									}
								}
							}
								
							//出库
							String insert_leave_stock = WeixinUtil.getLocalResource("/transactions/project/o2o/sql/ck/update-stock_pile.sql");
							insert_leave_stock = StringUtil.replace(insert_leave_stock, "${user}", String.valueOf(user));
							insert_leave_stock = transactions.project.util.ServiceTools.getSQL(insert_leave_stock,null,request);
							
							String insert_leave_stock_detail = WeixinUtil.getLocalResource("/transactions/project/o2o/sql/ck/update-stock_pile.sql");
							insert_leave_stock_detail = StringUtil.replace(insert_leave_stock_detail, "${user}",  String.valueOf(user));
							insert_leave_stock_detail = transactions.project.util.ServiceTools.getSQL(insert_leave_stock_detail,null,request);
							
							String update_stock_pile = WeixinUtil.getLocalResource("/transactions/project/o2o/sql/ck/update-stock_pile.sql");
							update_stock_pile = StringUtil.replace(update_stock_pile, "${user}",  String.valueOf(user));
							update_stock_pile = transactions.project.util.ServiceTools.getSQL(update_stock_pile,null,request);
							
							String queryShopNumber=
									transactions.project.util.ServiceTools.getSQL(WeixinUtil.getLocalResource("/transactions/project/zhifubao/jiekou/query_shop_car.sql"),null,request);
							queryShopNumber = StringUtil.replace(queryShopNumber,"${out_trade_no}",out_trade_no);
							
							Recordset rsShopNumber_ = db.get(queryShopNumber);
							while(rsShopNumber_.next())
							{
								String good_id=rsShopNumber_.getString("good_id");
								String actual_price=rsShopNumber_.getString("purchase_price");
								String purchase_quantity=rsShopNumber_.getString("purchase_quantity");
								
								String querySeq="select nextval('seq_ws_leave_stock') as leave_stock_id";
								Recordset rsSeq = db.get(querySeq);
								rsSeq.first();
								String leave_stock_id =rsSeq.getString("leave_stock_id");
								
								String insert_leave_stock_= StringUtils.replace(insert_leave_stock, "${leave_stock_id}", leave_stock_id);
								insert_leave_stock_= StringUtils.replace(insert_leave_stock_, "${good_id}", good_id);
								insert_leave_stock_= StringUtils.replace(insert_leave_stock_, "${quantity}", purchase_quantity);
								
								db.exec(insert_leave_stock_);
								
								String insert_leave_stock_detail_= StringUtils.replace(insert_leave_stock_detail, "${leave_stock_id}", leave_stock_id);
								 insert_leave_stock_detail_= StringUtils.replace(insert_leave_stock_detail_, "${goods_id}", good_id);
								 insert_leave_stock_detail_= StringUtils.replace(insert_leave_stock_detail_, "${quantity}", purchase_quantity);
								 insert_leave_stock_detail_= StringUtils.replace(insert_leave_stock_detail_, "${price}", actual_price);
								db.exec(insert_leave_stock_detail_);
								
								boolean flag2=true;
								Date appointment_date=rsShopNumber_.getDate("appointment_date");
								int is_unlimited=rsShopNumber_.getInt("is_unlimited");
								//修改预约库存
								if(appointment_date!=null){
									//查询是否有设置数量
									String query_ws_bespeak_numSql =StringUtil.replace(WeixinUtil.getLocalResource("/transactions/project/o2o/sql/ck/query_ws_bespeak_num.sql"), "${good_id}", String.valueOf(good_id)) ;
									Recordset query_ws_bespeak_num= db.get(query_ws_bespeak_numSql);
									query_ws_bespeak_num.top();
									
									int	bespeak_num=0;
									if(query_ws_bespeak_num.next()){
											bespeak_num=query_ws_bespeak_num.getInteger("bespeak_max_num");
									}
									if(bespeak_num>=0){
										String update_stock_2Sql =StringUtil.replace(WeixinUtil
												.getLocalResource("/transactions/project/o2o/sql/ck/update-stock_pile_appoitment.sql"), "${trade_no}", out_trade_no) ;
										//String update_stock_2Sql=getSQL(getLocalResource("/transactions/project/o2o/sql/ck/update-stock_pile_appoitment.sql"),inputParams);
										String update_stock_2= StringUtils.replace(update_stock_2Sql, "${good_id}", good_id);
										update_stock_2= StringUtils.replace(update_stock_2, "${quantity}", purchase_quantity);
										db.exec(update_stock_2);
									}
									String update_stock_pile_= StringUtils.replace(update_stock_pile, "${good_id}", good_id);
									update_stock_pile_= StringUtils.replace(update_stock_pile_, "${quantity}", purchase_quantity);
									db.exec(update_stock_pile_);
									flag2=false;
								}
								if(flag2){
								
										if(is_unlimited!=0){
											String update_stock_2Sql =StringUtil.replace(WeixinUtil
													.getLocalResource("/transactions/project/o2o/sql/ck/update-stock_pile_appoitment.sql"), "${trade_no}", out_trade_no) ;
											//String update_stock_2Sql=getSQL(getLocalResource("/transactions/project/o2o/sql/ck/update-stock_pile_appoitment.sql"),inputParams);
											String update_stock_2= StringUtils.replace(update_stock_2Sql, "${good_id}", good_id);
											update_stock_2= StringUtils.replace(update_stock_2, "${quantity}", purchase_quantity);
									}
								
								
								
									String update_stock_pile_= StringUtils.replace(update_stock_pile, "${good_id}", good_id);
									update_stock_pile_= StringUtils.replace(update_stock_pile_, "${quantity}", purchase_quantity);
									db.exec(update_stock_pile_);
								}
							}
					
							//此订单点击了支付
							String update_actual1="update ws_trade_order set  "
									+"payment_success= 1,state = 2 "	//state=2支付成功
									+ "where trade_order_code = '"+out_trade_no+"'";
							db.exec(update_actual1);
							
							//修改订单状态，购买成功
							String updateTradeSql =StringUtil.replace(WeixinUtil
									.getLocalResource("/transactions/project/zhifubao/jiekou/update-trade.sql"), "${sale_order_code}", out_trade_no) ;
							updateTradeSql = transactions.project.util.ServiceTools.getSQL(updateTradeSql,null,request);	//替换更新日期字段
							db.exec(updateTradeSql);
							/*//充值卡充钱
							String updateAccountSql =StringUtil.replace(StringUtil.replace(WeixinUtil
									.getLocalResource("/transactions/project/zhifubao/jiekou/update-account.sql"), "${sale_order_code}", out_trade_no), "${userlogin}", user2) ;
							db.exec(updateAccountSql);
							//新增充值历史
							String insertBlanceSql =WeixinUtil
									.getLocalResource("/transactions/project/zhifubao/jiekou/insert-account_list.sql") ;
							insertBlanceSql =StringUtil.replace(insertBlanceSql, "${sale_order_code}", out_trade_no) ;*/
							// 账户分成 2015-08-29
							ClassLoader loader = Thread.currentThread().getContextClassLoader();
							OrderSplit os = (OrderSplit) loader.loadClass("transactions.project.util.order.OrderSplit").newInstance();
							os.setDb(db);
							os.setSaleOrderCode(out_trade_no);
							os.service(new Recordset());
						}
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					format = "<xml><return_code><![CDATA[%1$s]]></return_code><return_msg><![CDATA[%2$s]]></return_msg></xml>";
					formatXml = String.format(format, "SUCCESS", "");
				} else {
					format = "<xml><return_code><![CDATA[%1$s]]></return_code><return_msg><![CDATA[%2$s]]></return_msg></xml>";
					formatXml = String.format(format, "FAIL", "");
				}
				//插入历史
				String appid = element.getElementsByTagName("appid")
						.item(0).getTextContent();
				String bank_type = element
						.getElementsByTagName("bank_type").item(0)
						.getTextContent();
				String cash_fee = element.getElementsByTagName("cash_fee")
						.item(0).getTextContent();
				String fee_type = element.getElementsByTagName("fee_type")
						.item(0).getTextContent();
				String is_subscribe = element
						.getElementsByTagName("is_subscribe").item(0)
						.getTextContent();
				String mch_id = element.getElementsByTagName("mch_id")
						.item(0).getTextContent();
				String nonce_str = element
						.getElementsByTagName("nonce_str").item(0)
						.getTextContent();
				String openid = element.getElementsByTagName("openid")
						.item(0).getTextContent();

				String out_trade_no = element
						.getElementsByTagName("out_trade_no").item(0)
						.getTextContent();
				String return_code = element
						.getElementsByTagName("return_code").item(0)
						.getTextContent();
				String sign = element.getElementsByTagName("sign").item(0)
						.getTextContent();
				String time_end = element.getElementsByTagName("time_end")
						.item(0).getTextContent();
				String total_fee = element
						.getElementsByTagName("total_fee").item(0)
						.getTextContent();
				String trade_type = element
						.getElementsByTagName("trade_type").item(0)
						.getTextContent();
				String transaction_id = element
						.getElementsByTagName("transaction_id").item(0)
						.getTextContent();
				String[] list = { "appid", "bank_type", "cash_fee",
						"fee_type", "is_subscribe", "mch_id", "nonce_str",
						"openid", "out_trade_no", "result_code",
						"return_code", "sign", "time_end", "total_fee",
						"trade_type", "transaction_id" };
				String[] value = { appid, bank_type, cash_fee, fee_type,
						is_subscribe, mch_id, nonce_str, openid,
						out_trade_no, result_code, return_code, sign,
						time_end, total_fee, trade_type, transaction_id };
				String insertHistorySql = WeixinUtil
						.getLocalResource("/transactions/project/weixin/service/insert-trade_history.sql");
				for (int i = 0; i < list.length; i++) {
					insertHistorySql = StringUtil.replace(insertHistorySql,
							"${fld:" + list[i] + "}", "'" + value[i] + "'");
				}
				db.exec(insertHistorySql);
			/*	//修改订单状态，购买成功
				String updateTradeSql = WeixinUtil.getLocalResource("/transactions/project/weixin/service/update-trade.sql");
				updateTradeSql = StringUtil.replace(updateTradeSql, "${sale_order_code}",  String.valueOf(out_trade_no));
				updateTradeSql = transactions.project.util.ServiceTools.getSQL(updateTradeSql,null,request);*/
				
				
				/*String updateTradeSql =StringUtil.replace(WeixinUtil
						.getLocalResource("/transactions/project/weixin/service/update-trade.sql"), "${sale_order_code}", out_trade_no) ;*/
				//db.exec(updateTradeSql);
				//充值卡充钱
				String updateAccountSql =StringUtil.replace(StringUtil.replace(WeixinUtil
						.getLocalResource("/transactions/project/weixin/service/update-account.sql"), "${weixin_id}", openid), "${sale_order_code}", out_trade_no) ;
				
				db.exec(updateAccountSql);
				//新增充值历史
				String insertBlanceSql =StringUtil.replace(WeixinUtil
						.getLocalResource("/transactions/project/weixin/service/insert-account_list.sql"), "${weixin_id}", openid) ;
				insertBlanceSql =StringUtil.replace(insertBlanceSql, "${sale_order_code}", out_trade_no) ;

				String queryBlanceSql =StringUtil.replace(StringUtil.replace(WeixinUtil
						.getLocalResource("/transactions/project/weixin/service/query-trade_account.sql"), "${weixin_id}", openid), "${sale_order_code}", out_trade_no) ;
				Recordset queryBlanceRecordset= db.get(queryBlanceSql);
				queryBlanceRecordset.top();
				while(queryBlanceRecordset.next()){
					String amount_fee=queryBlanceRecordset.getString("amount_fee");
					String goods_id=queryBlanceRecordset.getString("goods_id");
					db.exec(StringUtil.replace(StringUtil.replace(insertBlanceSql, "${goods_id}", goods_id), "${amount_fee}", amount_fee));
				}
				
				// 账户分成 2015-08-29
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				OrderSplit os = (OrderSplit) loader.loadClass("transactions.project.util.order.OrderSplit").newInstance();
				os.setDb(db);
				os.setSaleOrderCode(out_trade_no);
				os.service(new Recordset());
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (null == type) {
			response.getOutputStream().write(formatXml.getBytes("UTF-8"));
			response.setContentType("text/xml; charset=UTF-8");
		} else {
			response.getOutputStream().write(
					jsonObject.toString().getBytes("UTF-8"));
			response.setContentType("text/json; charset=UTF-8");
		}

	}
}
