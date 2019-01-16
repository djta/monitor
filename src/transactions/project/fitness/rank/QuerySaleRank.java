package transactions.project.fitness.rank;

import java.sql.Types;

import transactions.project.util.Tools;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.xml.Element;

/***
 * 销售金额排名工具类
 * @author C
 * 2018-01-04
 */
public class QuerySaleRank extends GenericTransaction {

	public int service(Recordset inputs) throws Throwable {

		int rc = super.service(inputs);
		// 已完成
		Element q[] = getConfig().getDocument().getElements("query-finish");
		if( null == q || q.length == 0 ){
			throw new Throwable("The node query-finish cannot be null in config.xml");
		}
		String queryName = q[0].getString();
		String queryFinish = getResource(queryName);
		// 目标
		q = getConfig().getDocument().getElements("query-target");
		if( null == q || q.length == 0 ){
			throw new Throwable("The node query-target cannot be null in config.xml");
		}
		queryName = q[0].getString();
		String queryTarget = getResource(queryName);
		queryTarget = getSQL(queryTarget, inputs);
		Recordset rsTarget = getDb().get(queryTarget);
		// 员工
		String queryStaff = getLocalResource("sql/query-staff.sql");
		queryStaff = getSQL(queryStaff, inputs);
		Recordset rsStaff = getDb().get(queryStaff);
		
		Recordset rs = new Recordset();
		rs.append("rankno", Types.INTEGER);
		rs.append("userlogin", Types.VARCHAR);
		rs.append("staffname", Types.VARCHAR);
		rs.append("userid", Types.INTEGER);
		rs.append("finishfee", Types.DOUBLE);
		rs.append("targetfee", Types.DOUBLE);

		queryFinish = getSQL(queryFinish, inputs);
		Recordset rsFinish = getDb().get(queryFinish);
		while( rsFinish.next() ){
			String salemember = rsFinish.getString("salemember");
			Double finishfee = rsFinish.getDouble("finishfee");
			if( null == finishfee || finishfee.doubleValue() == 0 || null == salemember || "".equals(salemember) ){
				continue;
			}
			int idx = findRecordIdx(rs, "userlogin", salemember);
			if( idx >= 0 ){
				rs.setRecordNumber(idx);
				finishfee = Tools.addDoubleValue(finishfee, rs.getDouble("finishfee"));
			}else{
				rs.addNew();
				rs.setValue("userlogin", salemember);
			}
			rs.setValue("finishfee", finishfee);
		}
		if( null != rs && rs.getRecordCount() > 0 ){
			rs.sort("finishfee");
			rs.sort("finishfee");
			rs.top();
			while( rs.next() ){
				rs.setValue("rankno", rs.getRecordNumber()+1);
				int idx = findRecordIdx(rsStaff, "userlogin", rs.getString("userlogin"));
				if( idx >= 0 ){
					rsStaff.setRecordNumber(idx);
					rs.setValue("staffname", rsStaff.getString("name"));
					rs.setValue("userid", rsStaff.getInteger("user_id"));
				}else{
					rs.setValue("staffname", "未知");
				}
				idx = findRecordIdx(rsTarget, "userlogin", rs.getString("userlogin"));
				if( idx >= 0 ){
					rsTarget.setRecordNumber(idx);
					rs.setValue("targetfee", rsTarget.getDouble("targetfee"));
				}else{
					rs.setValue("targetfee", 0d);
				}
			}
		}
		
		publish("query.sql", rs);
		return rc;
	}
	
	private int findRecordIdx(Recordset rs, String colName, String value) throws Throwable{
		int idx = -1;
		if( null != rs && rs.getRecordCount() > 0 ){
			rs.top();
			idx = rs.findRecord(colName, value);
		}
		return idx;
	}
}
