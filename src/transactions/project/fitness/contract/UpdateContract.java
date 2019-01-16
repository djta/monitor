package transactions.project.fitness.contract;

import dinamica.GenericTableManager;
import dinamica.Recordset;

/**
 * 操作(修改/删除)未付款合同
 * @author C
 * 2016-05-24
 */
public class UpdateContract extends GenericTableManager {

	public int service(Recordset inputParams) throws Throwable {
		int rc = 0;
		String query = "SELECT status FROM cc_contract WHERE code = ${fld:contractcode} AND org_id = ${def:org}";
		query = getSQL(query, inputParams);
		Recordset rs = getDb().get(query);
		if( rs.getRecordCount() > 0 ){
			if( rs.getRecordCount() == 1 ){
				rs.first();
				Integer status = rs.getInteger("status");
				if( 2 == status.intValue() || 4 == status.intValue() ){
					throw new Throwable("该合同已付款，不允许修改删除");
				}else{
					rc = super.service(inputParams);
				}
			}else{
				throw new Throwable("该合同编号在合同中被重复");
			}
		}else{
			throw new Throwable("未找到相应的合同数据");
		}
		return rc;
	}
	
}
