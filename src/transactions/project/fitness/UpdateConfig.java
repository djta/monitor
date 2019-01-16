package transactions.project.fitness;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

/**
 * 修改参数配置表
 * @author Administrator
 *
 */
public class UpdateConfig extends GenericTransaction {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = getDb();

		//查询该俱乐部存不存在该参数
		String query = getLocalResource("/transactions/project/fitness/config/select-cnfg.sql");
		query = getSQL(query, inputParams);
		Recordset rs = db.get(query);
		rs.first();
		int count = rs.getInteger("count");
		//判断该俱乐部有没有该参数   
		if(count<1){
			//该俱乐部添加该参数
			String insert = getLocalResource("/transactions/project/fitness/config/insert-cnfg.sql");
			insert = getSQL(insert, inputParams);
			db.addBatchCommand(insert);
		}else{
			//修改参数
			String update = getLocalResource("/transactions/project/fitness/config/update-cnfg.sql");
			update = getSQL(update, inputParams);
			db.addBatchCommand(update);
		}
		db.exec();
		return rc;
	}
}
