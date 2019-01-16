package transactions.project.weixin.service;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.json.JSONException;
import org.json.JSONObject;

import transactions.project.weixin.common.WeixinUtil;
import dinamica.Db;
import dinamica.Jndi;
import dinamica.Recordset;
import dinamica.StringUtil;

/**
 * Servlet implementation class PayTest
 */
public class PayTest extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PayTest() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String _dataSource = getServletContext().getInitParameter(
				"def-datasource");
		String ss = "";
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
		String jsapi_ticket = null;
		try {
			jsapi_ticket = WeixinUtil.getJsTicket(db, "2006");
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String url = request.getRequestURL().toString();
		String url = "http://club.desoft.cn/club/weixin/paytest";

		try {
			JSONObject jsonObject = WeixinUtil.signature(jsapi_ticket, url);
			String nonce_str = (String) jsonObject.get("noncestr");
			String timestamp = (String) jsonObject.get("timestamp");
			String signature = (String) jsonObject.get("signature");
			System.out.println("++++++++++++++++++++++++");
			System.out.println(nonce_str);
			System.out.println(timestamp);
			System.out.println(signature);
			System.out.println("++++++++++++++++++++++++");
			String out_trade_no = WeixinUtil.create_nonce_str();
			Recordset queryTrade = db
					.get(StringUtil.replace(
							StringUtil.replace(
									(WeixinUtil
											.getLocalResource("/transactions/project/weixin/service/query-trade.sql")),
									"${userlogin}", "003".toString()),
							"${out_trade_no}", out_trade_no));
			queryTrade.first();
			String appid = queryTrade.getString("appid");
			String mch_id = queryTrade.getString("mch_id");
			String openid = queryTrade.getString("openid");
			String body = queryTrade.getString("body");
			String notify_url = queryTrade.getString("notify_url");
			String trade_type = queryTrade.getString("trade_type");
			String total_fee = queryTrade.getString("total_fee");
			String partnerKey = queryTrade.getString("partnerkey");
			String spbill_create_ip = request.getHeader("x-forwarded-for");
			if (spbill_create_ip == null || spbill_create_ip.length() == 0
					|| "unknown".equalsIgnoreCase(spbill_create_ip)) {
				spbill_create_ip = request.getHeader("Proxy-Client-IP");
			}
			if (spbill_create_ip == null || spbill_create_ip.length() == 0
					|| "unknown".equalsIgnoreCase(spbill_create_ip)) {
				spbill_create_ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (spbill_create_ip == null || spbill_create_ip.length() == 0
					|| "unknown".equalsIgnoreCase(spbill_create_ip)) {
				spbill_create_ip = request.getRemoteAddr();
			}

			String packageStr = WeixinUtil.getPackage(appid, mch_id,
					spbill_create_ip, nonce_str, openid, body, out_trade_no,
					notify_url, trade_type, total_fee, partnerKey, timestamp);

			String paySign = WeixinUtil.getPaySign(timestamp, nonce_str,
					packageStr, partnerKey, appid);
			
			System.out.println("-----------------------------");
			System.out.println(appid);
			System.out.println(timestamp);
			System.out.println(nonce_str);
			System.out.println(signature);
			System.out.println(packageStr);
			System.out.println(paySign);
			System.out.println(url);
			System.out.println("-----------------------------");
			ss = "<html>"
					+ "<head><script type='text/javascript' src='http://res.wx.qq.com/open/js/jweixin-1.0.0.js'></script></head>"
					+ "<body>"
					+ "<input size=\"100\" style=\"height=30px;width=50px;\"  type='button' value='testpay1' onclick='pay()'>"
					+ "<script> alert(location.href.split('#')[0]);"
					+ "wx.config({"
					+ "debug: true,"
		        	    + "appId: \"wx148eb0b3b8cd10cc\","
		        	    + "timestamp: \"1422951179\","
		        	    + "nonceStr: \"9c838d2e45b2ad1094d42f4ef36764f6\","
		        	    + "signature: \""+signature+"\","
		        	    + "jsApiList: ['chooseWXPay','checkJsApi','showOptionMenu']"
		        	    + "});"
		        	    + "wx.ready(function(){"
		        	    + "	wx.showOptionMenu();"
		        	    + "});"
		        	    + " function pay(){"
		        	    + "	alert('test pay'); "
		        	    + "wx.chooseWXPay({"
		        	    + "timestamp: "+1422951179+", "
		        	    + "nonceStr: '9c838d2e45b2ad1094d42f4ef36764f6', "
		        	    + "package: '"+packageStr+"', "
		        	    + "signType: 'MD5', "
			        	    + "paySign: '"+paySign+"', "
			        	    + " success: function (res) {"
			        	        + " alert('success pay');"
			        	        + "}"
			        	        + "});"
		        	    + "};"
					+ "</script>"
					+ "</body>"
					+ "</html>";

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * String noncestr=JSONObject.getNames("noncestr")[0]; String
		 * timestamp=data.timestamp; String signature=data.signature;
		 */

		response.getOutputStream().write(ss.getBytes("UTF-8"));
		response.setContentType("text/html; charset=UTF-8");
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}
