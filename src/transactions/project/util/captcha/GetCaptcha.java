package transactions.project.util.captcha;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import transactions.project.util.yuntongxun.sdk.YTXTools;
import dinamica.Db;
import dinamica.Recordset;

/***
 * 获取短信验证码
 * @author C
 * 2015-08-31
 */
public class GetCaptcha extends HttpServlet {
	private static final long serialVersionUID = 3345575251414251820L;
	public static final int smsTimeoutMinute = 10;	// 短信有效时间：10分钟
	
	public enum SMS_STATUS {
		UnSend(-1),Success(1),Failed(0);
		private int key;

		private SMS_STATUS(int key) {
			this.key = key;
		}

		public int getKey() {
			return key;
		}
	}

	public GetCaptcha() {
		super();
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Db db = null;
		try {
			db = transactions.project.util.ServiceTools.getDB(request);
			String mobile = request.getParameter("mobile");	// 获取手机号码
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 获取短信帐号
			String queryAccount = transactions.project.util.ServiceTools.getLocalResource("/transactions/project/util/captcha/sql/query-account.sql");
			Recordset rsAccount = db.get(queryAccount);
			while(rsAccount.next()){
				// 获取短信模板
				String queryTemplate = transactions.project.util.ServiceTools.getLocalResource("/transactions/project/util/captcha/sql/query-template.sql");
				queryTemplate = StringUtils.replace(queryTemplate, "${name}", "云通讯验证码模板");
				Recordset rsTemplate = db.get(queryTemplate);
				if( null == rsTemplate || rsTemplate.getRecordCount() <=0 ){
					break;
				}
				rsTemplate.first();
				String ytxTemplateId = rsTemplate.getString("template_content");
				// 生成验证码并写入验证码记录表
				String captchaCode = transactions.project.util.ServiceTools.createCaptchaCode();
				String insertCaptcha = transactions.project.util.ServiceTools.getLocalResource("/transactions/project/util/captcha/sql/insert-captcha.sql");
				insertCaptcha = StringUtils.replace(insertCaptcha, "${mobile}", mobile);
				insertCaptcha = StringUtils.replace(insertCaptcha, "${captcha_code}", captchaCode);
				TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
				Calendar curCal = Calendar.getInstance(timeZone);
				curCal.setTime(new Date());
				insertCaptcha = StringUtils.replace(insertCaptcha, "${created}", sdf.format(curCal.getTime()));
				curCal.add(Calendar.MINUTE, smsTimeoutMinute);
				insertCaptcha = StringUtils.replace(insertCaptcha, "${continue_time}", sdf.format(curCal.getTime()));
				insertCaptcha = transactions.project.util.ServiceTools.getSQL(insertCaptcha, null, request);
				db.exec(insertCaptcha);
				String sendTime = sdf.format(new Date());
				// 发送短信
				Map<String, String> map = YTXTools.sendSMS(rsAccount.getString("account_id"), rsAccount.getString("password"), 
						rsAccount.getString("self_key"), mobile, ytxTemplateId, new String[]{captchaCode, String.valueOf(smsTimeoutMinute)});
				String status = "";
				String responseStr = "";
				String messageId = "";
				String statusDesc = "";
				if( map != null ){
					status = ( null != map.get("status") ? map.get("status") : "0");
					messageId = ( null != map.get("message_id") ? map.get("message_id") : "");
					statusDesc = ( null != map.get("status_desc") ? map.get("status_desc") : "");
					responseStr = ( null != map.get("response") ? map.get("response") : "");
				}
				String templateContent = rsTemplate.getString("content");
				templateContent = StringUtils.replace(templateContent, "${code}", captchaCode);
				templateContent = StringUtils.replace(templateContent, "${time}", String.valueOf(smsTimeoutMinute));
				// 记录短信记录
				String insertSms = transactions.project.util.ServiceTools.getLocalResource("/transactions/project/util/captcha/sql/insert-sms.sql");
				insertSms = StringUtils.replace(insertSms, "${tuid}", "${seq:currval@seq_cc_sms}");
				insertSms = transactions.project.util.ServiceTools.getSQL(insertSms, rsAccount, request);
				insertSms = transactions.project.util.ServiceTools.getSQL(insertSms, rsTemplate, request);
				insertSms = StringUtils.replace(insertSms, "${mobile}", mobile);
				insertSms = StringUtils.replace(insertSms, "${content}", templateContent);
				insertSms = StringUtils.replace(insertSms, "${send_time}", sendTime);
				insertSms = StringUtils.replace(insertSms, "${status}", status);
				insertSms = StringUtils.replace(insertSms, "${status_desc}", statusDesc);
				insertSms = StringUtils.replace(insertSms, "${remark}", responseStr);
				insertSms = StringUtils.replace(insertSms, "${message_id}", messageId);
				db.exec(insertSms);
				response.setContentType("text/plain");
				break;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			if( db != null && null != db.getConnection() ){
				try {
					Connection conn = db.getConnection();
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
