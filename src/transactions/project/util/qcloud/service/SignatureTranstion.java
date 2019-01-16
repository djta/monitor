package transactions.project.util.qcloud.service;

import java.sql.Types;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.StringUtils;

import transactions.project.util.qcloud.util.QCloudTools;
import transactions.project.util.qcloud.util.Signature;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

public class SignatureTranstion extends GenericTransaction{
	public int service(Recordset inputParams) throws Throwable{
		int rc = super.service(inputParams);
		Db db = getDb();

		String signStr = "";
		// 获取帐号信息
		String query = QCloudTools.getLocalResource("/transactions/project/util/qcloud/service/sql/query-account.sql");
		Recordset rs = db.get(query);
		if( null == rs || rs.getRecordCount() <= 0 ){
			throw new Throwable("未找到云端帐号信息");
		}
		rs.first();
		String secretid = rs.getString("secretid");
		String secretkey = rs.getString("secretkey");
		
		long currenttime = System.currentTimeMillis() / 1000;
		int random = new Random().nextInt(java.lang.Integer.MAX_VALUE);
		// 判断当前签名是否有效
		String signature = !rs.isNull("signature") && null != rs.getString("signature") ? rs.getString("signature") : null; 
		Date expireTime = !rs.isNull("expire_time") && null != rs.getDate("expire_time") ? rs.getDate("expire_time") : null;
		if( null != expireTime && StringUtils.isNotBlank(signature) && expireTime.after(new Date()) ){
			signStr = signature;
		}else{
			// 生成签名
			Signature sign = new Signature();
			sign.setSecretId(secretid);
			sign.setSecretKey(secretkey);
			sign.setCurrentTime(currenttime);
			sign.setRandom(random);
			sign.setSignValidDuration(3600 * 24 * 2);
			signStr = sign.getUploadSignature();
			// 保存签名
			String update = QCloudTools.getLocalResource("/transactions/project/util/qcloud/service/sql/update-signature.sql");
			update = StringUtil.replace(update, "${signature}", signStr);
			update = StringUtil.replace(update, "${expire_time}", String.valueOf(sign.getExpireTime()));
			update = getSQL(update, rs);
			db.exec(update);
		}
		
		Recordset rsSignature = new Recordset();
		rsSignature.append("secretid", Types.VARCHAR);
		rsSignature.append("timestamp", Types.BIGINT);
		rsSignature.append("nonce", Types.INTEGER);
		rsSignature.append("signature", Types.VARCHAR);
		rsSignature.append("signaturemethod", Types.VARCHAR);
		
		rsSignature.addNew();
		rsSignature.setValue("secretid", secretid);
		rsSignature.setValue("timestamp", currenttime);
		rsSignature.setValue("nonce", random);
		rsSignature.setValue("signature", signature);
		rsSignature.setValue("signaturemethod", Signature.HMAC_ALGORITHM);
		publish("_rsSignature", rsSignature);
		return rc;
	}
}
