package transactions.project.weixin.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import transactions.project.util.Tools;
import transactions.project.weixin.service.pay.util.MD5Util;
import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

public class WeixinUtil {
	// 企业号
	public static final String WX_ACCESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";
	public static final String WX_USER_ID_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo";
	public static final String WX_MENU_CREATE_URL = "https://qyapi.weixin.qq.com/cgi-bin/menu/create?access_token=";
	public static final String WX_ORG_CREATE_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/create?access_token=";
	public static final String WX_ORG_UPDATE_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/update?access_token=";
	public static final String WX_ORG_DELETE_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/delete?access_token=${access_token}"
			+ "&id=${id}";
	public static final String WX_ORG_LIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/department/list?access_token=";
	public static final String WX_USER_CREATE_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=";
	public static final String WX_USER_UPDATE_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/update?access_token=";
	public static final String WX_USER_DELETE_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/delete?access_token=${access_token}&userid=${userid}";
	public static final String WX_USER_LIST_URL = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist?access_token=${access_token}&department_id=${department_id}&fetch_child=0&status=0";
	public static final String WX_SEND_MESSAGE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=${access_token}";

	// 服务号
	public final static String SER_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
	public static final String SER_USER_ID_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
	public final static String SER_MENU_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=";
	public final static String SER_SEND_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";
	public static final String SER_USER_LIST_URL = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=";
	public static final String SER_USER_DETAIL_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

	// 微信支付、
	public static final String WX_JS_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=${access_token}&type=jsapi";
	public static final String WX_PAY_UNIFIEDORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	public static final String WX_PAY_ORDER_QUERY="https://api.mch.weixin.qq.com/pay/orderquery";
	public static final String WX_PAY_DELIVER_NOTIFY="https://api.weixin.qq.com/pay/delivernotify?access_token=";
	
	// 二维码
	public static final String QRCODE_CREATE_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
	public static final String QRCODE_SHOW_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
	
	// 多媒体文件下载
	public static final String GET_MEDIA_URL = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=${access_token}&media_id=";
	
	/*
	 * 企业号获取当前token
	 * 
	 * db app_id 企业应用在数据库的主键 corpid corpsecret
	 */
	public static String getAccessTokenForCorp(Db db, String corpTuid,
			String corpId, String corpsecret) throws Throwable {
		String updateTokenSql = getLocalResource("/transactions/project/weixin/corp/update-token.sql");
		updateTokenSql = StringUtil.replace(updateTokenSql, "${fld:corp_tuid}",
				corpTuid);
		String getTokenSql = getLocalResource("/transactions/project/weixin/corp/query-token.sql");
		getTokenSql = StringUtil.replace(getTokenSql, "${fld:corp_tuid}",
				corpTuid);
		Recordset tokenRecordset = db.get(getTokenSql);
		tokenRecordset.first();
		if (tokenRecordset.getString("access_token") == null) {
			String new_access_token = getWeixinAccessTokenForCorp(corpId,
					corpsecret);

			updateTokenSql = StringUtil.replace(updateTokenSql,
					"${access_token}", new_access_token);
			db.exec(updateTokenSql);
			return new_access_token;
		} else {
			tokenRecordset.first();
			String oldAccessToken = tokenRecordset.getString("access_token");
			java.util.Date start = tokenRecordset.getDate("addtime");
			String expiresIb = tokenRecordset.getString("expires_ib");
			java.util.Date end = new java.util.Date();
			if (end.getTime() - start.getTime() > Integer.parseInt(expiresIb) * 1000) {
				String new_access_token = getWeixinAccessTokenForCorp(corpId,
						corpsecret);
				updateTokenSql = StringUtil.replace(updateTokenSql,
						"${access_token}", new_access_token);
				db.exec(updateTokenSql);
				return new_access_token;
			} else {
				return oldAccessToken;
			}
		}
	}

	/*
	 * 企业号从微信服务器获取最新的AccessToken
	 * 
	 * corpid 企业id corpsecret 获取token的秘钥
	 */
	public static String getWeixinAccessTokenForCorp(String corpId,
			String corpSecret) throws Exception {
		String newAccessToken = null;
		try {
			String access_token_url = WX_ACCESS_TOKEN_URL + "?corpid=" + corpId
					+ "&corpsecret=" + corpSecret;
			JSONObject jsonObject = (JSONObject) httpRequest(access_token_url,
					"GET", null, null);
			newAccessToken = (String) jsonObject.get("access_token");
		} catch (Throwable e) {
			throw new Exception("WeixinUtil 获取AccessToken失败");
		}
		return newAccessToken;
	}

	/*
	 * 企业号创建菜单
	 * 
	 * postString 菜单拼接jason字符串 accessToken agentid 应用id
	 */
	public static void createMenuForCopr(String postString, String accessToken,
			String agentId) throws Throwable {
		if (null == accessToken || "".equals(accessToken)) {
			throw new Throwable("accessToken不能为空");
		}
		if (null == agentId || "".equals(agentId)) {
			throw new Throwable("agentid不能为空");
		}
		if (null == postString || "".equals(postString)) {
			throw new Throwable("postString不能为空");
		}

		String menu_create_url = WX_MENU_CREATE_URL + accessToken + "&agentid="
				+ agentId;
		JSONObject jsonObject = (JSONObject) httpRequest(menu_create_url,
				"POST", postString, null);
		int errcode = jsonObject.getInt("errcode");
		if (0 != errcode) {
			throw new Throwable(jsonObject.toString());
		}
	}

