package transactions.project.util.order;

import org.apache.commons.lang.StringUtils;

import transactions.project.util.Tools;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

/***
 * 订单费用分成
 * @author C
 * 2015-08-28
 */
public class OrderSplit extends GenericTransaction{
	private Db db = null;
	private String sale_order_code = null;
	
	public void setDb(Db db){
		this.db = db;
	}
	
	public void setSaleOrderCode(String sale_order_code){
		this.sale_order_code = sale_order_code;
	}
	
	public int service(Recordset inputs) throws Throwable{
		int rc = 0;
		String queryAccount = getLocalResource("sql/query-account.sql");
		String updateAccount = getLocalResource("sql/update-account.sql");
		String insertList = getLocalResource("sql/insert-account-list.sql");
		// 获取订单记录
		String queryOrder = getLocalResource("sql/query-order.sql");
		queryOrder = StringUtils.replace(queryOrder, "${sale_order_code}", sale_order_code);
		Recordset rsOrder = this.queryRecordset(queryOrder, "未找到订单记录");
		while( rsOrder.next() ){
			Integer platOrgId = (rsOrder.isNull("plat_org_id") ? null : rsOrder.getInteger("plat_org_id"));	// 获取平台编号
			String personUserlogin = (rsOrder.isNull("person_userlogin") ? null : rsOrder.getString("person_userlogin"));	// 获取个人（服务者）登录代码
			Integer orgId = (rsOrder.isNull("org_id") ? null : rsOrder.getInteger("org_id"));	// 获取服务商编号
			// 获取订单明细
			String queryDetail = getLocalResource("sql/query-order-detail.sql");
			queryDetail = getSQL(queryDetail, rsOrder);
			Recordset rsDetail = db.get(queryDetail);
			while(rsDetail.next()){
				double price = (rsDetail.isNull("price") ? 0d : rsDetail.getDouble("price"));
				double goodsFee = price;
				// 1、平台提成
				String platRate = rsDetail.getString("plat_rate");
				goodsFee = this.handleFeeCut(String.valueOf(platOrgId), platRate, price, "2", queryAccount, updateAccount, insertList, rsDetail, goodsFee, null);
				// 2、个人费用（服务者）
				String personRate = rsDetail.getString("person_rate");
				goodsFee = this.handleFeeCut(personUserlogin, personRate, price, "1", queryAccount, updateAccount, insertList, rsDetail, goodsFee, null);
				// 3、服务商费用
				goodsFee = this.handleFeeCut(String.valueOf(orgId), null, price, "2", queryAccount, updateAccount, insertList, rsDetail, goodsFee, goodsFee);
			}
		}
		db.exec();
		return rc;
	}
	
	/**
	 * 提成费用处理
	 * @param userlogin
	 * @param rate
	 * @param price
	 * @param acountType
	 * @param queryAccount
	 * @param updateAccount
	 * @param insertList
	 * @param rsDetail
	 * @param goodsFee
	 * @param _cutFee
	 * @return
	 * @throws Throwable 
	 */
	private double handleFeeCut(String userlogin, String rate, double price, String accountType, 
			String queryAccount, String updateAccount, String insertList, Recordset rsDetail, double goodsFee, Double _cutFee) throws Throwable{
		if( StringUtils.isNotBlank(userlogin) ){
			// 获取帐号
			String _queryAccount = StringUtils.replace(queryAccount, "${userlogin}", userlogin);
			_queryAccount = StringUtils.replace(_queryAccount, "${account_type}", accountType);
			Recordset rsAccount = this.queryRecordset(_queryAccount, "未找到帐号记录");
			rsAccount.first();
			// 获取提成金额
			double cutFee = 0d;
			if( null != _cutFee ){
				cutFee = _cutFee.doubleValue();
			}else{
				cutFee = this.getFeeCut(price, rate);
			}
			if( cutFee > 0d ){
				goodsFee = Tools.reduceDoubleValue(goodsFee, cutFee);	// 扣除提成后金额
				// 更新账户余额
				double balance = (rsAccount.isNull("account_balance") ? 0d : rsAccount.getDouble("account_balance"));	// 账户余额
				balance = Tools.addDoubleValue(balance, cutFee);
				String _updateAccount = getSQL(updateAccount, rsAccount);
				_updateAccount = StringUtils.replace(_updateAccount, "${balance}", String.valueOf(balance));
				db.addBatchCommand(_updateAccount);
				// 增加账户历史
				String insertHistory = getSQL(insertList, rsDetail);
				insertHistory = getSQL(insertHistory, rsAccount);
				insertHistory = StringUtils.replace(insertHistory, "${fee}", String.valueOf(cutFee));
				insertHistory = StringUtils.replace(insertHistory, "${recharge_type}", "2");
				insertHistory = StringUtils.replace(insertHistory, "${remark}", "订单提成");
				db.addBatchCommand(insertHistory);
			}
		}
		return goodsFee;
	}
	
	private Recordset queryRecordset(String query, String errMsg) throws Throwable{
		Recordset rs = db.get(query);
		if( null == rs || rs.getRecordCount() <= 0 ){
			throw new Throwable(errMsg);
		}
		rs.first();
		return rs;
	}
	
	/***
	 * 获取分成金额
	 * @param price
	 * @param rateStr
	 * @return
	 */
	private double getFeeCut(double price, String rateStr){
		double cutFee = 0d;
		if( StringUtils.isNotBlank(rateStr) ){
			if( rateStr.endsWith("%") ){	// 百分比
				rateStr = StringUtils.replace(rateStr, "%", "");
				cutFee = price*Double.parseDouble(rateStr)/100;
			}else{	// 固定值
				cutFee = Double.parseDouble(rateStr);
			}
		}
		return cutFee;
	}
	
}
