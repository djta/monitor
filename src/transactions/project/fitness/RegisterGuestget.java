package transactions.project.fitness;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import transactions.project.fitness.util.ErpTools;
import transactions.project.util.yuntongxun.sdk.YTXTools;
import dinamica.Base64;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;
import dinamica.StringUtil;

/***
 * 资源宝注册试用
 * @author C
 * 2018-03-06
 */

public class RegisterGuestget extends GenericTableManager {
	private final static String ACCOUNT_BEGIN = "XF";
	private final static int step = 239;
	
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String insert = getResource("insert.sql");
		String insertUser = getResource("insert-user.sql");
		String insertStaff = getResource("insert-staff.sql");
		String insertSkill = getResource("insert-skills.sql");
		String insertPasslog = getResource("insert-passlog.sql");

		Db db = getDb();
		// 生成注册帐号
		int userid = this.getSeq(db, "security.seq_user");
		String password = ErpTools.getStringSimpleRandom(6);	// 生成6位随机密码
		String userlogin = ACCOUNT_BEGIN + ErpTools.lpad(String.valueOf(userid + step), "0", 6);

		java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
		byte[] b = (userlogin + ":" + password).getBytes();
		byte[] hash = md.digest(b);
		String pwd = Base64.encodeToString( hash, true );
		
		String name = inputParams.getString("name");
		String mobile = inputParams.getString("mobile");
		String demouri = getConfig().getConfigValue("demo-uri", "");
		String appname = getConfig().getConfigValue("appname", "");
		
		insert = this.replaceSqlParams(insert, inputParams, userid, userlogin, password);
		db.exec(insert);
		insertUser = this.replaceSqlParams(insertUser, inputParams, userid, userlogin, pwd);
		db.exec(insertUser);
		insertStaff = this.replaceSqlParams(insertStaff, inputParams, userid, userlogin, pwd);
		db.exec(insertStaff);
		insertSkill = this.replaceSqlParams(insertSkill, inputParams, userid, userlogin, pwd);
		db.exec(insertSkill);
		insertPasslog = this.replaceSqlParams(insertPasslog, inputParams, userid, userlogin, pwd);
		db.exec(insertPasslog);

		// 获取短信帐号
		String queryAccount = getLocalResource("/transactions/project/util/captcha/sql/query-account.sql");
		Recordset rsAccount = db.get(queryAccount);
		while(rsAccount.next()){
			// 获取短信模板
			String queryTemplate = getLocalResource("/transactions/project/util/captcha/sql/query-template.sql");
			queryTemplate = StringUtils.replace(queryTemplate, "${name}", "云通讯试用通知");
			Recordset rsTemplate = db.get(queryTemplate);
			if( null == rsTemplate || rsTemplate.getRecordCount() <=0 ){
				break;
			}
			rsTemplate.first();
			String ytxTemplateId = rsTemplate.getString("template_content");
			// 发送短信
			Map<String, String> map = YTXTools.sendSMS(rsAccount.getString("account_id"), rsAccount.getString("password"), 
					rsAccount.getString("self_key"), mobile, ytxTemplateId, new String[]{name, appname, demouri, userlogin, password});
			
			/**【芯复科技】芯复科技尊敬的${name}，您好！感谢申请试用“${appname}”，电脑端Demo页面:${demouri}；移动端请关注公众号：德胜信诚。您的账号是${userlogin},密码是${password}。
			有任何问题请联系contact@gymjam.cn。祝您工作愉快！*/
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
			templateContent = StringUtils.replace(templateContent, "${name}", name);
			templateContent = StringUtils.replace(templateContent, "${appname}", appname);
			templateContent = StringUtils.replace(templateContent, "${demouri}", demouri);
			templateContent = StringUtils.replace(templateContent, "${userlogin}", userlogin);
			templateContent = StringUtils.replace(templateContent, "${password}", password);
			// 记录短信记录
			String insertSms = getLocalResource("/transactions/project/util/captcha/sql/insert-sms.sql");
			insertSms = StringUtils.replace(insertSms, "${tuid}", "${seq:nextval@seq_cc_sms}");
			insertSms = getSQL(insertSms, rsAccount);
			insertSms = getSQL(insertSms, rsTemplate);
			insertSms = StringUtils.replace(insertSms, "${mobile}", mobile);
			insertSms = StringUtils.replace(insertSms, "${content}", templateContent);
			String sendTime = sdf.format(new Date());
			insertSms = StringUtils.replace(insertSms, "${send_time}", sendTime);
			insertSms = StringUtils.replace(insertSms, "${status}", status);
			insertSms = StringUtils.replace(insertSms, "${status_desc}", statusDesc);
			insertSms = StringUtils.replace(insertSms, "${remark}", responseStr);
			insertSms = StringUtils.replace(insertSms, "${message_id}", messageId);
			db.exec(insertSms);
			break;
		}
		return rc;
	}
	
	private Integer getSeq(Db db, String seqName) throws Throwable{
		String querySeq = "SELECT nextval('${seq_name}') AS seq";
		querySeq = StringUtils.replace(querySeq, "${seq_name}", seqName);
		querySeq = getSQL(querySeq, null);
		Recordset rs = db.get(querySeq);
		rs.first();
		return rs.getInteger("seq");
	}
	
	private String replaceSqlParams(String insert, Recordset inputParams, int userid, String userlogin, String pwd) throws Throwable{
		insert = getSQL(insert, inputParams);
		insert = StringUtil.replace(insert, "${user_id}", String.valueOf(userid));
		insert = StringUtil.replace(insert, "${userlogin}", userlogin);
		insert = StringUtil.replace(insert, "${passwd}", pwd);
		return insert;
	}
}