	/*
	 * 企业号OAuth2.0网页授权,获取用户ID
	 * 
	 * access_token code 微信回调code agentid 应用ID
	 */
	public static String getWeixinUserIdForCorp(String accessToken,
			String code, String agentId) throws Throwable {
		if (null == accessToken || "".equals(accessToken)) {
			throw new Throwable("accessToken不能为空");
		}
		if (null == code || "".equals(code)) {
			throw new Throwable("code不能为空");
		}
		if (null == agentId || "".equals(agentId)) {
			throw new Throwable("agentid不能为空");
		}
		String user_id_url = WX_USER_ID_URL + "?access_token=" + accessToken
				+ "&code=" + code + "&agentid=" + agentId;
		JSONObject jsonObject = (JSONObject) httpRequest(user_id_url, "GET",
				null, null);

		String UserId = (String) jsonObject.get("UserId");
		return UserId;
	}

	public static String mapToJsonString(Map map) throws JSONException {
		Iterator iterator = map.keySet().iterator();
		;
		JSONObject jsonObject = null;
		while (iterator.hasNext()) {
			if (null == jsonObject) {
				jsonObject = new JSONObject();
			}
			String key = (String) iterator.next();
			Object value = map.get(key);
			jsonObject.put(key, value);
		}
		return jsonObject.toString();
	}

	/*
	 * 企业号 创建部门
	 */
	public static int createOrgForCorp(String accessToken, Map map)
			throws Throwable {
		String postString = WeixinUtil.mapToJsonString(map);
		if (null == accessToken || "".equals(accessToken)) {
			throw new Throwable("access_token不能为空");
		}
		if (null == postString || "".equals(postString)) {
			throw new Throwable("postString不能为空");
		}
		String create_org_url = WX_ORG_CREATE_URL + accessToken;
		int orgId;
		JSONObject jsonObject = (JSONObject) httpRequest(create_org_url,
				"POST", postString, null);
		int errcode = jsonObject.getInt("errcode");
		if (0 == errcode) {
			orgId = jsonObject.getInt("id");
		} else {
			throw new Throwable(jsonObject.toString());
		}
		return orgId;
	}

	/*
	 * 企业号 更新部门
	 */
	public static int updateOrgForCorp(String accessToken, Map map)
			throws Throwable {
		String postString = WeixinUtil.mapToJsonString(map);
		if (null == accessToken || "".equals(accessToken)) {
			throw new Throwable("access_token不能为空");
		}
		if (null == postString || "".equals(postString)) {
			throw new Throwable("postString不能为空");
		}
		String update_org_url = WX_ORG_UPDATE_URL + accessToken;

		JSONObject jsonObject = (JSONObject) httpRequest(update_org_url,
				"POST", postString, null);
		int errcode = jsonObject.getInt("errcode");
		if (0 == errcode) {
			return errcode;
		} else {
			throw new Throwable(jsonObject.toString());
		}
	}

	/*
	 * 企业号 删除部门
	 */
	public static int deleteOrgForCorp(String accessToken, String id)
			throws Throwable {
		if (null == accessToken || "".equals(accessToken)) {
			throw new Throwable("access_token不能为空");
		}
		if (null == id || "".equals(id)) {
			throw new Throwable("id不能为空");
		}
		String delete_org_url = StringUtil
				.replace(StringUtil.replace(WX_ORG_DELETE_URL,
						"${access_token}", accessToken), "${id}", id);
		JSONObject jsonObject = (JSONObject) httpRequest(delete_org_url, "GET",
				null, null);
		int errcode = jsonObject.getInt("errcode");
		if (0 == errcode) {
			return errcode;
		} else {
			throw new Throwable(jsonObject.toString());
		}
	}

	/*
	 * 企业号 获取部门列表
	 */
	public static JSONArray getOrgListForCorp(String accessToken)
			throws Throwable {
		if (null == accessToken || "".equals(accessToken)) {
			throw new Throwable("access_token不能为空");
		}
		String get_org_url = WX_ORG_LIST_URL + accessToken;
		JSONObject jsonObject = (JSONObject) httpRequest(get_org_url, "GET",
				null, null);
		int errcode = jsonObject.getInt("errcode");
		if (0 == errcode) {
			return jsonObject.getJSONArray("department");
		} else {
			throw new Throwable(jsonObject.toString());
		}
	}

	/*
	 * 企业号 创建成员
	 */
	public static int createUserForCorp(String accessToken, Map map)
			throws Throwable {
		String postString = WeixinUtil.mapToJsonString(map);
		if (null == accessToken || "".equals(accessToken)) {
			throw new Throwable("access_token不能为空");
		}
		if (null == postString || "".equals(postString)) {
			throw new Throwable("postString不能为空");
		}
		String create_user_url = WX_USER_CREATE_URL + accessToken;
		JSONObject jsonObject = (JSONObject) httpRequest(create_user_url,
				"POST", postString, null);
		int errcode = jsonObject.getInt("errcode");
		if (0 == errcode) {
			return errcode;
		} else if (60102 == errcode) {
			return -1;
		} else {
			throw new Throwable(jsonObject.toString());
		}
	}

	/*
	 * 企业号 更新成员
	 */
	public static int updateUserForCorp(String accessToken, Map map)
			throws Throwable {
		String postString = WeixinUtil.mapToJsonString(map);
		if (null == accessToken || "".equals(accessToken)) {
			throw new Throwable("access_token不能为空");
		}
		if (null == postString || "".equals(postString)) {
			throw new Throwable("postString不能为空");
		}
		String update_user_url = WX_USER_UPDATE_URL + accessToken;

		JSONObject jsonObject = (JSONObject) httpRequest(update_user_url,
				"POST", postString, null);
		int errcode = jsonObject.getInt("errcode");
		if (0 == errcode) {
			return errcode;
		} else if (0 == 60111) {
			return -1;
		} else {
			throw new Throwable(jsonObject.toString());
		}
	}

