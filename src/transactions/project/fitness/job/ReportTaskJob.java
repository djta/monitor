package transactions.project.fitness.job;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.tools.ant.util.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import transactions.project.fitness.util.ErpTools;

import com.ccms.quartz.quartz.BaseJob;
import com.ccms.quartz.quartz.JDBC;

import dinamica.Db;
import dinamica.Recordset;
import dinamica.StringUtil;

/***
 * 任务完成情况处理（当天处理前一天、每月第一天处理上一个月）
 * @author C
 * 2018-03-14
 */
public class ReportTaskJob extends BaseJob{
	private static Logger logger = Logger.getLogger(ReportTaskJob.class.getName());
	private static Boolean IS_FINISH = true;
	private static byte[] lock = new byte[0];
	
	private static boolean isTargetFinish = true;
	
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
				String basepath = "/transactions/project/fitness/job/sql/report/task/";
				
				// 计算本月天数
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.DATE, 1);	//把日期设置为当月第一天 
				cal.roll(Calendar.DATE, -1);	//日期回滚一天，也就是最后一天 
				int totaldays = cal.get(Calendar.DATE);
				Date currentDate = new Date();
				String targetYear = ErpTools.formatDate(currentDate, "yyyy");
				String targetMonth = ErpTools.formatDate(currentDate, "MM");
				String currentDateStr = ErpTools.formatDate(currentDate, "yyyy-MM-dd");

				String queryConfig = getLocalResource(basepath + "query-config.sql");
				String queryTask = getLocalResource(basepath + "query-task.sql");
				String insertTaskDaily = getLocalResource(basepath + "insert-task-daily.sql");
				String insertTaskMonthly = getLocalResource(basepath + "insert-task-monthly.sql");
				
				String queryNewGuest = getLocalResource(basepath + "query-total-newguest.sql");
				String queryFollow = getLocalResource(basepath + "query-total-follow.sql");
				String queryPrepare = getLocalResource(basepath + "query-total-prepare.sql");
				String queryVisit = getLocalResource(basepath + "query-total-visit.sql");
				String queryNewContract = getLocalResource(basepath + "query-total-newcontract.sql");
				String queryNewContractFee = getLocalResource(basepath + "query-total-newcontract-fee.sql");
				String queryCall = getLocalResource(basepath + "query-total-call.sql");
				String queryMcCall = getLocalResource(basepath + "query-total-mccall.sql");
				String queryPtCall = getLocalResource(basepath + "query-total-ptcall.sql");
				String queryTest = getLocalResource(basepath + "query-total-test.sql");
				String queryTestClass = getLocalResource(basepath + "query-total-testclass.sql");
				String queryClass = getLocalResource(basepath + "query-total-class.sql");
				String querySite = getLocalResource(basepath + "query-total-site.sql");
				
				// 资源获取量、跟进量、预约量、实际到访量、成单量、回访量、回访预约会籍量、回访预约私教量、体测量、体验课上课量、总上课量、场开量
				String[] intTargetList = {"guest_target", "follow_target", "prepare_target", "visit_target", "ordernum_target", "call_target", 
										"call_mc_target", "call_pt_target", "test_target", "unpayclass_target", "allclass_target", "site_target"};
				String[] intSqlList = {queryNewGuest, queryFollow, queryPrepare, queryVisit, queryNewContract, queryCall, 
										queryMcCall, queryPtCall, queryTest, queryTestClass, queryClass, querySite};
				// 成单额
				String[] doubleTargetList = {"orderfee_target"};
				String[] doubleSqlList = {queryNewContractFee};
				
				// 1、获取俱乐部列表
				Recordset rsOrg = ErpTools.getOrgList(db);
				while( rsOrg.next() ){
					// 2、获取任务类型
					String _queryConfig = getSQL(queryConfig, rsOrg);
					Recordset rsConfig = db.get(_queryConfig);
					if( null == rsConfig || rsConfig.getRecordCount() <= 0 ){
						continue;
					}
					rsConfig.first();
					if( rsConfig.isNull("param_value") || null == rsConfig.getString("param_value") ){
						continue;
					}
					String taskType = rsConfig.getString("param_value");
					if( !("0".equals(taskType) || "1".equals(taskType)) ){	/** 0按岗位、1按分组 */
						continue;
					}
					
					// 3、根据岗位/分组获取员工任务
					String _queryTask = getSQL(queryTask, rsOrg);
					_queryTask = StringUtil.replace(_queryTask, "${target_year}", targetYear);
					_queryTask = StringUtil.replace(_queryTask, "${target_month}", targetMonth);
					Recordset rsTask = db.get(_queryTask);
					while( rsTask.next() ){
						isTargetFinish = true;
						String _insertDaily = getSQL(insertTaskDaily, rsTask);
						_insertDaily = StringUtil.replace(_insertDaily, "${target_date}", currentDateStr);
						
						for(int i = 0; i < intTargetList.length; i++){
							_insertDaily = this.replaceIntegerTotal(db, rsTask, intTargetList[i], intSqlList[i], _insertDaily, true, totaldays, currentDateStr, currentDateStr);
						}
						for(int i = 0; i < doubleTargetList.length; i++){
							_insertDaily = this.replaceDoubleTotal(db, rsTask, doubleTargetList[i], doubleSqlList[i], _insertDaily, true, totaldays, currentDateStr, currentDateStr);
						}
						
						_insertDaily = StringUtil.replace(_insertDaily, "${isfinish}", isTargetFinish ? "1" : "0");
						db.exec(_insertDaily);
					}
					
					// 4、每月第一天计算上月的任务完成情况
					cal.setTime(currentDate);
					if( 1 == cal.get(Calendar.DAY_OF_MONTH) ){
						cal.add(Calendar.DATE, -1);
						String fdate = ErpTools.formatDate(cal.getTime(), "yyyy-MM-01");
						String tdate = ErpTools.formatDate(cal.getTime(), "yyyy-MM-dd");
						targetYear = ErpTools.formatDate(cal.getTime(), "yyyy");
						targetMonth = ErpTools.formatDate(cal.getTime(), "MM");
						_queryTask = getSQL(queryTask, rsOrg);
						_queryTask = StringUtil.replace(_queryTask, "${target_year}", targetYear);
						_queryTask = StringUtil.replace(_queryTask, "${target_month}", targetMonth);
						rsTask = db.get(_queryTask);
						while( rsTask.next() ){
							isTargetFinish = true;
							String _insert = getSQL(insertTaskMonthly, rsTask);
							_insert = StringUtil.replace(_insert, "${target_year}", targetYear);
							_insert = StringUtil.replace(_insert, "${target_month}", targetMonth);
							
							for(int i = 0; i < intTargetList.length; i++){
								_insert = this.replaceIntegerTotal(db, rsTask, intTargetList[i], intSqlList[i], _insert, false, totaldays, fdate, tdate);
							}
							for(int i = 0; i < doubleTargetList.length; i++){
								_insert = this.replaceDoubleTotal(db, rsTask, doubleTargetList[i], doubleSqlList[i], _insert, false, totaldays, fdate, tdate);
							}
							
							_insert = StringUtil.replace(_insert, "${isfinish}", isTargetFinish ? "1" : "0");
							db.exec(_insert);
						}
						
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
	
	private String replaceDoubleTotal(Db db, Recordset rsTask, String key, String query, String insert, 
			boolean isDaily, int totaldays, String fdate, String tdate) throws Throwable{
		Double target = ErpTools.getDoubleValue(rsTask, key);
		if( null != target && target.doubleValue() > 0d ){
			Double targetFinish = this.getDoubleTotal(db, query, rsTask, fdate, tdate);
			targetFinish = (null == targetFinish ? 0d : targetFinish);
			if( isDaily ){
				target = target/totaldays;
			}
			insert = StringUtil.replace(insert, "${"+key+"}", String.valueOf(target));
			insert = StringUtil.replace(insert, "${"+key+"_finish}", String.valueOf(targetFinish));
			isTargetFinish = isTargetFinish && target.doubleValue() <= targetFinish.doubleValue();
		}else{
			insert = StringUtil.replace(insert, "${"+key+"}", "NULL");
			insert = StringUtil.replace(insert, "${"+key+"_finish}", "NULL");
		}
		return insert;
	}
	
	private String replaceIntegerTotal(Db db, Recordset rsTask, String key, String query, String insert, 
			boolean isDaily, int totaldays, String fdate, String tdate) throws Throwable{
		Integer target = ErpTools.getIntegerValue(rsTask, key);
		if( null != target && target.intValue() > 0 ){
			int targetFinish = this.getIntegerTotal(db, query, rsTask, fdate, tdate);
			if( isDaily ){
				Double _target = Math.ceil(target*1.00/totaldays);
				target = _target.intValue();
			}
			insert = StringUtil.replace(insert, "${"+key+"}", String.valueOf(target));
			insert = StringUtil.replace(insert, "${"+key+"_finish}", String.valueOf(targetFinish));
			isTargetFinish = isTargetFinish && target.intValue() <= targetFinish;
		}else{
			insert = StringUtil.replace(insert, "${"+key+"}", "NULL");
			insert = StringUtil.replace(insert, "${"+key+"_finish}", "NULL");
		}
		return insert;
	}
	
	private int getIntegerTotal(Db db, String sql, Recordset rsData, String fdate, String tdate) throws Throwable{
		String query = getSQL(sql, rsData);
		query = StringUtils.replace(query, "${fdate}", fdate);
		query = StringUtils.replace(query, "${tdate}", tdate);
		Recordset rs = db.get(query);
		rs.first();
		return rs.getInt("total");
	}
	
	private Double getDoubleTotal(Db db, String sql, Recordset rsData, String fdate, String tdate) throws Throwable{
		String query = getSQL(sql, rsData);
		query = StringUtils.replace(query, "${fdate}", fdate);
		query = StringUtils.replace(query, "${tdate}", tdate);
		Recordset rs = db.get(query);
		rs.first();
		return rs.isNull("total") ? null : rs.getDouble("total");
	}
}
