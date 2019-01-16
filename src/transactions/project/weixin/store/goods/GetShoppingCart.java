package transactions.project.weixin.store.goods;


import dinamica.GenericTransaction;
import dinamica.Recordset;
/**
 * 从session中获取购物车数据
 * @author YaoNianXin
 *
 */
public class GetShoppingCart extends GenericTransaction {
	@Override
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		if(getSession().getAttribute("OrderGoodsRecordset")==null){
			Recordset r_goods=new Recordset();
			r_goods.append("goods_id", java.sql.Types.VARCHAR);
			r_goods.append("goods_name", java.sql.Types.VARCHAR);
			r_goods.append("goods_num", java.sql.Types.INTEGER);
			r_goods.append("tenantry_id", java.sql.Types.INTEGER);
			r_goods.append("goods_price", java.sql.Types.DOUBLE);
			publish("OrderGoodsRecordset",r_goods);
		}else{
			publish("OrderGoodsRecordset",(Recordset) getSession().getAttribute("OrderGoodsRecordset"));
		}
		return rc;
	}
}
