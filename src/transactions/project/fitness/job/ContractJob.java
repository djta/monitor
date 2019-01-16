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
 * 合同相关处理
 * @author C
 * 2016-06-28
 */
public class ContractJob extends BaseJob{
	private static Logger logger = Logger.getLogger(ContractJob.class.getName());
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
				// 删除超过7天未付款的合同
				// 获取删除脚本
				String deleteContract = getLocalResource("/transactions/project/fitness/job/sql/contract/unpay/delete-contract.sql");
				String deleteCustomer = getLocalResource("/transactions/project/fitness/job/sql/contract/unpay/delete-customer.sql");
				// 获取需要更新的数据
				String queryUnpay = getLocalResource("/transactions/project/fitness/job/sql/contract/unpay/query.sql");
				
				// 获取俱乐部列表
				String queryOrg =  getLocalResource("/transactions/project/fitness/job/sql/query-org.sql");
				Recordset rsOrg = db.get(queryOrg);
				while(rsOrg.next()){
					String _queryUnpay = getSQL(queryUnpay, rsOrg);
					Recordset rsUnpay = db.get(_queryUnpay);
					while(rsUnpay.next()){
						db.beginTrans();
						// 删除会员信息
						String _deleteCustomer= getSQL(deleteCustomer, rsUnpay);
						db.exec(_deleteCustomer);
						// 删除合同
						String _deleteContract = getSQL(deleteContract, rsUnpay);
						db.exec(_deleteContract);
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
