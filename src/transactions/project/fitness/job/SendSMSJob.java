package transactions.project.fitness.job;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import transactions.project.util.yuntongxun.sdk.YTXTools;

import com.ccms.quartz.quartz.BaseJob;
import com.ccms.quartz.quartz.JDBC;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

/****
 * 发送短信
 * @author C
 * 2016-08-05
 */
public class SendSMSJob extends BaseJob{
	private static Logger logger = Logger.getLogger(SendSMSJob.class.getName());
	private static Boolean IS_FINISH = true;
	private static byte[] lock = new byte[0];
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException{
		//执行方法之前先判断该线程是否执行完成
		if(IS_FINISH == false){
			super.beforeExecute(arg0, "ignore");
			return;
		}
		String tuid = super.beforeExecute(arg0, "");
		String error = "";
		//加锁，以免上一个线程尚未执行完，下一个就执行了
		synchronized(lock){
			IS_FINISH = false;
			Connection conn = null;
			try {
				conn = JDBC.getConnection();
				Db db = new Db(conn);
				String basepath = "/transactions/project/fitness/job/sql/sms/";
				String querySMS = getLocalResource(basepath + "query-sms.sql");
				String updateSMS = getLocalResource(basepath + "update-sms.sql");
				// 获取短信帐号列表
				String queryAccount = getLocalResource(basepath + "query-account.sql");
				Recordset rsAccount = db.get(queryAccount);
				while(rsAccount.next()){
					String account_id = rsAccount.getString("account_id");
					String password = rsAccount.getString("password");
					String self_key = rsAccount.getString("self_key");
					if( StringUtils.isBlank(account_id) || StringUtils.isBlank(password) || StringUtils.isBlank(self_key) ){
						continue;
					}
					// 获取待发送短信列表
					String _querySMS = getSQL(querySMS, rsAccount);
					Recordset rsSMS = db.get(_querySMS);
					while(rsSMS.next()){
						String receiver = rsSMS.getString("receiver");
						String ytxTemplateId = (rsSMS.isNull("template_content") ? "" : rsSMS.getString("template_content"));
						String custname = (rsSMS.isNull("name") ? "" : rsSMS.getString("name"));
						String[] params = new String[]{custname};
						// 发送短信
						Map<String, String> map = YTXTools.sendSMS(account_id, password, self_key, receiver, ytxTemplateId, params);
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
						// 更新发送结果
						String _updateSMS = getSQL(updateSMS, rsSMS);
						_updateSMS = StringUtil.replace(_updateSMS, "${status}", status);
						_updateSMS = StringUtil.replace(_updateSMS, "${status_desc}", statusDesc);
						_updateSMS = StringUtil.replace(_updateSMS, "${remark}", responseStr);
						_updateSMS = StringUtil.replace(_updateSMS, "${message_id}", messageId);
						db.exec(_updateSMS);
					}
				}
			}catch (Throwable e) {
				error = e.getMessage();
				logger.error(e);
				e.printStackTrace();
			}finally{
				if(conn != null){
					// 关闭连接
					try {
						conn.close();
					} catch (SQLException e) {
						logger.error(e);
					}
				}
				IS_FINISH = true;
				super.afterExecute(arg0, tuid, error);
			}
		}
    }
}
