package transactions.project.weixin.corp;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import transactions.project.weixin.common.AesException;
import transactions.project.weixin.common.WeixinUtil;
import transactions.project.weixin.common.XMLParse;
import dinamica.Db;
import dinamica.Jndi;
import dinamica.Recordset;
import dinamica.StringUtil;

/**
 * Servlet implementation class CoreController
 */
public class WeixinController extends HttpServlet {

	private static final long serialVersionUID = 5881481743772142579L;

	/**
	 * Default constructor.
	 */
	public WeixinController() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String agentid = request.getParameter("agentid");
		String corpid = null;
		if (null == agentid) {
			throw new ServletException("企业号认证失败, agentid不能为空");
		}

		String token = "";
		String encodingAESKey = null;

		String _dataSource = getServletContext().getInitParameter(
				"def-datasource");
		String _jndiPrefix = getServletContext()
				.getInitParameter("jndi-prefix");
		String jndiName = _jndiPrefix + _dataSource;
		DataSource _ds = null;
		Connection con = null;
		try {
			_ds = Jndi.getDataSource(jndiName);
			con = _ds.getConnection();
			Db db = new Db(con);
			Recordset queryCorpRecordset = db
					.get(StringUtil
							.replace(
									"select t1.callback_token,t1.encoding_aeskey,t2.corp_id from wx_company_app t1 "
											+ "join wx_company t2 on t1.company_id=t2.tuid where t1.tuid='${tuid}'",
									"${tuid}", agentid));
			queryCorpRecordset.first();
			corpid = queryCorpRecordset.getString("corp_id");
			if (null == corpid || "".equals(corpid)) {
				throw new Exception("corpid不能为空");
			}
			token = queryCorpRecordset.getString("callback_token");
			if (null == token || "".equals(token)) {
				throw new Exception("callback_token不能为空");
			}
			encodingAESKey = queryCorpRecordset.getString("encoding_aeskey");
			if (null == encodingAESKey || "".equals(encodingAESKey)) {
				throw new Exception("encodingAESKey不能为空");
			}
		} catch (Throwable e1) {
			e1.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		// 设置编码
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");

		// 企业号的基本信息，配置时填写
		String msg_signature = request.getParameter("msg_signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		PrintWriter out = response.getWriter();
		String result = null;
		try {
			WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encodingAESKey,
					corpid);
			result = wxcpt.VerifyURL(msg_signature, timestamp, nonce, echostr);
		} catch (AesException e) {
			// 验证URL失败，错误原因请查看异常
			e.printStackTrace();
		}
		if (result == null) {
			result = token;
		}
		out.print(result);
		out.close();
		out = null;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO 消息的接收、处理、响应
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/xml");
		request.setCharacterEncoding("UTF-8");

		String encodingAESKey = null;
		String token = null;
		String corpId = null;
		String agentTuid = null;
		String secret = null;
		DataSource _ds = null;
		Connection con = null;

		Map<String, String> requestMap = null;
		String fromUserName = null;
		// 公众帐号
		String toUserName = null;
		String msgSignature = request.getParameter("msg_signature");
		String timeStamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		// 消息类型
		String msgType = null;
		WXBizMsgCrypt wb = null;
		PrintWriter out = response.getWriter();
		try {
			String _dataSource = getServletContext().getInitParameter(
					"def-datasource");
			String _jndiPrefix = getServletContext().getInitParameter(
					"jndi-prefix");
			String jndiName = _jndiPrefix + _dataSource;

			// 获取token，encodingAESKey
			agentTuid = request.getParameter("agentid");
			_ds = Jndi.getDataSource(jndiName);
			con = _ds.getConnection();
			Db db = new Db(con);
			Recordset queryCorpRecordset = db
					.get(StringUtil.replace(
							WeixinUtil
									.getLocalResource("/transactions/project/weixin/corp/query-app.sql"),
							"${tuid}", agentTuid));
			if (queryCorpRecordset.getRecordCount() > 0) {
				queryCorpRecordset.first();
				token = queryCorpRecordset.getString("callback_token");
				encodingAESKey = queryCorpRecordset
						.getString("encoding_aeskey");
				secret = queryCorpRecordset.getString("secret");
				String welcomeMessage = queryCorpRecordset
						.getString("welcome_message");

				String postString = WeixinUtil.getPostString(request);
				// 解密微信服务器post字符串
				corpId = request.getParameter("cid");
				wb = new WXBizMsgCrypt(token, encodingAESKey, corpId);
				String decryptMsg = wb.DecryptMsg(msgSignature, timeStamp,
						nonce, postString);

				requestMap = WeixinUtil.parseXml(decryptMsg);
				if (null == agentTuid || null == corpId) {
					throw new Exception("企业号认证失败，corpid 和 agentid不能为空");
				}

				msgType = requestMap.get("MsgType");
				if ("event".equals(msgType)) {
					// 发送方帐号（open_id）
					fromUserName = requestMap.get("FromUserName");
					// 公众帐号
					String eventType = requestMap.get("Event");
					String agentid = requestMap.get("AgentID");
					if ("LOCATION".equals(eventType)) {
						toUserName = requestMap.get("ToUserName");
						// String createTime = requestMap.get("CreateTime");
						String latitude = requestMap.get("Latitude");
						String longitude = requestMap.get("Longitude");
						// String precision = requestMap.get("Precision");
						db.exec("insert into wx_user_location values(nextval('seq_wx_user_location'),'"
								+ fromUserName
								+ "','"
								+ latitude
								+ "','"
								+ longitude + "','" + toUserName + "',now())");
					} else if ("enter_agent".equals(eventType)) {

						if (!"".equals(welcomeMessage)
								&& null != welcomeMessage) {
							String corpTuid = queryCorpRecordset
									.getString("tuid");
							String accessToken = WeixinUtil
									.getAccessTokenForCorp(db, corpTuid,
											corpId, secret);
							String appNameString = queryCorpRecordset
									.getString("app_name");
							welcomeMessage = StringUtil.replace(welcomeMessage,
									"${agnetName}", appNameString);
							WeixinUtil.sendMessageTextForCorp(accessToken,
									welcomeMessage, fromUserName, "", "",
									agentid);
						}
					}
				}
			}
			out.print("");
			out.close();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
