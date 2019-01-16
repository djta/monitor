package transactions.project.fitness;

import java.sql.Types;

import transactions.project.fitness.util.ErpTools;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;
import dinamica.StringUtil;

/***
 * 生成体验卡卡号
 * 8位格式：T+2位俱乐部编号头+0+1位数+3位随机字符（数字或大写字母）
 * @author C
 * 2018-03-23
 */
public class CreateExpercard extends GenericTransaction {
	
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = getDb();
		
		String resultcode = ErpTools.no;
		String expercardcode = "";
		String msg = "";
		Recordset rs = new Recordset();
		rs.append("resultcode", Types.VARCHAR);
		rs.append("expercardcode", Types.VARCHAR);
		rs.append("_org_id", Types.INTEGER);
		rs.append("msg", Types.VARCHAR);
		
		Integer org_id = null;
		if( !inputParams.containsField("org_id") || inputParams.isNull("org_id") || null == inputParams.getInteger("org_id") ){
			throw new Throwable("参数org_id不能为空");
		}
		org_id = inputParams.getInteger("org_id");
		
		try {
			String queryCodeExists = "SELECT code, org_id FROM cc_expercard_log WHERE expercardcode = '${code}' AND status = 1 AND org_id = ${fld:org_id}";
			queryCodeExists = getSQL(queryCodeExists, inputParams);
			String queryOrg = "SELECT memberhead FROM hr_org WHERE org_id = ${fld:org_id}";
			queryOrg = getSQL(queryOrg, inputParams);
			Recordset rsOrg = db.get(queryOrg);
			if( null != rsOrg && rsOrg.getRecordCount() > 0 ){
				rsOrg.first();
				String memberhead = "";
				if( !rsOrg.isNull("memberhead") && null != rsOrg.getValue("memberhead") && !"".equals(rsOrg.getString("memberhead")) ){
					memberhead = rsOrg.getString("memberhead");
					memberhead = memberhead.toUpperCase();
				}
				expercardcode = this.createCode(db, queryCodeExists, memberhead);
				resultcode = ErpTools.yes;
			}else{
				msg = "获取俱乐部信息失败";
			}
		} catch(Throwable t) {
			t.printStackTrace();
			msg = "生成体验卡号失败";
		} finally {
			rs.addNew();
			rs.setValue("resultcode", resultcode);
			rs.setValue("expercardcode", expercardcode);
			rs.setValue("msg", msg);
			rs.setValue("_org_id", org_id);
			publish("_rsexpercard", rs);
		}
		return rc;
	}
	
	private static final String EXPERCARD_HEAD = "T";	// 体验卡首位

	/**
	 * 生成体验卡号
	 * @param db
	 * @param queryCodeExists
	 * @param memberhead
	 * @return
	 * @throws Throwable
	 */
	private String createCode(Db db, String queryCodeExists, String memberhead) throws Throwable{
		// 8位格式：T+2位俱乐部编号头+0+1位数+3位随机字符（数字或大写字母）
		String code = EXPERCARD_HEAD + memberhead + "0" + ErpTools.getNumberRandom(1) + ErpTools.getUpperStringRandom(3);
		String _queryCodeExists = StringUtil.replace(queryCodeExists, "${code}", code);
		Recordset rsExists = db.get(_queryCodeExists);
		if( null != rsExists && rsExists.getRecordCount() > 0 ){
			return createCode(db, queryCodeExists, memberhead);
		}
		return code;
	}
}
