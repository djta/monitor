package transactions.project.weixin.store.goods;


import dinamica.GenericTableManager;
import dinamica.Recordset;
/**
 * 购物车数据存放session中
 * @author CaoNianXin
 *
 */
public class shoppingCart extends GenericTableManager {
	@Override
	public int service(Recordset inputParams) throws Throwable {
		int rc = super.service(inputParams);
		Recordset  rs = null;
		if( null != getSession().getAttribute("_shopingCart") ){
			rs  = (Recordset)getSession().getAttribute("_shopingCart");
			int goods_id= inputParams.getInt("goods_id");
			int shop_number_change=inputParams.getInt("shop_number_change");
			int recNum = rs.findRecord("goods_id", goods_id);
			if(recNum >= 0){
				rs.setRecordNumber(recNum);
				int num=rs.getInt("shop_number_change");
				num=num+=shop_number_change;
				//double actual_pricem= Double.parseDouble(rs.getString("actual_price"));
				rs.setValue("shop_number_change", num);
				//rs.setValue("actual_price", actual_pricem);
			}else{
				rs.addNew();
				inputParams.copyValues(rs);
			}
		}else{
			rs = inputParams;
		}
		getSession().setAttribute("_shopingCart", rs);
		return rc;
	}
}
