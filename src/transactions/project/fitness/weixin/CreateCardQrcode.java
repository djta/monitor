package transactions.project.fitness.weixin;

import java.sql.Types;

import org.apache.commons.lang.StringUtils;

import transactions.project.fitness.util.ErpTools;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

/**
 * 生成会员卡常规二维码
 * @author C
 * 2018-01-29
 */
public class CreateCardQrcode extends GenericTransaction {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		String qrcodePath = "";
		Integer tuid = null;
		try {
			// 验证参数
			String pk_value = inputParams.containsField("pk_value") ? inputParams.getString("pk_value") : "";
			if( null == pk_value || "".equals(pk_value) ){
				throw new Throwable("提交参数pk_value不能为空");
			}

			Db db = getDb();
			String queryCard = "SELECT qrcode_path, org_id, md5(concat(org_id, '###', code)) AS qrcode_value FROM cc_card WHERE code = ${fld:pk_value} AND org_id = ${def:org} AND isgoon = 0 AND status != 0 AND status != 6";
			queryCard = getSQL(queryCard, inputParams);
			Recordset rsCard = db.get(queryCard);
			if( null == rsCard || rsCard.getRecordCount() <= 0 ){
				throw new Throwable("未找到相关卡信息");
			}
			rsCard.first();
			if( !rsCard.isNull("qrcode_path") && null != rsCard.getValue("qrcode_path") && !"".equals(rsCard.getString("qrcode_path")) ){
				qrcodePath = rsCard.getString("qrcode_path");
			}

			// 生成常规二维码
			if( null == qrcodePath || "".equals(qrcodePath) ){
				Integer org_id = rsCard.getInteger("org_id");
				String qrcode_value = rsCard.getString("qrcode_value");
				qrcodePath = ErpTools.createCommonQrcode(getRequest(), qrcode_value, pk_value, org_id);
				
				// 更新二维码地址
				String update = "UPDATE cc_card SET qrcode_path = '${qrcode_path}' WHERE code = ${fld:pk_value} AND org_id = ${org_id}";
				update = StringUtils.replace(update, "${qrcode_path}", qrcodePath);
				update = StringUtils.replace(update, "${org_id}", String.valueOf(org_id));
				update = getSQL(update, inputParams);
				db.exec(update);
			}

			// 添加二维码记录
			if( null != qrcodePath && !"".equals(qrcodePath) ){
				String querySeq = getLocalResource("/transactions/project/fitness/weixin/sql/qrcode/query-qrcode-seq.sql");
				Recordset rsSeq = db.get(querySeq);
				rsSeq.first();
				tuid = rsSeq.getInteger("tuid");
				
				String insertQrcode = getLocalResource("/transactions/project/fitness/weixin/sql/qrcode/insert-qrcode.sql");
				insertQrcode = getSQL(insertQrcode, inputParams);
				insertQrcode = StringUtil.replace(insertQrcode, "${tuid}", String.valueOf(tuid));
				insertQrcode = StringUtil.replace(insertQrcode, "${qrcodetype}", String.valueOf(1));
				insertQrcode = StringUtil.replace(insertQrcode, "${expire_seconds}", "NULL");
				insertQrcode = StringUtil.replace(insertQrcode, "${qrcode_uri}", qrcodePath);
				db.exec(insertQrcode);
			}
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
			Recordset rsQrcode = new Recordset();
			rsQrcode.append("qrcode_id", Types.VARCHAR);
			rsQrcode.append("qrcode_path", Types.VARCHAR);
			rsQrcode.addNew();
			rsQrcode.setValue("qrcode_id", String.valueOf(tuid));
			rsQrcode.setValue("qrcode_path", qrcodePath);
			publish("_rsQrcode", rsQrcode);
		}
		return rc;
	}
}
