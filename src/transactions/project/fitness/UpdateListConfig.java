package transactions.project.fitness;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

/**
 * 修改列表类型参数配置表
 * @author Administrator
 *
 */
public class UpdateListConfig extends GenericTransaction {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = getDb();

		//查询该俱乐部存不存在该参数
		String query = getLocalResource("/transactions/project/fitness/configlist/select-cnfg.sql");
		query = getSQL(query, inputParams);
		Recordset rs = db.get(query);
		rs.first();
		int count = rs.getInteger("count");
		String crud = inputParams.getString("crud");
		//判断该俱乐部有没有该参数   
		if(count<1){
			//该俱乐部添加该参数
			String insert = getLocalResource("/transactions/project/fitness/configlist/insert-findcnfg.sql");
			insert = getSQL(insert, inputParams);
			db.addBatchCommand(insert);
			//新参数
			if(crud.equals("1")||crud.equals("2")){
				String insertcnfg = getLocalResource("/transactions/project/fitness/configlist/insert.sql");
				insertcnfg = getSQL(insertcnfg, inputParams);
				db.addBatchCommand(insertcnfg);
			}
		}else{
			//添加参数
			if(crud.equals("1")){
				String insertcnfg = getLocalResource("/transactions/project/fitness/configlist/insert.sql");
				insertcnfg = getSQL(insertcnfg, inputParams);
				db.addBatchCommand(insertcnfg);
			}else if(crud.equals("2")){
				//修改参数
				String update = getLocalResource("/transactions/project/fitness/configlist/update-cnfg.sql");
				update = getSQL(update, inputParams);
				db.addBatchCommand(update);
			}else if(crud.equals("3")){
				//删除参数
				String delete = getLocalResource("/transactions/project/fitness/configlist/delete-cnfg.sql");
				delete = getSQL(delete, inputParams);
				db.addBatchCommand(delete);
			}
		}
		db.exec();
		return rc;
	}
}
