package transactions.project.weixin.store.goods;

import javax.servlet.http.HttpSession;

import dinamica.GenericTransaction;
import dinamica.Recordset;

public class CartSubmit extends GenericTransaction
{

	public synchronized int service(Recordset inputParams) throws Throwable
	{
		super.service(inputParams);
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
    		GenericTransaction t = (GenericTransaction) loader.loadClass("transactions.project.weixin.store.goods.GoodsReserveValidate").newInstance();
			t.init(this.getContext(), this.getRequest(), this.getResponse());
			t.setConfig(this.getConfig());
			t.setConnection(this.getConnection());
            t.service(inputParams);
		} catch (Throwable e) {
			throw e;
		}
		
		try {
			String [] goods_name=getRequest().getParameterValues("goods_name");
			String [] goods_id_a = getRequest().getParameterValues("goods_id");
			String [] goods_num_a = getRequest().getParameterValues("goods_num");
			String [] tenantry_id_a = getRequest().getParameterValues("tenantry_id");
			String [] price_a = getRequest().getParameterValues("actual_price");
			String[] goods_param_desc_str = getRequest().getParameterValues("goods_param_desc");
			String[] goods_param_str = getRequest().getParameterValues("goods_param");
			String[] bespeak_value_str = getRequest().getParameterValues("bespeak_value");
			
			HttpSession session = getSession();
			Recordset r_goods = new Recordset();
			r_goods.append("goods_id", java.sql.Types.VARCHAR);
			r_goods.append("goods_name", java.sql.Types.VARCHAR);
			r_goods.append("goods_num", java.sql.Types.INTEGER);
			r_goods.append("tenantry_id", java.sql.Types.INTEGER);
			r_goods.append("goods_price", java.sql.Types.DOUBLE);
			r_goods.append("goods_param_desc", java.sql.Types.VARCHAR);
			r_goods.append("goods_param", java.sql.Types.VARCHAR);
			r_goods.append("bespeak_value", java.sql.Types.VARCHAR);			
			for (int i = 0; i < goods_id_a.length; i++) {
				r_goods.addNew();
				r_goods.setValue("goods_id", goods_id_a[i]);
				r_goods.setValue("goods_name", goods_name[i]);
				r_goods.setValue("goods_num", Integer.parseInt(goods_num_a[i]));
				r_goods.setValue("tenantry_id", Integer.parseInt(tenantry_id_a[i]));
				r_goods.setValue("goods_price", Double.parseDouble(price_a[i]));
				r_goods.setValue("goods_param_desc", goods_param_desc_str[i]);
				r_goods.setValue("goods_param", goods_param_str[i]);
				r_goods.setValue("bespeak_value", bespeak_value_str[i]);
			}
			r_goods.sort("bespeak_value");
			session.setAttribute("OrderGoodsRecordset", r_goods);
		} catch (Throwable e) {
			throw e;
		}
		return 0 ;
	}
}
