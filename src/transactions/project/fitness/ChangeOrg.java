package transactions.project.fitness;

import dinamica.GenericTransaction;
import dinamica.Recordset;

/***
 * 登录后变更所属俱乐部
 * @author Administrator
 * 2017-12-07
 */
public class ChangeOrg extends GenericTransaction {
	
	public int service(Recordset inputParams) throws Throwable {
		int rc = 0;

		super.service(inputParams);
		// 获取参数org_id
		if( !inputParams.containsField("org_id") || inputParams.isNull("org_id") || null == inputParams.getValue("org_id") ){
			throw new Throwable("The parameter org_id is null.");
		}
		Integer org_id = inputParams.getInteger("org_id");
		// 验证org_id是否有效、是否有权限访问
		if( !isOrgValidate(org_id) ){
			throw new Throwable("The org_id is invalid or no access.");
		}

		getSession().setAttribute("dinamica.user.org", org_id);
		return rc;
	}
	
	/***
	 * 验证org_id是否有效、是否有权限访问
	 * @param org_id
	 * @return
	 */
	private boolean isOrgValidate(Integer org_id){
		boolean isvalid = false;
		try {
			String userid = (String) getSession().getAttribute("dinamica.user.id");
			String query = "select 1 from hr_staff f where f.user_id = "+userid+" "
					+ "and (f.org_id = "+org_id+" or exists(select 1 from hr_staff_org o where o.user_id = f.user_id and o.org_id = "+org_id+"))" ;
			Recordset rs = getDb().get(query);
			isvalid = (rs.getRecordCount() > 0);
		} catch (Throwable e) {
			isvalid = false;
			e.printStackTrace();
		}
		return isvalid;
	}
}
