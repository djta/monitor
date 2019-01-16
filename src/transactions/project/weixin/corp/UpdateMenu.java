package transactions.project.weixin.corp;


import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;

public class UpdateMenu extends GenericTableManager{
	@Override
	public int service(Recordset inputParams) throws Throwable {
		int rc=super.service(inputParams);
		String updateMenuSql=getSQL(getResource("update.sql"), inputParams);
		String queryParentNum=getSQL(getResource("query-parent_num.sql"), inputParams);
		String queryChildNum=getSQL(getResource("query-child_num.sql"), inputParams);
		String queryTuid=getSQL(getResource("query-tuid.sql"), inputParams);
		Db db=getDb();
		Recordset tuidRecordset=db.get(queryTuid);
		tuidRecordset.first();
		String old_p_id=tuidRecordset.getString("p_id")==null?"":tuidRecordset.getString("p_id");
		String new_p_id=inputParams.getString("p_id")==null?"":inputParams.getString("p_id");
		if(!old_p_id.equals(new_p_id)){
			if("".equals(new_p_id)){
				Recordset queryParentNumRecordset=db.get(queryParentNum);
				if(queryParentNumRecordset.getRecordCount()>=3){
					throw new Throwable("可创建最多 3 个一级菜单");
				}
			}else{
				Recordset queryChildNumRecordset=db.get(queryChildNum);
				if(queryChildNumRecordset.getRecordCount()>=5){
					throw new Throwable("每个一级菜单下可创建最多 5 个二级菜单");
				}
			}
		}
		db.exec(updateMenuSql);
		return rc;
	}

}
