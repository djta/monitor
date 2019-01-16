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

import transactions.project.util.sms.SDKClient;
import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

/***
 * 获取短信验证码（畅卓）
 * @author C
 * 2015-09-23
 */
public class GetCaptchaClient extends HttpServlet {
	private static final long serialVersionUID = -1102342313380487589L;
	private static final int smsTimeoutMinute = 10;	// 短信有效时间：10分钟
	
	public static final String yes = "1";
	public static final String no = "0";

	public GetCaptchaClient() {
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
			TimeZone timeZone = TimeZone.getTimeZone("GMT+8");
			// 获取验证短信单号每天最大发送量
			int maxSend = this.getMaxCapatchaDailyNum(db);
			// 根据手机号码获取当天验证短信发送量
			int sendNum = this.getMobileCapatchaDailyNum(db, mobile, timeZone);
			if( sendNum < maxSend ){	// 未超过最大发送量
				// 获取短信帐号
				String queryAccount = transactions.project.util.ServiceTools.getLocalResource("/transactions/project/util/captcha/sql/query-account.sql");
				Recordset rsAccount = db.get(queryAccount);
				while(rsAccount.next()){
					// 获取短信模板
					String queryTemplate = transactions.project.util.ServiceTools.getLocalResource("/transactions/project/util/captcha/sql/query-template.sql");
					queryTemplate = StringUtils.replace(queryTemplate, "${name}", "验证码模板");
					Recordset rsTemplate = db.get(queryTemplate);
					if( null == rsTemplate || rsTemplate.getRecordCount() <=0 ){
						break;
					}
					rsTemplate.first();
					// 生成验证码并写入验证码记录表
					String captchaCode = transactions.project.util.ServiceTools.createCaptchaCode();
					String insertCaptcha = transactions.project.util.ServiceTools.getLocalResource("/transactions/project/util/captcha/sql/insert-captcha.sql");
					insertCaptcha = StringUtils.replace(insertCaptcha, "${mobile}", mobile);
					insertCaptcha = StringUtils.replace(insertCaptcha, "${captcha_code}", captchaCode);
					Calendar curCal = Calendar.getInstance(timeZone);
					curCal.setTime(new Date());
					insertCaptcha = StringUtils.replace(insertCaptcha, "${created}", sdf.format(curCal.getTime()));
					curCal.add(Calendar.MINUTE, smsTimeoutMinute);
					insertCaptcha = StringUtils.replace(insertCaptcha, "${continue_time}", sdf.format(curCal.getTime()));
					insertCaptcha = transactions.project.util.ServiceTools.getSQL(insertCaptcha, null, request);
					db.exec(insertCaptcha);
					String sendTime = sdf.format(new Date());
					// 发送短信
					String template_content = rsTemplate.getString("template_content");
					template_content = StringUtil.replace(template_content, "${code}", captchaCode);
					template_content = StringUtil.replace(template_content, "${time}", String.valueOf(smsTimeoutMinute));
					SDKClient client = new SDKClient(rsAccount.getString("account_id"), rsAccount.getString("password"));
					Map<String, String> map = client.sendSMS(mobile, template_content, sendTime, "");
					String status = no;
					String statusDesc = "";
					String responseStr = "";
					String messageId = "";
					if( map != null && ( null == map.get("error_message") || "".equals(map.get("error_message")) ) ){
						responseStr = (null != map.get("response") ? map.get("response") : null);
						if( null != map.get("returnstatus") && "success".equalsIgnoreCase(map.get("returnstatus")) ){
							status = yes;
						}
						if( null != map.get("message") && !"".equals(map.get("message")) ){
							statusDesc = map.get("message");
						}
						if( null != map.get("taskID") && !"".equals(map.get("taskID")) ){
							messageId = map.get("taskID");
						}
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

	/**
	 * 获取验证短信单号每天最大发送量
	 * @param db
	 * @return
	 * @throws Throwable
	 */
	private int getMaxCapatchaDailyNum(Db db) throws Throwable{
		String value = "";
		String queryConfig = transactions.project.util.ServiceTools.getLocalResource("/transactions/project/util/captcha/sql/query-config.sql");
		Recordset rsConfig = db.get(queryConfig);
		if( rsConfig.getRecordCount() > 0 ){
			rsConfig.first();
			value = rsConfig.getString("param_value");
			if( StringUtils.isBlank(value) ){
				value = rsConfig.getString("default_value");
			}
		}
		if( StringUtils.isBlank(value) ){
			value = "10";
		}
		return Integer.parseInt(value);
	}
	
	/**
	 * 根据手机号码获取当天验证短信发送量
	 * @param db
	 * @param mobile
	 * @param timeZone
	 * @return
	 * @throws Throwable
	 */
	private int getMobileCapatchaDailyNum(Db db, String mobile, TimeZone timeZone) throws Throwable{
		int num = 0;
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		String query = transactions.project.util.ServiceTools.getLocalResource("/transactions/project/util/captcha/sql/query-mobile.sql");
		query = StringUtils.replace(query, "${mobile}", mobile);
		Calendar cal = Calendar.getInstance(timeZone);
		cal.setTime(new Date());
		String time = sdfDate.format(cal.getTime()) + " 00:00:00";
		query = StringUtils.replace(query, "${start_time}", time);
		cal.add(Calendar.DATE, 1);
		time = sdfDate.format(cal.getTime()) + " 00:00:00";
		query = StringUtils.replace(query, "${end_time}", time);
		Recordset rs = db.get(query);
		if( rs.getRecordCount() > 0 ){
			rs.first();
			num = rs.getInteger("total");
		}
		return num;
	}
	
}
