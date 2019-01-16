package com.ccms.workflow.quartz;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccms.context.InitializerServlet;
import com.ccms.quartz.quartz.BaseJob;
import com.ccms.quartz.quartz.JDBC;

import dinamica.Db;

public class AuthUpdateScheduleJob extends BaseJob {
	private static Logger logger = Logger.getLogger(AuthUpdateScheduleJob.class.getName());
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
			Connection connSyn = null;
			Connection conn = null;
			try {
				String synDataSource = InitializerServlet.getContext().getInitParameter("def-datasource");
				connSyn = JDBC.getConnection(null, synDataSource);
				conn = JDBC.getConnection();
				Db db = new Db(conn);
				//更新授权状态与更新用户授权信息
				db.beginTrans();
				String updateAuthStatus = getLocalResource("/com/ccms/workflow/sql/auth/update_auth_status.sql");
				String updateStaffAlias = getLocalResource("/com/ccms/workflow/sql/auth/update_staff_auth.sql");
				String clearStaffAlias = getLocalResource("/com/ccms/workflow/sql/auth/clear_staff_auth.sql");
				db.exec(getSQL(updateAuthStatus, null));
				db.exec(getSQL(updateStaffAlias, null));
				db.exec(getSQL(clearStaffAlias, null));
				db.commit();
			}catch (Throwable e) {
				error = e.getMessage();
				logger.error(e);
				e.printStackTrace();
			}finally{
				if(connSyn != null){
					// 关闭连接
					try {
						connSyn.close();
					} catch (SQLException e) {
						logger.error(e);
					}
				}
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