	/*
	 * 企业号 删除成员
	 */
	public static int deleteUserForCorp(String accessToken, String userid)
			throws Throwable {
		if (null == accessToken || "".equals(accessToken)) {
			throw new Throwable("access_token不能为空");
		}
		if (null == userid || "".equals(userid)) {
			throw new Throwable("userid不能为空");
		}
		String delete_user_url = StringUtil.replace(StringUtil.replace(
				WX_USER_DELETE_URL, "${access_token}", accessToken),
				"${userid}", userid);
		JSONObject jsonObject = (JSONObject) httpRequest(delete_user_url,
				"GET", null, null);
		int errcode = jsonObject.getInt("errcode");
		if (0 == errcode) {
			return errcode;
		} else {
			throw new Throwable(jsonObject.toString());
		}
	}

	/*
	 * 企业号 获取用户列表
	 */
	public static JSONArray getUserListForCorp(String accessToken,
			String departmentId) throws Throwable {
		if (null == accessToken || "".equals(accessToken)) {
			throw new Throwable("access_token不能为空");
		}
		if (null == departmentId || "".equals(departmentId)) {
			throw new Throwable("department_id不能为空");
		}
		String get_user_list_url = StringUtil.replace(StringUtil.replace(
				WX_USER_LIST_URL, "${access_token}", accessToken),
				"${department_id}", departmentId);
		JSONObject jsonObject = (JSONObject) httpRequest(get_user_list_url,
				"GET", null, null);
		int errcode = jsonObject.getInt("errcode");
		if (0 == errcode) {
			return jsonObject.getJSONArray("userlist");
		} else {
			throw new Throwable(jsonObject.toString());
		}
	}

	// 企业号发送信息
	public static JSONObject sendMessageTextForCorp(String accessToken,
			String message, String touser, String toparty, String totag,
			String agentid) throws Throwable {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("touser", touser);
		jsonObject.put("toparty", toparty);
		jsonObject.put("totag", totag);
		jsonObject.put("agentid", agentid);
		jsonObject.put("safe", "0");
		jsonObject.put("msgtype", "text");
		jsonObject.put("text", new JSONObject().put("content", message));
		String postString = jsonObject.toString();
		String send_messag_url = StringUtil.replace(WX_SEND_MESSAGE_URL,
				"${access_token}", accessToken);
		return (JSONObject) httpRequest(send_messag_url, "POST", postString,
				null);
	}

	/*
	 * 企业号 同步组织架构
	 */
	public static void publishOrgList(Db db, String tenantryId,
			String accessToken, String pid, String weixinOrgPid)
			throws Throwable {
		if (null == db) {
			throw new Throwable("数据库连接失败");
		}
		if (null == tenantryId) {
			throw new Throwable("tenantryId不能为空");
		}
		if (null == accessToken) {
			throw new Throwable("accessToken不能为空");
		}
		if (null == pid) {
			throw new Throwable("pid不能为空");
		}
		if (null == weixinOrgPid) {
			throw new Throwable("weixinOrgPid 不能为空");
		}
		String getOrgListSql = getLocalResource("/transactions/project/weixin/common/query-org.sql");
		String getUserSql = getLocalResource("/transactions/project/weixin/common/query-user.sql");
		String updateOrgCodeSql = getLocalResource("/transactions/project/weixin/common/update-org.sql");
		Recordset topRecordset = db.get(StringUtil.replace(
				StringUtil.replace(getOrgListSql, "${pid}", pid),
				"${tenantryId}", tenantryId));
		if (topRecordset.getRecordCount() > 0) {
			topRecordset.top();
			Map map = null;
			while (topRecordset.next()) {
				String orgId = topRecordset.getString("org_id");
				String orgCode = topRecordset.getString("org_code");
				String orgName = topRecordset.getString("org_name");
				String showOrder = topRecordset.getString("show_order");
				String postString = null;
				map = new HashMap();
				if (null == orgCode || "".equals(orgCode)) {
					map.put("name", orgName);
					map.put("parentid", weixinOrgPid);
					map.put("order",
							null == showOrder || "".equals(showOrder) ? "1"
									: showOrder);

					weixinOrgPid = String.valueOf(WeixinUtil.createOrgForCorp(
							accessToken, map));
					db.exec(StringUtil.replace(StringUtil.replace(
							updateOrgCodeSql, "${orgCode}", weixinOrgPid),
							"${orgId}", orgId));
					if (null != getUserSql && !"".equals(getUserSql)) {
						publisUserForCorp(db, Integer.valueOf(orgId),
								Integer.valueOf(weixinOrgPid), getUserSql,
								accessToken);
					}

				} else {
					map.put("id", Integer.parseInt(orgCode));
					map.put("name", orgName);
					map.put("parentid", weixinOrgPid);
					map.put("order", showOrder);
					weixinOrgPid = orgCode;
					WeixinUtil.updateOrgForCorp(accessToken, map);
					if (null != getUserSql && !"".equals(getUserSql)) {
						publisUserForCorp(db, Integer.valueOf(orgId),
								Integer.valueOf(weixinOrgPid), getUserSql,
								accessToken);
					}
				}
				publishOrgList(db, tenantryId, accessToken, orgId, weixinOrgPid);
			}
		}
	}

