package transactions.project.weixin.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;

import dinamica.Base64;
import dinamica.Config;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Jndi;
import dinamica.Recordset;
import dinamica.StringUtil;
import transactions.project.util.Tools;
import transactions.project.weixin.common.WeixinUtil;
import transactions.project.weixin.common.XMLParse;
import transactions.project.weixin.corp.WXBizMsgCrypt;

import com.ccms.context.InitializerServlet;

/**
 * Servlet implementation class WebInterface
 */
public class ServiceController extends HttpServlet {
	private static final long serialVersionUID = 4440739483644821986L;

	/**
	 * Default constructor.
	 */
	public ServiceController() {

	}

	/**
	 * 确认请求来自微信服务器
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 微信加密签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");
		PrintWriter out = response.getWriter();
		
		String _dataSource = getServletContext().getInitParameter("def-datasource");
		String _jndiPrefix = getServletContext().getInitParameter("jndi-prefix");
		String jndiName = _jndiPrefix + _dataSource;
		DataSource _ds = null;
		Connection con = null;
		String token=null;
		try {
			String service_tuid=request.getParameter("sid");
			if(null==service_tuid){
				throw new Throwable("接入失败。Service ID不能为空");
			}
			_ds = Jndi.getDataSource(jndiName);
			con = _ds.getConnection();
			Db db = new Db(con);
			
			Recordset queryTokenRecordset = db.get(StringUtil.replace(
					"select token from wx_service where tuid=${tuid}", "${tuid}", service_tuid));
			queryTokenRecordset.first();
			token = queryTokenRecordset.getString("token");
			// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
			if(null==token||"".equals(token)){
				throw new Throwable("access token 不能为空");
			}
			if(WeixinUtil.checkSignature(token, signature, timestamp, nonce)){
				out.print(echostr);
			}
			out.close();
			out = null;
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
		
		
	}

	/**
	 * 处理微信服务器发来的消息
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 微信加密签名
		String msg_signature = null;
		// 时间戳
		String timestamp = null;
		// 随机数
		String nonce = null;
		String service_tuid=null;
		String msg_encrypt=null;
		String msg_decrypt=null;
		String encoding_aeskey=null;
		WXBizMsgCrypt pc=null;
		
		
		String _dataSource = getServletContext().getInitParameter("def-datasource");
		String _jndiPrefix = getServletContext().getInitParameter("jndi-prefix");
		String jndiName = _jndiPrefix + _dataSource;
		DataSource _ds = null;
		Connection con = null;
		try {
			_ds = Jndi.getDataSource(jndiName);
			con = _ds.getConnection();
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	
		Db db = new Db(con);
		
		response.setCharacterEncoding("UTF-8");  
	    response.setContentType("application/xml");
	    request.setCharacterEncoding("UTF-8");  
	    
	    Map<String, String> requestMap = null;
	    String fromUserName = null;
		// 公众帐号
		String toUserName =null;
		// 消息类型
		String msgType =null ;
		String msgId =null;
	    PrintWriter out = response.getWriter();  
	    String respContent="";
	    int rc=0;
	    try {

			
	    	String postString=WeixinUtil.getPostString(request);
			requestMap = WeixinUtil.parseXml(postString);
			msg_encrypt=requestMap.get("Encrypt");
			if(!"".equals(msg_encrypt)&&null!=msg_encrypt){
				 msg_signature = request.getParameter("msg_signature");
				// 时间戳
				timestamp = request.getParameter("timestamp");
				// 随机数
				nonce = request.getParameter("nonce");
				service_tuid=request.getParameter("sid");

				
				Recordset queryTokenRecordset = db.get(StringUtil.replace(
						"select token,encoding_aeskey,appid from wx_service where tuid=${tuid}", "${tuid}", service_tuid));
				queryTokenRecordset.first();
				String token = queryTokenRecordset.getString("token");
				String appId=queryTokenRecordset.getString("appid");
				encoding_aeskey=queryTokenRecordset.getString("encoding_aeskey");
				// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
				if(null==token||"".equals(token)){
					throw new Throwable("access token 不能为空");
				}
				
				if(!WeixinUtil.checkSignatureForMessage(token, msg_signature, timestamp, nonce, msg_encrypt)){
					throw new Throwable("消息体签名验证失败");
				}
				pc = new WXBizMsgCrypt(token, encoding_aeskey, appId);
				msg_decrypt= pc.decrypt(msg_encrypt);
				requestMap = WeixinUtil.parseXml(msg_decrypt);
			}
			
			// 发送方帐号（open_id）
			 fromUserName = requestMap.get("FromUserName");
			// 公众帐号
			 toUserName = requestMap.get("ToUserName");
			 msgType = requestMap.get("MsgType");
			 msgId =  requestMap.get("MsgId");
			 String eventTypeString= (requestMap.containsKey("Event") ? requestMap.get("Event") : null);
			 //开通了上报地理位置接口的公众号，用户在关注后进入公众号会话时，会弹框让用户确认是否允许公众号使用其地理位置。弹框只在关注后出现一次，用户以后可以在公众号详情页面进行操作。
			 if (null != eventTypeString && eventTypeString.equals("LOCATION")) {
				String Latitude=requestMap.get("Latitude");
				String Longitude=requestMap.get("Longitude");
				db.exec("update hr_staff_weixin set latitude='"+Latitude+"',longitude='"+Longitude+"' where  weixin_userid='"+fromUserName+"'");
			}
			 String receiveContent = requestMap.get("Content");
			 if(msgType.equalsIgnoreCase("text")){	//文字的内容
				 if(receiveContent.indexOf("#")>0){
					 if(receiveContent.endsWith("#")){
						 receiveContent = receiveContent +" # # # # # # # # # # ";	//补齐#号，防止程序出错
					 }else{
						 receiveContent = receiveContent +"# # # # # # # # # # ";	//补齐#号，防止程序出错
					 }
					 
					 String sContent[] = receiveContent.split("#");
					 
					 Recordset inputParams=new Recordset();
					 inputParams.append("userlogin", Types.VARCHAR);
					 inputParams.append("passwd", Types.VARCHAR);
					 inputParams.append("name", Types.VARCHAR);
					 inputParams.append("mobile", Types.VARCHAR);
					 inputParams.append("weixin_userid", Types.VARCHAR);
					 inputParams.append("weixin_id", Types.VARCHAR);
					 inputParams.append("weixin_msgid", Types.VARCHAR);
					 
					 inputParams.addNew();
					 inputParams.setValue("userlogin", sContent[1]);
					 inputParams.setValue("passwd", sContent[2]);
					 inputParams.setValue("name", sContent[3]==null?sContent[1]:sContent[3]);
					 inputParams.setValue("mobile", sContent[1]);
					 inputParams.setValue("weixin_userid", fromUserName);
					 inputParams.setValue("weixin_id", toUserName);
					 inputParams.setValue("weixin_msgid", msgId);
					 //密码加密
					 if(sContent[2] != null && sContent[2].length() > 0){
							//retrieve values
							String password = sContent[2];
							String userid = sContent[1];
							
							//create MD5 hash using the string: userlogin:passwd
							java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
							byte[] b = (userid + ":" + password).getBytes();
							byte[] hash = md.digest(b);
							String pwd = Base64.encodeToString( hash, true );

							//set the "passwd" parameter value to the MD5 hash 
							inputParams.setValue("passwd", pwd);
					 }

					 
					 String validatorClassName = "";
					 String path = "";
					 if(sContent[0].equals("A")){
						 	validatorClassName = "transactions.project.weixin.service.binding.WeixinBinding";
				        	path = "/WEB-INF/classes/transactions/project/weixin/service/binding/";
					 }else if(sContent[0].equals("U")){
						 	validatorClassName = "transactions.project.weixin.service.unbind.WeixinUnbind";
				        	path = "/WEB-INF/classes/transactions/project/weixin/service/unbind/";
					 }else {
						 validatorClassName = "";
					}
					 /*switch (sContent[0]){
					 	case "0":
					 	case "a":
					 	case "A":
					        validatorClassName = "transactions.project.weixin.service.binding.WeixinBinding";
				        	path = "/WEB-INF/classes/transactions/project/weixin/service/binding/";
					        break;
					 	case "u":
					 	case "U":
					        validatorClassName = "transactions.project.weixin.service.unbind.WeixinUnbind";
				        	path = "/WEB-INF/classes/transactions/project/weixin/service/unbind/";
					        break;
					 	default:
					 		validatorClassName = "";
					        break;
					 }*/
			        if(validatorClassName!=null && validatorClassName.length() > 0){
			        	Config config = null;
			        	String path_home = "/WEB-INF/action/ccms/home/";
			        	
			        	String configData = StringUtil.getResource(InitializerServlet.getContext(), path_home + "config.xml");
			        	
			        	config = new Config(configData, path);
			        	
			    	    ClassLoader loader = Thread.currentThread().getContextClassLoader();
			    		GenericTransaction t = (GenericTransaction) loader.loadClass(validatorClassName).newInstance();
						t.init(this.getServletContext(), request, response);
						t.setConfig(config);
						t.setConnection(con);
			            rc=t.service(inputParams);
			           
			            
				    }
				}else{	// 接收普通文本消息
					String msgContent = requestMap.get("Content");
					msgContent = StringUtils.replace(msgContent, "'", "''");
					Integer createTime = null;
					if( requestMap.containsKey("CreateTime") && null != requestMap.get("CreateTime") ){
						createTime = Integer.parseInt(requestMap.get("CreateTime"));
					}
					// 存入微信消息记录表
					this.addChatRecord(db, fromUserName, 0, msgType, msgContent, createTime, msgId, null, request);
					// 自动回复处理
					String queryTemplate = "SELECT tuid, key_word, reply_content, remark FROM wx_chat_autoreply_template WHERE '" + msgContent + "' like concat('%',key_word,'%') AND is_deleted = 0";
					Recordset rsTemplate = db.get(queryTemplate);
					if( null != rsTemplate && rsTemplate.getRecordCount() > 0 ){
						rsTemplate.first();
						String replycontent = rsTemplate.getString("reply_content");
						if( StringUtils.isNotBlank(replycontent) ){
							respContent = XMLParse.generatePassiveText(fromUserName , toUserName, WeixinUtil.create_timestamp(), replycontent);
							this.addChatRecord(db, fromUserName, 1, msgType, replycontent, null, "", rsTemplate.getInt("tuid"), request);
						}
					}
				}
				if(rc==1){
					respContent=XMLParse.generatePassiveText(fromUserName , toUserName, WeixinUtil.create_timestamp(), "新增微信绑定成功!");
			    }else if(rc==2){
			    	respContent=XMLParse.generatePassiveText(fromUserName, toUserName, WeixinUtil.create_timestamp(), "更改微信绑定成功!");
			    }else if(rc==3){
			    	respContent=XMLParse.generatePassiveText(fromUserName, toUserName, WeixinUtil.create_timestamp(), "更改微信绑定成功!");
			    }else if(rc==4){
			    	respContent=XMLParse.generatePassiveText(fromUserName, toUserName, WeixinUtil.create_timestamp(), "帐号不存在，或者密码不正确!");
			    }else if(rc==5){
			    	respContent=XMLParse.generatePassiveText(fromUserName, toUserName, WeixinUtil.create_timestamp(), "解除微信绑定成功!");
			    }
			}else if( msgType.equalsIgnoreCase("event") ) {
				if( "subscribe".equalsIgnoreCase(eventTypeString) ){	// 订阅(关注)事件
					String queryTemplate = "SELECT tuid, key_word, reply_content, remark FROM wx_chat_autoreply_template WHERE key_word = 'subscribe' ORDER BY tuid";
					Recordset rsTemplate = db.get(queryTemplate);
					if( null != rsTemplate && rsTemplate.getRecordCount() > 0 ){
						rsTemplate.first();
						String replycontent = rsTemplate.getString("reply_content");
						if( StringUtils.isNotBlank(replycontent) ){
							respContent = XMLParse.generatePassiveText(fromUserName , toUserName, WeixinUtil.create_timestamp(), replycontent);
						}
					}
				}
				if( requestMap.containsKey("Ticket") ) {	// 二维码的ticket，可用来换取二维码图片
					String eventKey= (requestMap.containsKey("EventKey") ? requestMap.get("EventKey") : null);
					// 判断当前是否为扫描二维码
					boolean isScanQrcode = false;
					if( eventTypeString.equalsIgnoreCase("subscribe") &&  null != eventKey && eventKey.startsWith("qrscene_") ){
						isScanQrcode = true;
						eventKey = eventKey.substring("qrscene_".length()-1);
					}else if( eventTypeString.equalsIgnoreCase("scan") ){
						isScanQrcode = true;
					}
					if( isScanQrcode ){
						// 保存二维码扫描结果
						//saveQrcodeScanid(db, eventKey, fromUserName, request);
					}
				}
			}
			if(!"".equals(msg_encrypt)&&null!=msg_encrypt){
				respContent=pc.EncryptMsg(respContent, timestamp, nonce);
			}
			out.print(respContent);
			out.close();
	    } catch (Throwable e) { 
	    	out.print("");
			out.close();	
			e.printStackTrace();
	    } finally {   
	        if(out!=null) {   
	            out.flush();  
	            out.close();  
	        }  
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	    }
	}
	
	/*** 保存二维码扫描结果 */
	@SuppressWarnings("unused")
	private void saveQrcodeScanid(Db db, String eventKey, String fromUserName, HttpServletRequest request) throws Throwable{
		Integer scan_id = null;	// 二维码参数值：场景id
		try{
			scan_id = Integer.parseInt(eventKey);
		}catch(NumberFormatException e){
		}
		// 获取原二维码记录
		String queryCode = "SELECT tuid AS scan_id, * FROM wx_qrcode WHERE tuid = " + scan_id;
		Recordset rsCode = db.get(queryCode);
		while( rsCode.next() ){
			// 根据微信号将user_id回写至二维码扫描记录表中
			String insert = "INSERT INTO wx_qrcode_scan_log(tuid, wxlogin, scene_id, scene_str, status, created, org_id) "
					+ " VALUES(${seq:nextval@seq_wx_qrcode_scan_log}, '" + fromUserName + "', " + scan_id + ", NULL, 1, '" + Tools.convertDateToStr(new Date(),"yyyy-MM-dd HH:mm:ss") + "', ${fld:org_id})";
			insert = transactions.project.util.ServiceTools.getSQL(insert, rsCode, request);
			db.exec(insert);
			
			// 根据业务数据类型处理
			Integer datatype = rsCode.getInteger("data_type");	// 业务数据类型：0会员卡 1私教
			if( null != datatype && 0 == datatype.intValue() ){	// 会员卡
				// 入场刷卡
				/**ClassLoader loader = Thread.currentThread().getContextClassLoader();
				Inleft inleft = (Inleft) loader.loadClass("transactions.project.fitness.weixin.Inleft").newInstance();
				inleft.setDb(db);
				inleft.setScaneid(scan_id);
				int resultcode = inleft.service(new Recordset());
				String resultmsg = inleft.getResultmsg();*/
			}else if( null != datatype && 1 == datatype.intValue() ){	// 私教
				// 私教刷课
				/**ClassLoader loader = Thread.currentThread().getContextClassLoader();
				PTClass ptclass = (PTClass) loader.loadClass("transactions.project.fitness.weixin.PTClass").newInstance();
				ptclass.setDb(db);
				ptclass.setScaneid(scan_id);
				int resultcode = ptclass.service(new Recordset());
				String resultmsg = ptclass.getResultmsg();*/
			}
		}
	}
	
	/*** 数据存入表wx_chat_record */
	private void addChatRecord(Db db, String fromUserName, Integer record_type, String msgType, String content, Integer createTime, String msgId, Integer templateId, 
			HttpServletRequest request) throws Throwable{
		String insert = "INSERT INTO wx_chat_record(tuid, touser_wxlogin, record_type, msgtype, content, createtime, msgid, createdby, created, autoreply_template_id) " 
				+ "VALUES(${seq:nextval@seq_wx_chat_record}, '" + fromUserName + "', " + record_type + ", '" + msgType + "', '" + content + "', " + createTime + ", '" + msgId + "', 'sys', '" + Tools.convertDateToStr(new Date(),"yyyy-MM-dd HH:mm:ss") + "', " + templateId + ");";
		insert = transactions.project.util.ServiceTools.getSQL(insert, null, request);
		db.exec(insert);
	}
}

