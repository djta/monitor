package transactions.project.weixin.service;

import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;
import transactions.project.weixin.common.WeixinUtil;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class PublishMenuForService extends GenericTableManager {
	String appid = null;
	String appsecret = null;

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		String serviceId=inputParams.getString("app_id");
		Db db = getDb();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		String parentMenuSql = getSQL(getResource("query-parent_menu.sql"),
				inputParams);
		String childMenuSql = getSQL(getResource("query-child_menu.sql"),
				inputParams);
		String queryServiceSql = getSQL(getResource("query-service.sql"), inputParams);
		Recordset queryCorpRecordset = db.get(queryServiceSql);
		queryCorpRecordset.first();
		appid = queryCorpRecordset.getString("appid");
		if (null == appid) {
			throw new Exception("AppId不能为空");
		}
		appsecret = queryCorpRecordset.getString("appsecret");
		if (null == appsecret) {
			throw new Exception("AppSecret不能为空");
		}
		Recordset parentMenuRecordset = db.get(parentMenuSql);
		String url_head = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ appid + "&redirect_uri=";
		String url_wb = "&response_type=code&scope=snsapi_base&state="
				+ serviceId + "#wechat_redirect";
		if (parentMenuRecordset.getRecordCount() > 0) {
			parentMenuRecordset.top();

			while (parentMenuRecordset.next()) {

				String parent_menu_url_pre = parentMenuRecordset
						.getString("menu_url_pre");
				String parent_menu_name = parentMenuRecordset
						.getString("menu_name");
				String parent_first_page = parentMenuRecordset
						.getString("first_page");
				String parent_tuid = parentMenuRecordset.getString("tuid");
				String parent_menu_key = parentMenuRecordset
						.getString("menu_key");
				String parentradiotype=parentMenuRecordset.getString("radiotype");
				Recordset childMenuRecordset = db.get(StringUtil.replace(
						childMenuSql, "${p_id}", parent_tuid));
				if (childMenuRecordset.getRecordCount() > 0) {
					childMenuRecordset.top();
					JSONObject childMenuToal = new JSONObject();
					childMenuToal.put("name", parent_menu_name);
					JSONArray childjsonArray = new JSONArray();

					while (childMenuRecordset.next()) {
						String child_menu_url_pre = childMenuRecordset
								.getString("menu_url_pre");
						if (null == child_menu_url_pre) {
							throw new Throwable("menu_url_pre不能为空");
						}
						String child_menu_name = childMenuRecordset
								.getString("menu_name");
						String child_first_page = childMenuRecordset
								.getString("first_page");
						if (null == child_first_page) {
							throw new Exception("first_page不能为空");
						}
						String childradiotype=childMenuRecordset.getString("radiotype");
						String child_menu_key = childMenuRecordset
								.getString("menu_key");
						String name = child_menu_name;
						String key = child_menu_key;

						JSONObject childMenu = new JSONObject();
						childMenu.put("name", name);
						childMenu.put("key", key);
						
						if("0".equals(childradiotype)){
							childMenu.put("type", "view");
							String url = url_head
									+ URLEncoder
											.encode(child_menu_url_pre
													+ child_first_page
													+ (child_first_page
															.indexOf("?") > 0 ? "&"
															: "?") + "sid="
													+ serviceId ) + url_wb;
							
							childMenu.put("url", url);
						}else{
							childMenu.put("type", "click");
						}
						childjsonArray.put(childMenuRecordset.getRecordNumber(), childMenu);
					}
					childMenuToal.put("sub_button", childjsonArray);
					jsonArray.put(parentMenuRecordset.getRecordNumber(),
							childMenuToal);
					;
				} else {
					String key = parent_menu_key;
					JSONObject parentMenu = new JSONObject();
					String name = parent_menu_name;
					parentMenu.put("name", name);
					parentMenu.put("key", key);
					
					if("0".equals(parentradiotype)){
						if (null == parent_menu_url_pre
								|| null == parent_first_page) {
							continue;
						}
						parentMenu.put("type", "view");
						String url = url_head
								+ URLEncoder.encode(parent_menu_url_pre
										+ parent_first_page
										+ (parent_first_page.indexOf("?") > 0 ? "&"
												: "?") + "sid=" + serviceId) + url_wb;

						parentMenu.put("url", url);
					}else{
						parentMenu.put("type", "click");
					}
					

					jsonArray.put(parentMenuRecordset.getRecordNumber(),
							parentMenu);
				}

			}
		}
		jsonObject.put("button", jsonArray);
		String accessToken = WeixinUtil.getAccessTokenForService(db, serviceId);
		WeixinUtil.createMenuForService(jsonObject.toString(), accessToken);
		return rc;
	}
	/*public static void main(String[] args) {
		String s1=URLEncoder.encode("https://open.weixin.qq.com/connect/oauth2/authorize?appid=${fld:appid}&redirect_uri=http://club.desoft.cn/club/action/project/weixin/pay/crud?out_trade_no=${fld:sale_order_id}"+
				"&response_type=code&scope=snsapi_base&state=1#wechat_redirect");
		System.out.println(s1);
	}*/
}
