package transactions.project.weixin.corp;

import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;
import transactions.project.weixin.common.WeixinUtil;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

public class PublishMenuForCorp extends GenericTableManager {
	String corpid = null;
	String corpsecret = null;
	String agentid = null;

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = getDb();
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		String parentMenuSql = getSQL(getResource("query-parent_menu.sql"),
				inputParams);
		String childMenuSql = getSQL(getResource("query-child_menu.sql"),
				inputParams);
		String queryCorpSql = getSQL(getResource("query-corp.sql"), inputParams);
		String app_id = inputParams.getString("app_id");
		Recordset queryCorpRecordset = db.get(queryCorpSql);
		queryCorpRecordset.first();
		corpid = queryCorpRecordset.getString("corp_id");
		String corp_tuid = queryCorpRecordset.getString("corp_tuid");
		if (null == corpid) {
			throw new Exception("corpid不能为空");
		}
		corpsecret = queryCorpRecordset.getString("secret");
		if (null == corpsecret) {
			throw new Exception("corpsecret不能为空");
		}
		agentid = queryCorpRecordset.getString("app_id");
		if (null == agentid) {
			throw new Exception("agentid不能为空");
		}
		Recordset parentMenuRecordset = db.get(parentMenuSql);
		String url_head = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ corpid + "&redirect_uri=";
		String url_wb = "&response_type=code&scope=snsapi_base&state="
				+ corp_tuid + "#wechat_redirect";
		if (parentMenuRecordset.getRecordCount() > 0) {
			parentMenuRecordset.top();

			while (parentMenuRecordset.next()) {

				String parent_menu_url_pre = parentMenuRecordset
						.getString("menu_url_pre");
				String parent_menu_name = parentMenuRecordset
						.getString("menu_name");
				String parent_first_page = parentMenuRecordset
						.getString("first_page");
				String parent_menu_id = parentMenuRecordset
						.getString("menu_id");
				String parent_tuid = parentMenuRecordset.getString("tuid");
				String parent_menu_key = parentMenuRecordset
						.getString("menu_key");
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
						String child_menu_id = parentMenuRecordset
								.getString("menu_id");
						String child_menu_key = childMenuRecordset
								.getString("menu_key");

						String type = "view";
						String name = child_menu_name;
						String url = url_head
								+ URLEncoder
										.encode(child_menu_url_pre
												+ child_first_page
												+ (child_first_page
														.indexOf("?") > 0 ? "&"
														: "?") + "agentid="
												+ agentid + "&menu_id="
												+ child_menu_id) + url_wb;
						String key = child_menu_key;
						JSONObject childMenu = new JSONObject();
						childMenu.put("type", type);
						childMenu.put("name", name);
						childMenu.put("url", url);
						childMenu.put("key", key);
						childjsonArray
								.put(childMenuRecordset.getRecordNumber(),
										childMenu);
					}
					childMenuToal.put("sub_button", childjsonArray);
					jsonArray.put(parentMenuRecordset.getRecordNumber(),
							childMenuToal);
					;
				} else {
					if (null == parent_menu_url_pre
							|| null == parent_first_page) {
						continue;
					}
					String type = "view";
					String name = parent_menu_name;
					String url = url_head
							+ URLEncoder.encode(parent_menu_url_pre
									+ parent_first_page
									+ (parent_first_page.indexOf("?") > 0 ? "&"
											: "?") + "agentid=" + agentid
									+ "&menu_id=" + parent_menu_id) + url_wb;
					String key = parent_menu_key;
					JSONObject parentMenu = new JSONObject();
					parentMenu.put("type", type);
					parentMenu.put("name", name);
					parentMenu.put("url", url);
					parentMenu.put("key", key);
					jsonArray.put(parentMenuRecordset.getRecordNumber(),
							parentMenu);
				}

			}
		}
		jsonObject.put("button", jsonArray);
		String accessToken = WeixinUtil.getAccessTokenForCorp(db, app_id,
				corpid, corpsecret);
		WeixinUtil.createMenuForCopr(jsonObject.toString(), accessToken,
				agentid);
		return rc;
	}
}
