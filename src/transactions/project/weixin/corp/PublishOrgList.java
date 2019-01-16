package transactions.project.weixin.corp;

import transactions.project.weixin.common.WeixinUtil;
import dinamica.Db;
import dinamica.GenericTableManager;
import dinamica.Recordset;

public class PublishOrgList extends GenericTableManager {
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Db db = getDb();

		String queryCorpSql = getSQL(getResource("query-corp.sql"), inputParams);

		Recordset queryCorpRecordset = db.get(queryCorpSql);
		queryCorpRecordset.first();
		String corpTuid = queryCorpRecordset.getString("tuid");
		if (null == corpTuid) {
			throw new Throwable("企业号不存在");
		}
		String corpId = queryCorpRecordset.getString("corp_id");
		if (null == corpId) {
			throw new Throwable("corp_id不能为空");
		}
		String secret = queryCorpRecordset.getString("secret");
		if (null == secret) {
			throw new Throwable("secret不能为空");
		}
		String tenantryId = queryCorpRecordset.getString("tenantry_id");
		if (null == tenantryId) {
			throw new Throwable("tenantryId不能为空");
		}
		String pid = inputParams.getString("pid");
		if (null == pid) {
			throw new Throwable("pid不能为空");
		}
		String weixinOrgPid = inputParams.getString("weixin_org_pid");
		if (null == weixinOrgPid) {
			throw new Throwable("weixin_org_pid不能为空");
		}
		String accessToken = WeixinUtil.getAccessTokenForCorp(db, corpTuid,
				corpId, secret);
		WeixinUtil.publishOrgList(db, tenantryId, accessToken, pid,
				weixinOrgPid);
		return rc;
	}
}
