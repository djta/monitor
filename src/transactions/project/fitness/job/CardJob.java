package transactions.project.fitness.job;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccms.quartz.quartz.BaseJob;
import com.ccms.quartz.quartz.JDBC;

import dinamica.Db;
import dinamica.Recordset;

/***
 * 卡相关处理
 * @author C
 * 2016-06-28
 */
public class CardJob extends BaseJob{
	private static Logger logger = Logger.getLogger(CardJob.class.getName());
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
				String basepath = "/transactions/project/fitness/job/sql/card/";
				// 1、会员卡自动过期
				// 时效卡过期
				db.exec(getSQL(getLocalResource(basepath + "update-invalid-type0.sql"), null));
				// 计次卡次数用完或者过期
				db.exec(getSQL(getLocalResource(basepath + "update-invalid-type1.sql"), null));
				// 基金卡两种基金加起来小于10或者过期
				db.exec(getSQL(getLocalResource(basepath + "update-invalid-type2.sql"), null));
				
				// 2、新卡自动开卡：两周内不限时启用、两月内不限时启用
				// 获取更新脚本
				String updateStart = getLocalResource(basepath + "start/update.sql");
				// 获取需要更新的数据
				String queryStart = getLocalResource(basepath + "start/query.sql");
				Recordset rsStart = db.get(getSQL(queryStart, null));
				while(rsStart.next()){
					String _update = getSQL(updateStart, rsStart);
					db.exec(_update);
				}
				
				// 3、续卡自动开卡
				// 获取更新将启用续卡的会员卡的原卡的I_ISGOON更新成-1脚本
				String updateIsgoon = getLocalResource(basepath + "ctnstart/update-isgoon.sql");
				String updateCtncard = getLocalResource(basepath + "ctnstart/update-ctncard.sql");
				/**String deleteOld = getLocalResource(basepath + "ctnstart/delete-old.sql");*/
				// 获取需要更新的数据
				String queryCtnstart = getLocalResource(basepath + "ctnstart/query.sql");
				Recordset rsCtnstart = db.get(getSQL(queryCtnstart, null));
				while(rsCtnstart.next()){
					db.beginTrans();
					// 将启用续卡的会员卡的原卡的I_ISGOON更新成-1
					String _updateIsgoon = getSQL(updateIsgoon, rsCtnstart);
					db.exec(_updateIsgoon);
					// 按照启用方式启用续卡，同时将各种基金、密码、操课卡时间转移过来
					String _updateCtncard = getSQL(updateCtncard, rsCtnstart);
					db.exec(_updateCtncard);
					// 删除I_ISGOON=1的原卡记录
					/**String _deleteOld = getSQL(deleteOld, rsCtnstart);
					db.exec(_deleteOld);*/
					db.commit();
				}
				
				// 4、停卡到期自动开卡
				String updateStops = getLocalResource(basepath + "stops/update.sql");
				String updateStopsCard = getLocalResource(basepath + "stops/update-card.sql");
				String insertStopsMsg = getLocalResource(basepath + "stops/insert-message.sql");
				String insertStopsOpera = getLocalResource(basepath + "stops/insert-operatelog.sql");
				// 获取需要更新的数据
				String queryStops = getLocalResource(basepath + "stops/query.sql");
				Recordset rsStops = db.get(getSQL(queryStops, null));
				while(rsStops.next()){
					db.beginTrans();
					// 更新停卡记录表
					String _update = getSQL(updateStops, rsStops);
					db.exec(_update);
					// 更新卡表
					String _updateCard = getSQL(updateStopsCard, rsStops);
					db.exec(_updateCard);
					// 新增消息
					String _insertMsg = getSQL(insertStopsMsg, rsStops);
					db.exec(_insertMsg);
					// 新增操作日志
					String _insertOper = getSQL(insertStopsOpera, rsStops);
					db.exec(_insertOper);
					db.commit();
				}
				
				//初次训练启用根据卡类型的开卡期限进行开卡
				String queryFixed = getLocalResource(basepath + "fixed/query.sql");
				Recordset rsFixed = db.get(getSQL(queryFixed, null));
				while(rsFixed.next()){
					//开卡
					String updatecardstatus = getLocalResource(basepath + "fixed/update-cardstartenddate.sql");
					String _update = getSQL(updatecardstatus, rsFixed);
					db.exec(_update);
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
