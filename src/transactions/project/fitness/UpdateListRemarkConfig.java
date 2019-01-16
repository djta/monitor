package transactions.project.fitness;

import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

/**
 * 修改列表类型参数配置表（加备注字段）
 * @author Administrator
 *
 */
public class UpdateListRemarkConfig extends GenericTransaction {

	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = getDb();

		//查询该俱乐部存不存在该参数
		String query = getLocalResource("/transactions/project/fitness/configlistremark/select-cnfg.sql");
		query = getSQL(query, inputParams);
		Recordset rs = db.get(query);
		rs.first();
		int count = rs.getInteger("count");
		String crud = inputParams.getString("crud");
		//判断该俱乐部有没有该参数   
		if(count<1){
			//该俱乐部添加该参数
			String insert = getLocalResource("/transactions/project/fitness/configlistremark/insert-findcnfg.sql");
			insert = getSQL(insert, inputParams);
			db.addBatchCommand(insert);
			//新参数
			if(crud.equals("1")||crud.equals("2")||crud.equals("5")||crud.equals("6")){
				String insertcnfg = getLocalResource("/transactions/project/fitness/configlistremark/insert.sql");
				insertcnfg = getSQL(insertcnfg, inputParams);
				db.addBatchCommand(insertcnfg);
			}
			if(crud.equals("4")){
				//禁用启动参数
				String updatestatus = getLocalResource("/transactions/project/fitness/configlistremark/insert-status.sql");
				updatestatus = getSQL(updatestatus, inputParams);
				db.addBatchCommand(updatestatus);
			}
		}else{
			//添加参数
			if(crud.equals("1")||crud.equals("5")){
				String insertcnfg = getLocalResource("/transactions/project/fitness/configlistremark/insert.sql");
				insertcnfg = getSQL(insertcnfg, inputParams);
				db.addBatchCommand(insertcnfg);
			}else if(crud.equals("2")||crud.equals("6")){
				//修改参数
				String update = getLocalResource("/transactions/project/fitness/configlistremark/update-cnfg.sql");
				update = getSQL(update, inputParams);
				db.addBatchCommand(update);
			}else if(crud.equals("3")||crud.equals("7")){
				//删除参数
				String delete = getLocalResource("/transactions/project/fitness/configlistremark/delete-cnfg.sql");
				delete = getSQL(delete, inputParams);
				db.addBatchCommand(delete);
			}else if(crud.equals("4")){
				//禁用启动参数
				String delete = getLocalResource("/transactions/project/fitness/configlistremark/update-status.sql");
				delete = getSQL(delete, inputParams);
				db.addBatchCommand(delete);
			}
		}
		db.exec();
		return rc;
	}
}
