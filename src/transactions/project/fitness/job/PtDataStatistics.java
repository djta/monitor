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

/***
 * 私教部个人数据统计
 * @author Administrator
 *
 */
public class PtDataStatistics extends BaseJob{
	private static Logger logger = Logger.getLogger(PtDataStatistics.class.getName());
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

				String basepath = "/transactions/project/fitness/job/sql/ptdata/statistics/";
				//查询所有的俱乐部
				String queryorg = getLocalResource(basepath + "query-org.sql");
				Recordset rsorg = db.get(queryorg);
				//循环俱乐部
				while(rsorg.next()){
					//根据俱乐部查询私教
					String querypt = getLocalResource(basepath + "query-pt.sql");
					querypt = getSQL(querypt, rsorg);
					Recordset rspt = db.get(querypt);
					//循环私教
					while(rspt.next()){
						String queryptdatastatistics = getLocalResource(basepath + "query-pt_data_statistics.sql");
						queryptdatastatistics = getSQL(queryptdatastatistics, rspt);
						queryptdatastatistics = getSQL(queryptdatastatistics, rsorg);
						Recordset rsdata = db.get(queryptdatastatistics);
						rsdata.first();
						//查询私教上月是否已统计
						if(Integer.parseInt(rsdata.getString("num"))==0){
							System.out.println(rspt.getString("name")+"----未统计");
							//统计上月私教部个人数据统计
							String querypttarget = getLocalResource(basepath + "query-target.sql");
							querypttarget = getSQL(querypttarget, rsorg);
							querypttarget = getSQL(querypttarget, rspt);
							Recordset rspttarget = db.get(querypttarget);
							String rworderfee_target = "0";
							if(rspttarget.getRecordCount()>0){
								rspttarget.first();
								//私教任务额
								rworderfee_target = rspttarget.getString("orderfee_target");
							}

							//完成额
							String queryorderfee_target = getLocalResource(basepath + "query-orderfee_target.sql");
							queryorderfee_target = getSQL(queryorderfee_target, rsorg);
							queryorderfee_target = getSQL(queryorderfee_target, rspt);
							Recordset rsorderfee_target = db.get(queryorderfee_target);
							String orderfee_target = "0";
							if(rsorderfee_target.getRecordCount()>0){
								rsorderfee_target.first();
								//私教任务额
								orderfee_target = rsorderfee_target.getString("orderfee_target");
								if(orderfee_target==null){
									orderfee_target = "0";
								}
							}

							//月销课时数量
							String querymarketclassnum = getLocalResource(basepath + "query-market_class_num.sql");
							querymarketclassnum = getSQL(querymarketclassnum, rsorg);
							querymarketclassnum = getSQL(querymarketclassnum, rspt);
							Recordset rsmarketclassnum = db.get(querymarketclassnum);
							String marketclassnum = "0";
							if(rsmarketclassnum.getRecordCount()>0){
								rsmarketclassnum.first();
								marketclassnum = rsmarketclassnum.getString("marketclassnum");
								if(marketclassnum==null){
									marketclassnum = "0";
								}
							}
							
							//私教课库存量
							String queryptinventorynum = getLocalResource(basepath + "query-pt_inventory_num.sql");
							queryptinventorynum = getSQL(queryptinventorynum, rsorg);
							queryptinventorynum = getSQL(queryptinventorynum, rspt);
							Recordset rsptinventorynum = db.get(queryptinventorynum);
							String ptinventorynum = "0";
							if(rsptinventorynum.getRecordCount()>0){
								rsptinventorynum.first();
								ptinventorynum = rsptinventorynum.getString("ptinventorynum");
								if(ptinventorynum==null){
									ptinventorynum = "0";
								}
							}
							
							//私教授课量
							String queryptteachingnum = getLocalResource(basepath + "query-pt_teaching_num.sql");
							queryptteachingnum = getSQL(queryptteachingnum, rsorg);
							queryptteachingnum = getSQL(queryptteachingnum, rspt);
							Recordset rsptteachingnum = db.get(queryptteachingnum);
							rsptteachingnum.first();
							String ptteachingnum = rsptteachingnum.getString("ptteachingnum");

							//体测量
							String querytestresultnum = getLocalResource(basepath + "query-testresult_num.sql");
							querytestresultnum = getSQL(querytestresultnum, rsorg);
							querytestresultnum = getSQL(querytestresultnum, rspt);
							Recordset rstestresultnum = db.get(querytestresultnum);
							rstestresultnum.first();
							String testresultnum = rstestresultnum.getString("testresultnum");

							//场开量
							String querysitetargetnum = getLocalResource(basepath + "query-sitetargetnum.sql");
							querysitetargetnum = getSQL(querysitetargetnum, rsorg);
							querysitetargetnum = getSQL(querysitetargetnum, rspt);
							Recordset rssitetargetnum = db.get(querysitetargetnum);
							rssitetargetnum.first();
							String sitetargetnum = rssitetargetnum.getString("sitetargetnum");

							//场开成交量
							String querysitetargetdealnum = getLocalResource(basepath + "query-site_target_deal_num.sql");
							querysitetargetdealnum = getSQL(querysitetargetdealnum, rsorg);
							querysitetargetdealnum = getSQL(querysitetargetdealnum, rspt);
							Recordset rssitetargetdealnum = db.get(querysitetargetdealnum);
							rssitetargetdealnum.first();
							String sitetargetdealnum = rssitetargetdealnum.getString("sitetargetdealnum");

							//私教到期量
							String queryptexpirenum = getLocalResource(basepath + "query-pt_expire_num.sql");
							queryptexpirenum = getSQL(queryptexpirenum, rsorg);
							queryptexpirenum = getSQL(queryptexpirenum, rspt);
							Recordset rseptexpirenum = db.get(queryptexpirenum);
							rseptexpirenum.first();
							String ptexpirenum = rseptexpirenum.getString("ptexpirenum");
							
							//私教续费量
							String queryptrenewalnum = getLocalResource(basepath + "query-pt_renewal_num.sql");
							queryptrenewalnum = getSQL(queryptrenewalnum, rsorg);
							queryptrenewalnum = getSQL(queryptrenewalnum, rspt);
							Recordset rsptrenewalnum = db.get(queryptrenewalnum);
							rsptrenewalnum.first();
							String ptrenewalnum = rsptrenewalnum.getString("ptrenewalnum");
							
							
							/*System.out.println("-------------------------------------------------------------------");
							System.out.println(rspt.getString("name")+"私教任务额:"+rworderfee_target+"----完成额:"+orderfee_target
									+"-------月销课时数量:"+marketclassnum+"-------私教授课量:"+ptteachingnum
									+"-------体测量:"+testresultnum+"-------场开量:"+sitetargetnum
									+"-------场开成交量:"+sitetargetdealnum+"-------会员续费量------------:"+ptinventorynum);*/
							
							String insertMcDataStatistics = getLocalResource(basepath + "insert.sql");
							insertMcDataStatistics = getSQL(insertMcDataStatistics, rsorg);
							insertMcDataStatistics = getSQL(insertMcDataStatistics, rspt);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${task_money}", rworderfee_target);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${results_money}", orderfee_target);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${market_class_num}", marketclassnum);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${pt_teaching_num}", ptteachingnum);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${testresult_num}", testresultnum);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${site_target_num}", sitetargetnum);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${site_target_deal_num}", sitetargetdealnum);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${pt_expire_num}", ptexpirenum);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${pt_inventory_num}", ptinventorynum);
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${pt_renewal_num}", ptrenewalnum);
							
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

							//场开成交率
							String site_target_deal_rate = null;
							if(Double.parseDouble(sitetargetnum)==0&&Double.parseDouble(sitetargetdealnum)==0){
								site_target_deal_rate = "0";
							}else if(Double.parseDouble(sitetargetnum)==0){
								site_target_deal_rate = "100";
							}else if(Double.parseDouble(sitetargetdealnum)==0){
								site_target_deal_rate = "0";
							}else{
								site_target_deal_rate = formatDouble(Double.parseDouble(sitetargetdealnum)/Double.parseDouble(sitetargetnum)*100);
							}
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${site_target_deal_rate}", site_target_deal_rate);

							//私教续费率
							String pt_renewal_rate = null;
							if(Double.parseDouble(ptexpirenum)==0&&Double.parseDouble(ptrenewalnum)==0){
								pt_renewal_rate = "0";
							}else if(Double.parseDouble(ptexpirenum)==0){
								pt_renewal_rate = "100";
							}else if(Double.parseDouble(ptrenewalnum)==0){
								pt_renewal_rate = "0";
							}else{
								pt_renewal_rate = formatDouble(Double.parseDouble(ptrenewalnum)/Double.parseDouble(ptexpirenum)*100);
							}
							insertMcDataStatistics = StringUtils.replaceOnce(insertMcDataStatistics, "${pt_renewal_rate}", pt_renewal_rate);

							db.exec(insertMcDataStatistics);
							
						}else{
							//System.out.println(rspt.getString("name")+"已统计");
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
