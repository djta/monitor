package transactions.project.weixin.store.club;

import dinamica.*;

public class VipReserveValidatephone extends GenericTransaction
{

	public synchronized int service(Recordset inputParams) throws Throwable
	{

		int rc = super.service(inputParams);
		
		Db db = getDb();
		String sql_file = getConfig().getConfigValue("validate-sql","validate.sql");
		String goods_id = getConfig().getConfigValue("goods_id","goods_id");
		String goods_name = getConfig().getConfigValue("goods_name","goods_name");
		String good_num = getConfig().getConfigValue("good_num","good_num");
		String store_id = getConfig().getConfigValue("store_id","store_id");
		int recordCount = getRequest().getParameterValues(goods_id).length;
		String [] good_ids = getRequest().getParameterValues(goods_id);
		String [] good_nums = getRequest().getParameterValues(good_num);
		String [] good_names = getRequest().getParameterValues(goods_name);
		String store_id_value = getRequest().getParameter(store_id);
		Recordset detail = new Recordset();
		detail.append(goods_id, java.sql.Types.VARCHAR);
		detail.append(store_id, java.sql.Types.VARCHAR);
		RequestValidationException errors = new RequestValidationException();
		for(int i=0;i<recordCount;i++){
			detail.addNew();
			detail.setValue(goods_id, good_ids[i]);
			if(store_id_value!= null && store_id_value.trim().length()>0){
				detail.setValue(store_id, store_id_value);
			}
			String sql = getSQL(getResource(sql_file),detail);
			Recordset r = db.get(sql);
			while(r.next()){
				Double num_left = r.getDouble("c");
				Double account =Double.valueOf(good_nums[i]);
				if(num_left!=null && account < num_left){
					errors.addMessage(good_names[i]+"余额不足！当前余额"+good_nums[i]+"元，活动价格 "+num_left+"元。<br/>");
				}
			}
		}
		if(errors.getErrors().getRecordCount() > 0){
			getRequest().setAttribute("dinamica.error.validation", "/action/project/weixin/club/campaign/shift_phone/alert_price");
			getRequest().setAttribute("dinamica.error.exception", errors);
			String desc = "";
			Recordset r = errors.getErrors();
			r.top();
			for(int i=0;i<errors.getErrors().getRecordCount();i++){
				r.next();
				desc += r.getString("message");
			}
			getRequest().setAttribute("dinamica.error.description", desc);
			throw errors;
		}
		return rc;
	}
}
