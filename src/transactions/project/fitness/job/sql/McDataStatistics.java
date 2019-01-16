package transactions.project.fitness.job.sql;

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

/***
 * 会籍部个人数据统计
 * @author Administrator
 *
 */
public class McDataStatistics extends BaseJob{
	private static Logger logger = Logger.getLogger(McDataStatistics.class.getName());
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

				String basepath = "/transactions/project/fitness/job/sql/mcdata/statistics/";
				//查询所有的俱乐部
				String queryorg = getLocalResource(basepath + "query-org.sql");
				Recordset rsorg = db.get(queryorg);
				//循环俱乐部
				while(rsorg.next()){
					//根据俱乐部查询会籍
					String querymc = getLocalResource(basepath + "query-mc.sql");
					querymc = getSQL(querymc, rsorg);
					Recordset rsmc = db.get(querymc);
					//循环会籍
					while(rsmc.next()){
						String querymcdatastatistics = getLocalResource(basepath + "query-mc_data_statistics.sql");
						querymcdatastatistics = getSQL(querymcdatastatistics, rsmc);
						querymcdatastatistics = getSQL(querymcdatastatistics, rsorg);
						Recordset rsdata = db.get(querymcdatastatistics);
						rsdata.first();
						//查询会籍上月是否已统计
						if(Integer.parseInt(rsdata.getString("num"))==0){
							//统计上月会籍部个人数据统计
							String querymctarget = getLocalResource(basepath + "query-target.sql");
							querymctarget = getSQL(querymctarget, rsorg);
							querymctarget = getSQL(querymctarget, rsmc);
							Recordset rsmctarget = db.get(querymctarget);
							String rworderfee_target = "0";
							if(rsmctarget.getRecordCount()>0){
								rsmctarget.first();
								//会籍任务额
								rworderfee_target = rsmctarget.getString("orderfee_target");
							}
							//完成额
							String queryorderfee_target = getLocalResource(basepath + "query-orderfee_target.sql");
							queryorderfee_target = getSQL(queryorderfee_target, rsorg);
							queryorderfee_target = getSQL(queryorderfee_target, rsmc);
							Recordset rsorderfee_target = db.get(queryorderfee_target);
							String orderfee_target = "0";
							if(rsorderfee_target.getRecordCount()>0){
								rsorderfee_target.first();
								//会籍任务额
								orderfee_target = rsorderfee_target.getString("orderfee_target");
								if(orderfee_target==null){
									orderfee_target = "0";
								}
							}
							//陌生接待
							String querymsnum = getLocalResource(basepath + "query-msnum.sql");
							querymsnum = getSQL(querymsnum, rsorg);
							querymsnum = getSQL(querymsnum, rsmc);
							Recordset rsmsnum = db.get(querymsnum);
							rsmsnum.first();
							String msnum = rsmsnum.getString("msnum");
							
							//陌生成交
							String querymsvisitnum = getLocalResource(basepath + "query-msvisitnum.sql");
							querymsvisitnum = getSQL(querymsvisitnum, rsorg);
							querymsvisitnum = getSQL(querymsvisitnum, rsmc);
							Recordset rsmsvisitnum = db.get(querymsvisitnum);
							rsmsvisitnum.first();
							String msvisitnum = rsmsvisitnum.getString("msvisitnum");

							//预约到访量
							String queryyyvisitnum = getLocalResource(basepath + "query-yyvisitnum.sql");
							queryyyvisitnum = getSQL(queryyyvisitnum, rsorg);
							queryyyvisitnum = getSQL(queryyyvisitnum, rsmc);
							Recordset rsyyvisitnum = db.get(queryyyvisitnum);
							rsyyvisitnum.first();
							String yyvisitnum = rsyyvisitnum.getString("yyvisitnum");

							//预约成交量
							String queryyydealnum = getLocalResource(basepath + "query-yydealnum.sql");
							queryyydealnum = getSQL(queryyydealnum, rsorg);
							queryyydealnum = getSQL(queryyydealnum, rsmc);
							Recordset rsyydealnum = db.get(queryyydealnum);
							rsyydealnum.first();
							String yydealnum = rsyydealnum.getString("yydealnum");
							
							//会员到期量
							String queryexpirenum = getLocalResource(basepath + "query-expirenum.sql");
							queryexpirenum = getSQL(queryexpirenum, rsorg);
							queryexpirenum = getSQL(queryexpirenum, rsmc);
							Recordset rsexpirenum = db.get(queryexpirenum);
							rsexpirenum.first();
							String expirenum = rsexpirenum.getString("expirenum");
							
							//会员续费量
							String queryrenewalnum = getLocalResource(basepath + "query-renewalnum.sql");
							queryrenewalnum = getSQL(queryrenewalnum, rsorg);
							queryrenewalnum = getSQL(queryrenewalnum, rsmc);
							Recordset rsrenewalnum = db.get(queryrenewalnum);
							rsrenewalnum.first();
							String renewalnum = rsrenewalnum.getString("renewalnum");
							
							
							/*System.out.println(rsmc.getString("name")+"任务额:"+rworderfee_target+"----完成额:"+orderfee_target
									+"-------陌生接待:"+msnum+"-------陌生成交:"+msvisitnum
									+"-------预约到访量:"+yyvisitnum+"-------预约成交量:"+yydealnum
									+"-------会员到期量:"+expirenum+"-------会员续费量:"+renewalnum);
							System.out.println("-----------------------------------");*/

							String insertMcDataStatistics = getLocalResource(basepath + "insert.sql");
							insertMcDataStatistics = getSQL(insertMcDataStatistics, rsorg);
							insertMcDataStatistics = getSQL(insertMcDataStatistics, rsmc);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${task_money}", rworderfee_target);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${results_money}", orderfee_target);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${strange_reception_num}", msnum);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${strange_deal_num}", msvisitnum);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${appointment_visit_num}", yyvisitnum);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${appointment_deal_num}", yydealnum);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${cust_expire_num}", expirenum);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${cust_renewal_num}", renewalnum);
							
							//完成比例
							String completion = null;
							if(Double.parseDouble(rworderfee_target)==0&&Double.parseDouble(orderfee_target)==0){
								completion = "0";
							}else if(Double.parseDouble(rworderfee_target)==0){
								completion = "100";
							}else if(Double.parseDouble(orderfee_target)==0){
								completion = "0";
							}else{
								completion = formatDouble(Double.parseDouble(orderfee_target)/Double.parseDouble(rworderfee_target)*100);
							}
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${completion}", completion);

							//陌生成交率
							String strange_transaction_rate = null;
							if(Double.parseDouble(msnum)==0&&Double.parseDouble(msvisitnum)==0){
								strange_transaction_rate = "0";
							}else if(Double.parseDouble(msnum)==0){
								strange_transaction_rate = "100";
							}else if(Double.parseDouble(msvisitnum)==0){
								strange_transaction_rate = "0";
							}else{
								strange_transaction_rate = formatDouble(Double.parseDouble(msvisitnum)/Double.parseDouble(msnum)*100);
							}
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${strange_transaction_rate}", strange_transaction_rate);
							
							//预约成交率
							String appointment_rate = null;
							if(Double.parseDouble(yyvisitnum)==0&&Double.parseDouble(yydealnum)==0){
								appointment_rate = "0";
							}else if(Double.parseDouble(yyvisitnum)==0){
								appointment_rate = "100";
							}else if(Double.parseDouble(yydealnum)==0){
								appointment_rate = "0";
							}else{
								appointment_rate = formatDouble(Double.parseDouble(yydealnum)/Double.parseDouble(yyvisitnum)*100);
							}
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${appointment_rate}", appointment_rate);

							//会籍续费率
							String mc_renewal_rate = null;
							if(Double.parseDouble(expirenum)==0&&Double.parseDouble(renewalnum)==0){
								mc_renewal_rate = "0";
							}else if(Double.parseDouble(expirenum)==0){
								mc_renewal_rate = "100";
							}else if(Double.parseDouble(renewalnum)==0){
								mc_renewal_rate = "0";
							}else{
								mc_renewal_rate = formatDouble(Double.parseDouble(renewalnum)/Double.parseDouble(expirenum)*100);
							}
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${mc_renewal_rate}", mc_renewal_rate);
							
							db.exec(insertMcDataStatistics);
						}else{
							//System.out.println(rsmc.getString("name")+"已统计");
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
	
	public static String formatDouble(double d) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(d);
    }
}
