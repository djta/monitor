package transactions.project.weixin.corp;


import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;

public class InsertMenu extends GenericTableManager{
@Override
public int service(Recordset inputParams) throws Throwable {
	int rc=super.service(inputParams);
	String insertMenuSql=getSQL(getResource("insert.sql"), inputParams);
	String queryParentNum=getSQL(getResource("query-parent_num.sql"), inputParams);
	String queryChildNum=getSQL(getResource("query-child_num.sql"), inputParams);
	Db db=getDb();
	String p_id=inputParams.getString("p_id");
	if(null==p_id){
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
	db.exec(insertMenuSql);
	// TODO Auto-generated method stub
	return rc;
}
}
