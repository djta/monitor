package transactions.project.fitness.weixin;

import java.sql.Types;

import transactions.project.fitness.util.ErpTools;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

/**
 * 生成微信临时二维码
 * @author C
 * 2018-01-29
 */
public class WXTempQrcode extends GenericTransaction {

	public int service(Recordset inputParams) throws Throwable {
		String qrcode_uri = "";
		int rc = super.service(inputParams);
		long expire_seconds = ErpTools.WX_TEMP_QRCODE_expire_seconds;	// 二维码有效时长
		Integer sceneid = null;
		try {
			// 验证参数
			String pkvalue = inputParams.containsField("pk_value") ? inputParams.getString("pk_value") : "";
			if( null == pkvalue || "".equals(pkvalue) ){
				throw new Throwable("提交参数pk_value不能为空");
			}
			Integer datatype = inputParams.containsField("data_type") ? inputParams.getInteger("data_type") : null;
			if( null == datatype ){
				throw new Throwable("提交参数data_type不能为空");
			}

			// 获取微信公众号
			Db db = getDb();
			String sid = "";
			String queryAccount = "SELECT tuid FROM wx_service WHERE is_deleted = '0' ORDER BY tuid";
			Recordset rsAccount = db.get(queryAccount);
			while( rsAccount.next() ){
				sid = String.valueOf(rsAccount.getValue("tuid"));
				break;
			}
			String querySeq = getLocalResource("/transactions/project/fitness/weixin/sql/qrcode/query-qrcode-seq.sql");
			Recordset rsSeq = db.get(querySeq);
			rsSeq.first();
			sceneid = rsSeq.getInteger("tuid");

			// 获取临时二维码有效时长
			expire_seconds = Long.parseLong(getConfig().getConfigValue("expire_seconds", String.valueOf(ErpTools.WX_TEMP_QRCODE_expire_seconds)));
			
			// 生成临时二维码
			qrcode_uri = ErpTools.createWXTempQrcode(sid, pkvalue, sceneid, expire_seconds, db);
			
			// 添加二维码记录
			if( null != qrcode_uri && !"".equals(qrcode_uri) ){
				String insertQrcode = getLocalResource("/transactions/project/fitness/weixin/sql/qrcode/insert-qrcode.sql");
				insertQrcode = getSQL(insertQrcode, inputParams);
				insertQrcode = StringUtil.replace(insertQrcode, "${tuid}", String.valueOf(sceneid));
				insertQrcode = StringUtil.replace(insertQrcode, "${qrcodetype}", String.valueOf(0));
				insertQrcode = StringUtil.replace(insertQrcode, "${expire_seconds}", String.valueOf(expire_seconds));
				insertQrcode = StringUtil.replace(insertQrcode, "${qrcode_uri}", qrcode_uri);
				db.exec(insertQrcode);
			}
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
			Recordset rsQrcode = new Recordset();
			rsQrcode.append("qrcode_id", Types.VARCHAR);
			rsQrcode.append("qrcode_uri", Types.VARCHAR);
			rsQrcode.append("expire_seconds", Types.VARCHAR);
			rsQrcode.addNew();
			rsQrcode.setValue("qrcode_id", String.valueOf(sceneid));
			rsQrcode.setValue("qrcode_uri", qrcode_uri);
			rsQrcode.setValue("expire_seconds", String.valueOf(expire_seconds));
			publish("_rsQrcode", rsQrcode);
		}
		return rc;
	}
}
