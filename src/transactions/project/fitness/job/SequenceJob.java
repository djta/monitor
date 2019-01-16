package transactions.project.fitness.job;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccms.quartz.quartz.BaseJob;
import com.ccms.quartz.quartz.JDBC;

import dinamica.Db;

/***
 * 序列重置Job
 * @author C
 * 2016-10-13
 */
public class SequenceJob extends BaseJob{
	private static Logger logger = Logger.getLogger(SequenceJob.class.getName());
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
				conn.setAutoCommit(true);
				Db db = new Db(conn);
				
				// 合同序列（按天重置）
				String basepath = "/transactions/project/fitness/job/sql/seq/";
				String updateContractSeq = getLocalResource(basepath + "update-contract-seq.sql");
				db.exec(updateContractSeq);
				
				// 资源序列（按年重置）
				SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
				SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
				TimeZone zone = TimeZone.getTimeZone("GMT+8");
				Calendar cal = Calendar.getInstance(zone);
				Date currentDate = cal.getTime();
				String currentMonth = sdfMonth.format(currentDate);
				String currentDay = sdfDay.format(currentDate);
				if( "01".equals(currentMonth) && "01".equals(currentDay) ){	// 每年第一天
					String updateGuestSeq = getLocalResource(basepath + "update-guest-seq.sql");
					db.exec(updateGuestSeq);
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
