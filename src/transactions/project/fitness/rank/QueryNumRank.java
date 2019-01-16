package transactions.project.fitness.rank;

import java.sql.Types;

import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.xml.Element;

/***
 * 计数排名工具类
 * @author C
 * 2018-01-04
 */
public class QueryNumRank extends GenericTransaction {

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
		rs.append("finishnum", Types.INTEGER);
		rs.append("targetnum", Types.INTEGER);

		queryFinish = getSQL(queryFinish, inputs);
		Recordset rsFinish = getDb().get(queryFinish);
		while( rsFinish.next() ){
			String salemember = rsFinish.getString("salemember");
			Integer finishnum = rsFinish.getInteger("finishnum");
			if( null == finishnum || finishnum.intValue() == 0 || null == salemember || "".equals(salemember) ){
				continue;
			}
			int idx = findRecordIdx(rs, "userlogin", salemember);
			if( idx >= 0 ){
				rs.setRecordNumber(idx);
				finishnum = finishnum.intValue() + rs.getInteger("finishnum").intValue();
			}else{
				rs.addNew();
				rs.setValue("userlogin", salemember);
			}
			rs.setValue("finishnum", finishnum);
		}
		if( null != rs && rs.getRecordCount() > 0 ){
			rs.sort("finishnum");
			rs.sort("finishnum");
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
					rs.setValue("targetnum", rsTarget.getInteger("targetnum"));
				}else{
					rs.setValue("targetnum", 0);
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
