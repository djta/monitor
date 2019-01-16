package transactions.project.util.qcloud.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import transactions.project.util.qcloud.util.QCloudTools;
import transactions.project.util.qcloud.util.Signature;
import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

/***
 * 获取签名
 * @author C 
<servlet>
	<description></description>
	<display-name>QSignatureService</display-name>
	<servlet-name>QSignatureService</servlet-name>
	<servlet-class>transactions.project.util.qcloud.service.QSignatureService</servlet-class>
</servlet>
<servlet-mapping>
	<servlet-name>QSignatureService</servlet-name>
	<url-pattern>/api/qcloud/getsignature</url-pattern>
</servlet-mapping>
 */
public class QSignatureService extends HttpServlet {
	private static final long serialVersionUID = -4532152171667821418L;

	private static Logger logger = Logger.getLogger(QSignatureService.class.getName());

	public QSignatureService() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Db db = null;
		String signStr = "";
		try {
			db = QCloudTools.getDB(request);
			// 获取帐号信息
			String query = QCloudTools.getLocalResource("/transactions/project/util/qcloud/service/sql/query-account.sql");
			Recordset rs = db.get(query);
			if( null == rs || rs.getRecordCount() <= 0 ){
				throw new Throwable("未找到云端帐号信息");
			}
			rs.first();
			String secretid = rs.getString("secretid");
			String secretkey = rs.getString("secretkey");
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
				sign.setCurrentTime(System.currentTimeMillis() / 1000);
				sign.setRandom(new Random().nextInt(java.lang.Integer.MAX_VALUE));
				sign.setSignValidDuration(3600 * 24 * 2);
				signStr = sign.getUploadSignature();
				// 保存签名
				String update = QCloudTools.getLocalResource("/transactions/project/util/qcloud/service/sql/update-signature.sql");
				update = StringUtil.replace(update, "${signature}", signStr);
				update = StringUtil.replace(update, "${expire_time}", String.valueOf(sign.getExpireTime()));
				update = QCloudTools.getSQL(update, rs, request);
				db.exec(update);
			}
		} catch (Throwable e) {
			logger.error(e);
		} finally {
			this.closeDb(db);	// 关闭数据库连接
			try {
				JSONObject resutlJson = new JSONObject();
				resutlJson.put("signature", signStr);
				QCloudTools.responseOut(response, resutlJson);
			} catch (JSONException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 关闭数据库连接
	 * @param db
	 */
	private void closeDb(Db db) {
		if (db != null && null != db.getConnection()) {
			try {
				Connection conn = db.getConnection();
				conn.close();
			} catch (SQLException e) {
				logger.error(e);
			}
		}
	}
}
