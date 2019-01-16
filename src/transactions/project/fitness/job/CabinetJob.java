package transactions.project.fitness.job;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import transactions.project.fitness.util.ErpTools;

import com.ccms.quartz.quartz.BaseJob;
import com.ccms.quartz.quartz.JDBC;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

/***
 * 手牌+储物柜处理
 * @author C
 * 2016-06-25
 */
public class CabinetJob extends BaseJob{
	private static Logger logger = Logger.getLogger(CabinetJob.class.getName());
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
				// 1、手牌处于已占用状态的第二天自动更新为空闲
				String updateCabinettemp = getLocalResource("/transactions/project/fitness/job/sql/cabinet/update-cabinet.sql");
				db.exec(updateCabinettemp);
				
				// 2、储物柜到期提醒
				String insertMsg = getLocalResource("/transactions/project/fitness/job/sql/cabinet/insert-cabinet-msg.sql");
				String queryCabinet = getLocalResource("/transactions/project/fitness/job/sql/cabinet/query-cabinet.sql");
				queryCabinet = getSQL(queryCabinet, null);
				Recordset rsCabinet = db.get(queryCabinet);
				while( rsCabinet.next() ){
					String _insertMsg = getSQL(insertMsg, rsCabinet);
					_insertMsg = StringUtil.replace(_insertMsg, "${cabinetcode}", rsCabinet.getString("cabinetcode"));
					_insertMsg = StringUtil.replace(_insertMsg, "${enddate}", ErpTools.formatDate(rsCabinet.getDate("enddate"), "yyyy年MM月dd日"));
					
					// 给会员发送消息
					String _insertMsgCust = _insertMsg;
					_insertMsgCust = StringUtil.replace(_insertMsgCust, "${recuser}", rsCabinet.getString("custuserlogin"));
					_insertMsgCust = StringUtil.replace(_insertMsgCust, "${recusername}", rsCabinet.getString("custname"));
					db.exec(_insertMsgCust);
					
					// 给办理的员工发送消息
					String _insertMsgStaff = _insertMsg;
					_insertMsgStaff = StringUtil.replace(_insertMsgStaff, "${recuser}", rsCabinet.getString("staffuserlogin"));
					_insertMsgStaff = StringUtil.replace(_insertMsgStaff, "${recusername}", rsCabinet.getString("staffname"));
					db.exec(_insertMsgStaff);
					
					// 给前台发送消息
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