	// 企业号 同步成员
	public static void publisUserForCorp(Db db, int orgId, int weixinOrgCode,
			String getUserSql, String accessToken) throws Throwable {
		String updateUserSysSql = getLocalResource("/transactions/project/weixin/common/update-user.sql");
		// 微信端当前部门拥有的人
		JSONArray jsonArray = WeixinUtil.getUserListForCorp(accessToken,
				String.valueOf(weixinOrgCode));

		Recordset getUserRecordset = db.get(StringUtil.replace(getUserSql,
				"${orgId}", String.valueOf(orgId)));
		if (getUserRecordset.getRecordCount() > 0) {
			getUserRecordset.top();
			Map map = null;
			while (getUserRecordset.next()) {
				String userlogin = getUserRecordset.getString("userlogin");
				String name = getUserRecordset.getString("name");
				int[] department = { weixinOrgCode };
				// String position=null;
				String mobile = getUserRecordset.getString("mobile");
				String email = getUserRecordset.getString("email");
				String weixinid = getUserRecordset.getString("weixin_userid");
				// String extattr=null;
				String isWeixinSys = getUserRecordset
						.getString("is_weixin_sys");
				map = new HashMap();
				map.put("userid", userlogin);
				map.put("name", name);
				map.put("department", department);
				// jsonObject.put("position", position);
				map.put("mobile", mobile);
				map.put("email", email);
				map.put("weixinid", weixinid);
				// jsonObject.put("extattr", extattr);
				if (null == isWeixinSys || "".equals(isWeixinSys)) {
					WeixinUtil.createUserForCorp(accessToken, map);
					db.exec(StringUtil.replace(updateUserSysSql,
							"${userlogin}", userlogin));
				} else {
					WeixinUtil.updateUserForCorp(accessToken, map);
				}
				// 应用端与微信端表，如果应用端不存在，则删除微信端成员
				for (int j = 0; j < jsonArray.length(); j++) {
					JSONObject jsonObject = (JSONObject) jsonArray.get(j);
					String userid = jsonObject.getString("userid");
					if (userid.equals(userlogin)) {
						jsonObject.append("is_delete", "0");
						break;
					}
				}
			}
		}
		for (int j = 0; j < jsonArray.length(); j++) {
			JSONObject jsonObject = (JSONObject) jsonArray.get(j);
			String userid = jsonObject.getString("userid");
			String isDelete = null;
			try {
				isDelete = jsonObject.getString("is_delete");
			} catch (Exception e) {

			}
			if (null == isDelete || "".equals(isDelete)) {
				WeixinUtil.deleteUserForCorp(accessToken, userid);
			}
		}
	}

