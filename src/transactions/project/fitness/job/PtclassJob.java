package transactions.project.fitness.job;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.tools.ant.util.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import transactions.project.fitness.util.ErpTools;

import com.ccms.quartz.quartz.BaseJob;
import com.ccms.quartz.quartz.JDBC;

import dinamica.Db;
import dinamica.Recordset;

/***
 * 私教课相关处理
 * @author C
 * 2018-03-14
 */
public class PtclassJob extends BaseJob{
	private static Logger logger = Logger.getLogger(PtclassJob.class.getName());
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
				// 1、根据参数配置，判断爽约PT课，是否第二天强行刷掉
				// 获取刷掉私教课脚本
				String insertPtlog = getLocalResource("/transactions/project/fitness/job/sql/ptclass/insert-ptlog.sql");
				String updatePtprepare = getLocalResource("/transactions/project/fitness/job/sql/ptclass/update-ptprepare.sql");
				String updatePtrest = getLocalResource("/transactions/project/fitness/job/sql/ptclass/update-ptrest.sql");
				// 获取需要爽约私教预约数据脚本
				String queryPtclass = getLocalResource("/transactions/project/fitness/job/sql/ptclass/query.sql");
				// 获取参数配置脚本
				String queryConfig = getLocalResource("/transactions/project/fitness/job/sql/query-config.sql");
				// 获取俱乐部列表
				String queryOrg =  getLocalResource("/transactions/project/fitness/job/sql/query-org.sql");
				Recordset rsOrg = db.get(queryOrg);
				while(rsOrg.next()){
					// 获取配置信息
					String _queryConfig = getSQL(queryConfig, rsOrg);
					_queryConfig = StringUtils.replace(_queryConfig, "${category}", "IsPrintPassPrepare");
					Recordset rsConfig = db.get(_queryConfig);
					if( null == rsConfig || rsConfig.getRecordCount() == 0 ){
						continue;
					}
					rsConfig.first();
					String paramValue = "";
					if( !rsConfig.isNull("param_value") && null != rsConfig.getValue("param_value") ){
						paramValue = rsConfig.getString("param_value");
					}
					
					// 获取需要爽约私教预约数据
					String _queryPtclass = getSQL(queryPtclass, rsOrg);
					Recordset rsPtclass = db.get(_queryPtclass);
					while(rsPtclass.next()){
						db.beginTrans();
						if( ErpTools.yes.equals(paramValue) ){	// 设置爽约课强行刷课时，第二天自动强行刷掉私教课
							String _insertPtlog= getSQL(insertPtlog, rsPtclass);
							db.exec(_insertPtlog);
							String _updatePtrest = getSQL(updatePtrest, rsPtclass);
							db.exec(_updatePtrest);
						}
						// 预约状态置为爽约
						String _updatePtprepare = getSQL(updatePtprepare, rsPtclass);
						db.exec(_updatePtprepare);
						db.commit();
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
