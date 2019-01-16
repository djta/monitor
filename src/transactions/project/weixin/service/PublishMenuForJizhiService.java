package transactions.project.weixin.service;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import transactions.project.weixin.common.WeixinUtil;
import dinamica.Recordset;

public class PublishMenuForJizhiService{
	static String appid = "wx74f2ddfcb5da6ff3";
	static String appsecret = "22cd3f9d47d18dddcb2236a4c7f23f42";

	public static void main(String [] args) throws Throwable {
		String serviceId="2001";
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		Recordset parentMenuRecordset = new Recordset();
		parentMenuRecordset.append("menu_url_pre", java.sql.Types.VARCHAR);
		parentMenuRecordset.append("menu_name", java.sql.Types.VARCHAR);
		parentMenuRecordset.append("first_page", java.sql.Types.VARCHAR);
		parentMenuRecordset.append("menu_key", java.sql.Types.VARCHAR);
		parentMenuRecordset.append("tuid", java.sql.Types.VARCHAR);
		parentMenuRecordset.append("radiotype", java.sql.Types.VARCHAR);
		parentMenuRecordset.addNew();
		parentMenuRecordset.setValue("menu_url_pre", "");
		parentMenuRecordset.setValue("menu_name", "极智医疗");
		parentMenuRecordset.setValue("first_page", "");
		parentMenuRecordset.setValue("menu_key", "firstpage");
		parentMenuRecordset.setValue("tuid", "");
		parentMenuRecordset.setValue("radiotype", "0");
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
				String parent_menu_key = parentMenuRecordset
						.getString("menu_key");
				String parentradiotype=parentMenuRecordset.getString("radiotype");
				
				String key = parent_menu_key;
				JSONObject parentMenu = new JSONObject();
				String name = parent_menu_name;
				parentMenu.put("name", name);
				//parentMenu.put("key", key);
				
				if("0".equals(parentradiotype)){
					parentMenu.put("type", "view");
					String url = url_head
							+ URLEncoder.encode("http://weixin.jizhiyiliao.com/user/home?tuid=2001") + url_wb;
					
					parentMenu.put("url", url);
				}else{
					parentMenu.put("type", "click");
				}
				
				
				jsonArray.put(parentMenuRecordset.getRecordNumber(),
						parentMenu);

			}
		}
		jsonObject.put("button", jsonArray);
		String accessToken = WeixinUtil.getWeixinAccessTokenForService(appid, appsecret);
		WeixinUtil.createMenuForService(jsonObject.toString(), accessToken);
	}
}