	/* ************************************************************************
	 * 服务号接入校验
	 */
	public static boolean checkSignature(String token, String signature,
			String timestamp, String nonce) {
		String[] arr = new String[] { token, timestamp, nonce };
		// 将token、timestamp、nonce三个参数进行字典序排序
		Arrays.sort(arr);
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			content.append(arr[i]);
		}
		MessageDigest md = null;
		String tmpStr = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
			// 将三个参数字符串拼接成一个字符串进行sha1加密
			byte[] digest = md.digest(content.toString().getBytes());
			tmpStr = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		content = null;
		// 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
		return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
	}
	/* ************************************************************************
	 * 服务号接入校验
	 */
	public static boolean checkSignatureForMessage(String token, String signature,
			String timestamp, String nonce,String msg_encrypt) {
		String[] arr = new String[] { token, timestamp, nonce, msg_encrypt};
		// 将token、timestamp、nonce三个参数进行字典序排序
		Arrays.sort(arr);
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			content.append(arr[i]);
		}
		MessageDigest md = null;
		String tmpStr = null;

		try {
			md = MessageDigest.getInstance("SHA-1");
			// 将三个参数字符串拼接成一个字符串进行sha1加密
			byte[] digest = md.digest(content.toString().getBytes());
			tmpStr = byteToStr(digest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		content = null;
		// 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
		return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
	}
	/*
	 * 服务号获取当前token
	 * 
	 * db serviceId 服务号主键
	 */
	public static String getAccessTokenForService(Db db, String serviceId)
			throws Throwable {

		String updateTokenSql = getLocalResource("/transactions/project/weixin/service/update-token.sql");
		updateTokenSql = StringUtil.replace(updateTokenSql, "${service_id}",
				serviceId);

		Recordset tokenRecordset = db
				.get(StringUtil
						.replace(
								getLocalResource("/transactions/project/weixin/service/query-token.sql"),
								"${service_id}", serviceId));
		tokenRecordset.first();
		String appid = tokenRecordset.getString("appid");
		String appsecret = tokenRecordset.getString("appsecret");
		tokenRecordset.first();
		if (tokenRecordset.getString("access_token") == null) {
			String new_access_token = getWeixinAccessTokenForService(appid,
					appsecret);
			updateTokenSql = StringUtil.replace(updateTokenSql,
					"${access_token}", new_access_token);
			db.exec(updateTokenSql);
			return new_access_token;
		} else {
			tokenRecordset.first();
			String old_access_token = tokenRecordset.getString("access_token");
			java.util.Date start = tokenRecordset.getDate("addtime");
			String expiresIb = tokenRecordset.getString("expires_ib");
			java.util.Date end = new java.util.Date();
			if (end.getTime() - start.getTime() > Integer.parseInt(expiresIb) * 1000) {
				String new_access_token = getWeixinAccessTokenForService(appid,
						appsecret);
				updateTokenSql = StringUtil.replace(updateTokenSql,
						"${access_token}", new_access_token);
				db.exec(updateTokenSql);
				return new_access_token;
			} else {
				return old_access_token;
			}
		}
	}

	/*
	 * 服务号从微信服务器获取最新的AccessToken
	 * 
	 * appid appsecret
	 */
	public static String getWeixinAccessTokenForService(String appid,
			String appsecret) throws Throwable {
		String new_access_token = null;
		String access_token_url = SER_ACCESS_TOKEN_URL
				+ "?grant_type=client_credential&appid=" + appid + "&secret="
				+ appsecret;
		JSONObject jsonObject = (JSONObject) httpRequest(access_token_url,
				"GET", null, null);
		if( !jsonObject.has("access_token") || jsonObject.isNull("access_token") ){
			System.out.println("Get AccessToken Errror: " + jsonObject);
		}
		new_access_token = (String) jsonObject.get("access_token");
		return new_access_token;
	}

	/*
	 * 服务号创建菜单
	 * 
	 * postString 菜单拼接jason字符串 accessToken
	 */
	public static void createMenuForService(String postString,
			String accessToken) throws Throwable {
		String menu_create_url = SER_MENU_CREATE_URL + accessToken;
		JSONObject jsonObject = (JSONObject) httpRequest(menu_create_url,
				"POST", postString, null);
		int errcode = jsonObject.getInt("errcode");
		if (0 != errcode) {
			throw new Throwable(jsonObject.toString());
		}
	}

	/*
	 * 服务号OAuth2.0网页授权,获取用户ID
	 * 
	 * appid secret code 微信回调code
	 */
	public static String getWeixinUserIdForService(String appid, String secret,
			String code) throws Throwable {
		String user_id_url = SER_USER_ID_URL + "?appid=" + appid + "&secret="
				+ secret + "&code=" + code + "&grant_type=authorization_code";
		JSONObject jsonObject = (JSONObject) httpRequest(user_id_url, "GET",
				null, null);
		String UserId = (String) jsonObject.get("openid");
		return UserId;
	}
	/*
	 * 服务号OAuth2.0网页授权,获取access_token
	 * 
	 * 
	 */
	public static JSONObject getWeixinAuthAccessTokenForService(String appid, String secret,
			String code) throws Throwable {
		String user_id_url = SER_USER_ID_URL + "?appid=" + appid + "&secret="
				+ secret + "&code=" + code + "&grant_type=authorization_code";
		JSONObject jsonObject = (JSONObject) httpRequest(user_id_url, "GET",
				null, null);
		return jsonObject;
	}
	/*
	 * 服务号获取用户列表
	 */
	public static JSONObject getUserListForService(String accessToken)
			throws Throwable {
		if (null == accessToken || "".equals(accessToken)) {
			throw new Throwable("access_token不能为空");
		}
		JSONObject jsonObject = (JSONObject) httpRequest(
				SER_USER_LIST_URL.concat(accessToken), "GET", null, null);
		return jsonObject;
	}

	public static JSONObject getUserDetailForService(String accessToken,
			String openid) throws Throwable {
		if (null == accessToken || "".equals(accessToken)) {
			throw new Throwable("access_token不能为空");
		}
		JSONObject jsonObject = (JSONObject) httpRequest(StringUtil.replace(
				StringUtil.replace(SER_USER_DETAIL_URL, "ACCESS_TOKEN",
						accessToken), "OPENID", openid), "GET", null, null);
		return jsonObject;
	}

	/*
	 * 服务号发送文本消息
	 */
	public static JSONObject sendWeixinMessageTextForService(
			String accessToken, String touser, String message) throws Throwable {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("touser", touser);
		jsonObject.put("msgtype", "text");
		jsonObject.put("text", new JSONObject().put("content", message));
		return (JSONObject) httpRequest(
				SER_SEND_MESSAGE_URL.concat(accessToken), "POST",
				jsonObject.toString(), null);
	}

	/*
	 * 通用方法**********************************************************************
	 * ************************** http请求，
	 * 
	 * requestUrl 请求url requestMethod get或者post outputStr
	 */
	public static Object httpRequest(String requestUrl, String requestMethod,
			String outputStr, String type) throws Throwable {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		HttpsURLConnection httpUrlConn = null;
		try {
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// 当有数据需要提交时
			if (null != outputStr) {
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(
					inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;

			httpUrlConn.disconnect();
			if ("1".equals(type)) {
				return buffer.toString();
			} else {
				jsonObject = new JSONObject(buffer.toString());
			}
		} catch (Throwable e) {
			throw e;
		} finally {
			if (null != httpUrlConn) {
				httpUrlConn.disconnect();
			}
		}
		return jsonObject;
	}

	/*
	 * 
	 * 解析xml
	 */
	public static Map<String, String> parseXml(String xmlString)
			throws Exception {
		Map<String, String> map = null;
		try {
			map = new HashMap<String, String>();
			// 从request中取得输入流
			Document document = DocumentHelper.parseText(xmlString);
			// 得到xml根元素
			Element root = document.getRootElement();
			// 得到根元素的所有子节点
			List<Element> elementList = root.elements();

			// 遍历所有子节点
			for (Element e : elementList)
				map.put(e.getName(), e.getText());
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return map;
	}

	/*
	 * 获取微信post请求字符串
	 */
	public static String getPostString(HttpServletRequest request)
			throws IOException {
		InputStream inputStream = null;
		StringBuffer sb = new StringBuffer();

		try {
			inputStream = request.getInputStream();
			byte[] data = new byte[5000];
			while (true) {
				int len = inputStream.read(data);
				if (len != -1) {
					sb.append(new String(data, 0, len));
				} else {
					break;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if (null != inputStream) {
				inputStream.close();
				inputStream = null;
			}
		}
		return sb.toString();
	}

	/*
	 * 获取指定位置文件
	 */
	public static String getLocalResource(String path) throws Throwable {

		StringBuffer buf = new StringBuffer(5000);
		byte[] data = new byte[5000];

		InputStream in = null;

		in = new WeixinUtil().getClass().getResourceAsStream(path);

		try {
			if (in != null) {
				while (true) {
					int len = in.read(data);
					if (len != -1) {
						buf.append(new String(data, 0, len));
					} else {
						break;
					}
				}
				return buf.toString();
			} else {
				throw new Throwable("Invalid path to resource: " + path);
			}

		} catch (Throwable e) {
			throw e;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}

	}

	/**
	 * @param request
	 * @throws Throwable
	 *             重新转化成功跳转后的参数
	 */
	public static void resetSuccessUrl(HttpServletRequest request)
			throws Throwable {
		// 对request数据参数处理
		Object successUrl = request.getAttribute("successUrl");
		if (successUrl == null) {
			throw new Throwable("跳转地址不能为空!");
		}
		StringBuffer url = new StringBuffer(256);
		url.append(successUrl.toString());
		Object successParams = request.getAttribute("successParams");
		if (successParams != null && successParams.toString().length() > 0) {
			String[] params = successParams.toString().split(",");
			for (int i = 0; i < params.length; i++) {
				String value = request.getParameter(params[i]);
				if (value == null) {
					Object v = request.getAttribute(params[i]);
					if (v != null) {
						value = v.toString();
					}
				}
				if (value != null) {
					if (url.indexOf("?") > 0) {
						url.append("&");
					} else {
						url.append("?");
					}
					url.append(params[i]).append("=").append(value);
				}
			}
		}
		request.setAttribute("successUrl", url.toString());
	}

	/**
	 * MD5加密
	 * 
	 * @param plainText
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String Md5(String plainText) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(plainText.getBytes());
		return new BigInteger(1, md.digest()).toString(16);
	}

	/**
	 * 将字节数组转换为十六进制字符串
	 * 
	 * @param byteArray
	 * @return
	 */
	private static String byteToStr(byte[] byteArray) {
		String strDigest = "";
		for (int i = 0; i < byteArray.length; i++) {
			strDigest += byteToHexStr(byteArray[i]);
		}
		return strDigest;
	}

	/**
	 * 将字节转换为十六进制字符串
	 * 
	 * @param mByte
	 * @return
	 */
	private static String byteToHexStr(byte mByte) {

		char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
				'B', 'C', 'D', 'E', 'F' };
		char[] tempArr = new char[2];
		tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
		tempArr[1] = Digit[mByte & 0X0F];

		String s = new String(tempArr);
		return s;
	}

	/*
	 * Base64加密
	 */
	public static String base64Encode(String str) {
		String base64EncodeChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

		int c1, c2, c3;
		int len = str.length();
		int i = 0;
		String out = "";
		while (i < len) {

			c1 = str.charAt(i++) & 0xff;
			if (i == len) {
				out += base64EncodeChars.charAt(c1 >> 2);
				out += base64EncodeChars.charAt((c1 & 0x3) << 4);
				out += "==";
				break;
			}
			c2 = str.charAt(i++);
			if (i == len) {
				out += base64EncodeChars.charAt(c1 >> 2);
				out += base64EncodeChars.charAt(((c1 & 0x3) << 4)
						| ((c2 & 0xF0) >> 4));
				out += base64EncodeChars.charAt((c2 & 0xF) << 2);
				out += "=";
				break;
			}
			c3 = str.charAt(i++);
			out += base64EncodeChars.charAt(c1 >> 2);
			out += base64EncodeChars.charAt(((c1 & 0x3) << 4)
					| ((c2 & 0xF0) >> 4));
			out += base64EncodeChars.charAt(((c2 & 0xF) << 2)
					| ((c3 & 0xC0) >> 6));
			out += base64EncodeChars.charAt(c3 & 0x3F);
		}
		return out;
	}

	/*
	 * 微信支付获取package
	 */
	public static String getPackage(String appid, String mch_id,
			String spbill_create_ip, String nonce_str, String openid,
			String body, String out_trade_no, String notify_url,
			String trade_type, String total_fee, String partnerKey,
			String timestamp) throws Throwable {
		total_fee=String.valueOf(Double.parseDouble(total_fee)*100);
		if(total_fee.indexOf(".")>0){
			total_fee=total_fee.substring(0,total_fee.lastIndexOf("."));
		}
		
		String sign = getSign(appid, mch_id, spbill_create_ip, nonce_str,
				openid, body, out_trade_no, notify_url, trade_type, total_fee,
				partnerKey);
		String postString = XMLParse.generate(openid, body, out_trade_no,
				total_fee, notify_url, trade_type, appid, mch_id,
				spbill_create_ip, nonce_str, sign);
		String returnMessage = (String) httpRequest(WX_PAY_UNIFIEDORDER_URL,
				"POST", postString, "1");
		String return_code = XMLParse.getElement(returnMessage)
				.getElementsByTagName("return_code").item(0).getTextContent();
		String err_code = null;
		try {
			err_code = XMLParse.getElement(returnMessage)
					.getElementsByTagName("err_code").item(0).getTextContent();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		if ("SUCCESS".equals(return_code) && null == err_code) {
			String prepay_id = XMLParse.getElement(returnMessage)
					.getElementsByTagName("prepay_id").item(0).getTextContent();
			return "prepay_id=" + prepay_id;
		} else {
			throw new Throwable(returnMessage);
		}
	}

	/*
	 * 微信支付获取统一下单sign
	 */
	public static String getSign(String appid, String mch_id,
			String spbill_create_ip, String nonce_str, String openid,
			String body, String out_trade_no, String notify_url,
			String trade_type, String total_fee, String partnerKey)
			throws NoSuchAlgorithmException {
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", appid);
		params.put("mch_id", mch_id);
		params.put("spbill_create_ip", spbill_create_ip);
		params.put("nonce_str", nonce_str);
		params.put("openid", openid);
		params.put("body", body);
		params.put("out_trade_no", out_trade_no);
		params.put("notify_url", notify_url);
		params.put("trade_type", trade_type);
		params.put("total_fee", total_fee);

		Object[] akeys = (Object[]) params.keySet().toArray();
		Arrays.sort(akeys);

		for (int i = 0; i < akeys.length; i++) {
			if (i != akeys.length - 1) {
				sb.append(akeys[i] + "=" + params.get(akeys[i]) + "&");
			} else {
				sb.append(akeys[i] + "=" + params.get(akeys[i]));
			}
		}
		sb.append("&key=" + partnerKey);
		return MD5Util.MD5Encode(sb.toString(), "UTF-8").toUpperCase();
	}

	/*
	 * 支付签名（paySign）生成方法
	 */
	public static String getPaySign(String timestamp, String noncestr,
			String packageStr, String partnerKey, String appid)
			throws Throwable {
		StringBuilder sb = new StringBuilder();

		Map<String, String> params = new HashMap<String, String>();
		params.put("appId", appid);
		params.put("timeStamp", timestamp);
		params.put("nonceStr", noncestr);
		params.put("package", packageStr);
		params.put("signType", "MD5");

		Object[] akeys = (Object[]) params.keySet().toArray();
		Arrays.sort(akeys);

		for (int i = 0; i < akeys.length; i++) {
			if (i != akeys.length - 1) {
				sb.append(akeys[i] + "=" + params.get(akeys[i]) + "&");
			} else {
				sb.append(akeys[i] + "=" + params.get(akeys[i]));
			}
		}
		sb.append("&key=" + partnerKey);
		String paySign = MD5Util.MD5Encode(sb.toString(), "UTF-8")
				.toUpperCase();
		return paySign;
	}

	/*
	 * 微信支付获取signature
	 */
	public static JSONObject signature(String jsapi_ticket, String url)
			throws JSONException {
		String nonce_str = create_nonce_str();
		String timestamp = create_timestamp();
		String string1;
		String signature = "";

		// 注意这里参数名必须全部小写，且必须有序
		string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str
				+ "&timestamp=" + timestamp + "&url=" + url;
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("noncestr", nonce_str);
		jsonObject.put("timestamp", timestamp);
		jsonObject.put("signature", signature);
		return jsonObject;
		/***
		String nonce_str = create_nonce_str();
		String timestamp = create_timestamp();
		String string1;
		String signature = "";

		// 注意这里参数名必须全部小写，且必须有序
		string1 = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str
				+ "&timestamp=" + timestamp + "&url=" + url;
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			 // 对拼接后的字符串进行 sha1 加密
	        byte[] digest = crypt.digest(string1.getBytes());
	        signature = byteToStr(digest);
	        if( StringUtils.isNotBlank(signature) ){
	        	signature = signature.toLowerCase();
	        }
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("noncestr", nonce_str);
		jsonObject.put("timestamp", timestamp);
		jsonObject.put("signature", signature);
		return jsonObject;
		*/
	}
	/*
	 * 微信支付获得共享地址签名
	 */
	public static String getAddrSign(String appId,String url,String timeStamp,String  nonceStr,String accessToken) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> params = new HashMap<String, String>();
		params.put("appid", appId);
		params.put("url", url);
		params.put("timestamp", timeStamp);
		params.put("noncestr", nonceStr);
		params.put("accesstoken", accessToken);
		
		System.out.println("appId:"+appId);
		System.out.println("url:"+url);
		System.out.println("timestamp:"+timeStamp);
		System.out.println("noncestr:"+nonceStr);
		System.out.println("accesstoken:"+accessToken);
		
		Object[] akeys = (Object[]) params.keySet().toArray();
		Arrays.sort(akeys);

		for (int i = 0; i < akeys.length; i++) {
			if (i != akeys.length - 1) {
				sb.append(akeys[i] + "=" + params.get(akeys[i]) + "&");
			} else {
				sb.append(akeys[i] + "=" + params.get(akeys[i]));
			}
		}
		MessageDigest crypt = null;
		try {
			crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(sb.toString().getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return   byteToHex(crypt.digest());
	}
	/*
	 * 微信支付----查询订单
	 */
	public static String orderQuery(String appid,String mch_id,String transaction_id,String out_trade_no,String nonce_str,String key) throws Throwable{
		Map<String, String> map=new HashMap<String,String>();
		map.put("appid", appid);
		map.put("mch_id", mch_id);
		map.put("nonce_str", nonce_str);
		
		if(null!=transaction_id&&!"".equals(transaction_id)){
			map.put("transaction_id", transaction_id);
		}
		if(null!=out_trade_no&&!"".equals(out_trade_no)){
			map.put("out_trade_no", out_trade_no);
		}
		String sign=getsign2(map,key);
		
		String outputString=
		"<xml>"+
		   "<appid>"+appid+"</appid>"+
		   "<mch_id>"+mch_id+"</mch_id>"+
		   "<nonce_str>"+nonce_str+"</nonce_str>"+
		   (null!=transaction_id&&!"".equals(transaction_id)?"<transaction_id>"+transaction_id+"</transaction_id>":"")+
		   (null!=out_trade_no&&!"".equals(out_trade_no)?"<out_trade_no>"+out_trade_no+"</out_trade_no>":"")+
		   "<sign>"+sign+"</sign>"+
		"</xml>";
		return (String)httpRequest(WX_PAY_ORDER_QUERY, "POST", outputString, "1");
	}
	
	
	private static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	/*
	 * 获取服务端jsapi_ticket
	 */
	public static String getJsTicket(Db db, String serviceId) throws Throwable {
		Recordset ticketRecordset = db
				.get(StringUtil
						.replace(
								getLocalResource("/transactions/project/weixin/common/query-ticket.sql"),
								"${tuid}", serviceId));
		ticketRecordset.first();
		String updateTicketSql = StringUtil
				.replace(
						getLocalResource("/transactions/project/weixin/common/update-ticket.sql"),
						"${tuid}", serviceId);
		if (ticketRecordset.getString("jsapi_ticket") == null) {
			String new_ticket = getJsTicketNew(db, serviceId);
			updateTicketSql = StringUtil.replace(StringUtil.replace(
					updateTicketSql, "${ticket}", new_ticket), "${tuid}",
					serviceId);
			db.exec(updateTicketSql);
			return new_ticket;
		} else {
			String old_ticket = ticketRecordset.getString("jsapi_ticket");
			java.util.Date start = ticketRecordset.getDate("addtime");
			String expiresIb = ticketRecordset.getString("expires_ib");
			java.util.Date end = new java.util.Date();
			if (end.getTime() - start.getTime() > Integer.parseInt(expiresIb) * 1000) {
				String new_ticket = getJsTicketNew(db, serviceId);
				updateTicketSql = StringUtil.replace(StringUtil.replace(
						updateTicketSql, "${tuid}", serviceId), "${ticket}",
						new_ticket);
				db.exec(updateTicketSql);
				return new_ticket;
			} else {
				return old_ticket;
			}
		}
	}
	/*
	 * 微信支付操作签名1
	 */
	public static String getsign1(Map<String,String> params)
			throws JSONException {
		StringBuilder sb = new StringBuilder();

		Object[] akeys = (Object[]) params.keySet().toArray();
		Arrays.sort(akeys);

		for (int i = 0; i < akeys.length; i++) {
			if (i != akeys.length - 1) {
				sb.append(akeys[i] + "=" + params.get(akeys[i]) + "&");
			} else {
				sb.append(akeys[i] + "=" + params.get(akeys[i]));
			}
		}
		String signature=null;
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(sb.toString().getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return signature;
	}
	/*
	 * 微信支付操作签名2
	 */
	public static String getsign2(Map<String,String> params,String key){
		String signString="";
		
		StringBuilder sb = new StringBuilder();

		Object[] akeys = (Object[]) params.keySet().toArray();
		Arrays.sort(akeys);

		for (int i = 0; i < akeys.length; i++) {
			if (i != akeys.length - 1) {
				sb.append(akeys[i] + "=" + params.get(akeys[i]) + "&");
			} else {
				sb.append(akeys[i] + "=" + params.get(akeys[i]));
			}
		}
		sb.append("&key=" + key);
		signString = MD5Util.MD5Encode(sb.toString(), "UTF-8")
				.toUpperCase();
		return signString;
	}
	/*
	 * 微信支付，发货通知
	 */
	public static JSONObject deliverNotify(String appid,String openid,String transid,String out_trade_no,String deliver_timestamp,String deliver_status
			,String deliver_msg,String appkey,String accessToken) throws Throwable{
		Map<String, String> map =new HashMap<String,String>();
		map.put("appid", appid);
		map.put("openid", openid);
		map.put("transid", transid);
		map.put("out_trade_no", out_trade_no);
		map.put("deliver_timestamp", deliver_timestamp);
		map.put("deliver_status", deliver_status);
		map.put("deliver_msg", deliver_msg);
		String app_signature=getsign2(map,appkey);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("appid", appid);
		jsonObject.put("openid", openid);
		jsonObject.put("transid", transid);
		jsonObject.put("out_trade_no", out_trade_no);
		jsonObject.put("deliver_timestamp", deliver_timestamp);
		jsonObject.put("deliver_status", deliver_status);
		jsonObject.put("deliver_msg", deliver_msg);
		jsonObject.put("app_signature", app_signature);
		jsonObject.put("sign_method", "sha1");
		String outputStr=jsonObject.toString();
		return (JSONObject)httpRequest(WX_PAY_DELIVER_NOTIFY+accessToken, "POST", outputStr, null);
	}
	/*
	 * 获取微信端最新jsapi_ticket
	 */
	public static String getJsTicketNew(Db db, String serviceId)
			throws Throwable {
		String accessToken = getAccessTokenForService(db, serviceId);
		JSONObject jsonObject = (JSONObject) httpRequest(StringUtil.replace(
				WX_JS_TICKET_URL, "${access_token}", accessToken), "GET", null,
				null);
		return jsonObject.getString("ticket");
	}

	public static String create_nonce_str() {
		Random random = new Random();
		return MD5Util
				.MD5Encode(String.valueOf(random.nextInt(10000)), "UTF-8");
	}
	
	/*
	 * 获取当前时间
	 */
	public static String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}
	
	/***
	 * 生成永久二维码
	 * @param scene_id 场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
	 * @param accessToken
	 * @return result
	 * @throws Throwable
	 */
	public static JSONObject createQrcode(int scene_id, String accessToken) throws Throwable {
		JSONObject json = new JSONObject();
		json.put("action_name", "QR_LIMIT_SCENE");
		json.put("action_info", new JSONObject().put("scene", new JSONObject().put("scene_id", scene_id)));
		String qrcodeCreateUrl = QRCODE_CREATE_URL + accessToken;
		JSONObject result = (JSONObject) httpRequest(qrcodeCreateUrl, "POST", json.toString(), null);
		if ( result.has("errcode") && 0 != result.getInt("errcode") ) {
			throw new Throwable(result.toString());
		}
		return result;
	}
	
	/***
	 * 生成临时二维码
	 * @param scene_id 场景值ID，临时二维码时为32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
	 * @param scene_str 场景值ID（字符串形式的ID），字符串类型，长度限制为1到64
	 * @param expire_seconds 二维码有效时间，以秒为单位。 最大不超过2592000（即30天）
	 * @param accessToken
	 * @return result
	 * @throws Throwable
	 */
	public static JSONObject createTempQrcode(int scene_id, String scene_str, long expire_seconds, String accessToken) throws Throwable {
		JSONObject json = new JSONObject();
		json.put("action_name", "QR_SCENE");
		json.put("expire_seconds", expire_seconds);
		json.put("action_info", new JSONObject().put("scene", new JSONObject().put("scene_id", scene_id).put("scene_str", scene_str)));
		String qrcodeCreateUrl = QRCODE_CREATE_URL + accessToken;
		JSONObject result = (JSONObject) httpRequest(qrcodeCreateUrl, "POST", json.toString(), null);
		if ( result.has("errcode") && 0 != result.getInt("errcode") ) {
			throw new Throwable(result.toString());
		}
		return result;
	}
	
	/***
	 * 下载多媒体文件
	 * @param db
	 * @param service_id
	 * @param media_id
	 * @param savePath
	 * @param filename
	 * @throws Throwable
	 */
	public static void getFileByMediaId(Db db, Integer service_id, String media_id, String savePath, String filename) throws Throwable{
		String accessToken = WeixinUtil.getAccessTokenForService(db, String.valueOf(service_id));
		String uri = StringUtils.replace(GET_MEDIA_URL, "${access_token}", accessToken) + media_id;
		Tools.downloadFile(uri, savePath, filename);
	}
	
	/**
	 * 获取IP
	 * @param request
	 * @return
	 */
	public static String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if( null != ip && !"".equals(ip) && ip.contains(",") ){
			String[] str = ip.split(",");
			ip = str[0];
		}
		return ip;
	}
}
