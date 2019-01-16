package transactions.project.util.qcloud.service;

import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import transactions.project.util.qcloud.util.QCloudTools;

import com.qcloud.QcloudApiModuleCenter;
import com.qcloud.Module.Vod;
import com.qcloud.Utilities.Json.JSONObject;

import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;

/**
 * 删除云端视频
 * @author Administrator
 */
public class DeleteVideo extends GenericTableManager{
	public int service(Recordset inputParams) throws Throwable{
		int rc = super.service(inputParams);
		Db db = getDb();

		String fileid = !inputParams.isNull("fileid") && null != inputParams.getString("fileid") ? inputParams.getString("fileid") : null;
		if( StringUtils.isBlank(fileid) ){
			throw new Throwable("参数fileid不能为空");
			
		}
		// 获取帐号信息
		String query = QCloudTools.getLocalResource("/transactions/project/util/qcloud/service/sql/query-account.sql");
		Recordset rs = db.get(query);
		if( null == rs || rs.getRecordCount() <= 0 ){
			throw new Throwable("未找到云端帐号信息");
		}
		rs.first();
		String secretid = rs.getString("secretid");
		String secretkey = rs.getString("secretkey");
		
		TreeMap<String, Object> config = new TreeMap<String, Object>();
		config.put("SecretId", secretid);
		config.put("SecretKey", secretkey);
		config.put("RequestMethod", "GET");
		config.put("DefaultRegion", "");
	
		QcloudApiModuleCenter module = new QcloudApiModuleCenter(new Vod(), config);
			
		TreeMap<String, Object> params = new TreeMap<String, Object>();
		params.put("priority", 0);
		params.put("fileId", fileid);
		String result = module.call("DeleteVodFile", params);
		JSONObject jsonResult = new JSONObject(result);
		if( !"0".equals(String.valueOf(jsonResult.get("code"))) ){
			throw new Throwable("删除失败：" + jsonResult.get("message"));
		}
		return rc;
	}
}
