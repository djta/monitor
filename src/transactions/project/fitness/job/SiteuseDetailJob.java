package transactions.project.fitness.job;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccms.quartz.quartz.BaseJob;
import com.ccms.quartz.quartz.JDBC;

import dinamica.Db;
import dinamica.Recordset;

/**
 * 场地预约根据时间更改状态（订单限制付款时间，超时自动取消）
 * @author Administrator
 *
 */
public class SiteuseDetailJob extends BaseJob{
	private static Logger logger = Logger.getLogger(SiteuseDetailJob.class.getName());
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

				String basepath = "/transactions/project/fitness/job/sql/site/";
				//查询所有的俱乐部
				String queryorg = getLocalResource(basepath + "query-org.sql");
				Recordset rsorg = db.get(queryorg);
				//循环俱乐部
				while(rsorg.next()){
					//查询场地预约保留时间
					String querysitedate = getLocalResource(basepath + "query-sitedate.sql");
					querysitedate = getSQL(querysitedate, rsorg);
					Recordset rssitedate = db.get(querysitedate);
					rssitedate.next();
					
					//订单限制付款时间，超时自动取消
					String update = getLocalResource(basepath + "update.sql");
					update = getSQL(update, rsorg);
					update = StringUtils.replaceOnce(update, "${fld:sitedate_value}"
							, "'"+rssitedate.getString("sitedate_value")+" min'");
					
					db.exec(update);
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
	
	public static String formatDouble(double d) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(d);
    }
}
