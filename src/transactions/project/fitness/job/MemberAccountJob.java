package transactions.project.fitness.job;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccms.quartz.quartz.BaseJob;
import com.ccms.quartz.quartz.JDBC;

import dinamica.Base64;
import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

/***
 * 帐号处理Job
 * @author C
 * 2016-08-13
 */
public class MemberAccountJob extends BaseJob{
	private static Logger logger = Logger.getLogger(MemberAccountJob.class.getName());
	private static Boolean IS_FINISH = true;
	private static byte[] lock = new byte[0];

//	private static final int STATUS_off = 0;	// 离职
	private static final int STATUS_validate = 1;	// 有效
	private static final int STATUS_invalidate = 2;	// 无效
	private static final int enabled = 1;	// 已启用
	private static final int disabled = 0;	// 已禁用
	private static final String DEFAULT_pwd = "123456";	// 默认密码
	
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
				// 会员帐号处理
				String basepath = "/transactions/project/fitness/job/sql/member/account/";
				String updateStaff = this.getsql(basepath + "update-staff.sql", null);	// 更新hr_staff状态
				String updateUser = this.getsql(basepath + "update-user.sql", null);	// 更新security.s_user状态
				String insertStaff = this.getsql(basepath + "insert-staff.sql", null);	// 写入hr_staff
				String insertUser = this.getsql(basepath + "insert-user.sql", null);	// 写入security.s_user
				String updateCustomer = this.getsql(basepath + "update-customer.sql", null);	// 更新cc_customer.user_id
				// 1、需禁用的帐号
				String disableAccount = this.getsql(basepath + "query-disable.sql", null);
				Recordset rsDisable = db.get(disableAccount);
				while(rsDisable.next()){
					// 帐号更新
					String _updateStaff = StringUtil.replace(updateStaff, "${status}", String.valueOf(STATUS_invalidate));
					_updateStaff = getSQL(_updateStaff, rsDisable);
					String _updateUser = StringUtil.replace(updateUser, "${enabled}", String.valueOf(disabled));
					_updateUser = getSQL(_updateUser, rsDisable);
					db.beginTrans();
					db.exec(_updateStaff);
					db.exec(_updateUser);
					db.commit();
				}
				// 2、需要启用的帐号
				String enableAccount = this.getsql(basepath + "query-enable.sql", null);
				Recordset rsEnable = db.get(enableAccount);
				while(rsEnable.next()){
					// 帐号更新
					String _updateStaff = StringUtil.replace(updateStaff, "${status}", String.valueOf(STATUS_validate));
					_updateStaff = getSQL(_updateStaff, rsEnable);
					String _updateUser = StringUtil.replace(updateUser, "${enabled}", String.valueOf(enabled));
					_updateUser = getSQL(_updateUser, rsEnable);
					db.beginTrans();
					db.exec(_updateStaff);
					db.exec(_updateUser);
					db.commit();
				}
				java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
				// 3、需要新增的帐号
				String queryUser = this.getsql(basepath + "query-user.sql", null);
				String newAccount = this.getsql(basepath + "query-new.sql", null);
				Recordset rsNew = db.get(newAccount);
				while(rsNew.next()){
					// 判断帐号是否已占用
					String _queryUser = getSQL(queryUser, rsNew);
					Recordset rsUser = db.get(_queryUser);
					if( null != rsUser && rsUser.getRecordCount() > 0 ){
						continue;
					}
					
					// 生成密码
					String userlogin = rsNew.getString("userlogin");
					byte[] b = (userlogin + ":" + DEFAULT_pwd).getBytes();
					byte[] hash = md.digest(b);
					String pwd = Base64.encodeToString(hash, true);
					// 帐号新增
					String _insertStaff = StringUtil.replace(insertStaff, "${status}", String.valueOf(STATUS_validate));
					_insertStaff = getSQL(_insertStaff, rsNew);
					String _insertUser = StringUtil.replace(insertUser, "${enabled}", String.valueOf(enabled));
					_insertUser = StringUtil.replace(_insertUser, "${passwd}", pwd);
					_insertUser = getSQL(_insertUser, rsNew);
					String _updateCustomer= getSQL(updateCustomer, rsNew);
					db.beginTrans();
					db.exec(_insertStaff);
					db.exec(_insertUser);
					db.exec(_updateCustomer);
					db.commit();
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
	
	private String getsql(String path, Recordset rs) throws Throwable{
		String sql = getLocalResource(path);
		sql = getSQL(sql, rs);
		return sql;
	}
}
