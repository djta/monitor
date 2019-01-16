package transactions.project.weixin.store.order;

import java.sql.Connection;
import java.sql.Types;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import transactions.project.weixin.store.StoreException;
import dinamica.AbstractModule;
import dinamica.Config;
import dinamica.Db;
import dinamica.Recordset;
import dinamica.RecordsetException;
import dinamica.StringUtil;

public class OrderGoods extends AbstractModule{
	
	private Recordset goods = new Recordset();
	private static String query_goods_sql_file = "order/query-goods.sql";
	public OrderGoods(String goods_id, Integer tenantry_id, Integer goods_num, String goods_param, String order_date, String order_time,
			ServletContext ctx, HttpServletRequest req, HttpServletResponse res, Config config, Connection con) throws Throwable {
		super.init(ctx, req, res);
		super.setConfig(config);
		super.setConnection(con);
		goods.append("goods_id", java.sql.Types.VARCHAR);
		goods.append("tenantry_id", java.sql.Types.INTEGER);
		goods.append("goods_num", java.sql.Types.INTEGER);
		goods.append("actual_price", java.sql.Types.DOUBLE);
		goods.append("goods_param", Types.VARCHAR);
		goods.append("order_date", Types.VARCHAR);
		goods.append("order_time", Types.VARCHAR);
		Db db = getDb();
		String sql_goods = getLocalResource(query_goods_sql_file);
		Recordset r = db.get(StringUtil.replace(sql_goods, "${goods_id}", goods_id));
		if(r.next()){
			goods.addNew();
			setGoods_id(goods_id);
			setTenantry_id(r.getInteger("tenantry_id"));
			setActual_price(r.getDouble("actual_price"));
			setGoods_num(goods_num);
			this.setGoods_param(goods_param);
			this.setOrder_date(order_date);
			this.setOrder_time(order_time);
		}else{
			throw new StoreException("订单中存在未知商品");
		}
	}
	public Recordset getRecordset(){
		return goods;
	}
	public String getGoods_id() throws Throwable {
		return goods.getRecordCount()>0?goods.getString("goods_id"):null;
	}
	public void setGoods_id(String goods_id) throws RecordsetException {
		this.goods.setValue("goods_id", goods_id);
	}
	public Integer getTenantry_id() throws Throwable {
		return goods.getRecordCount()>0?goods.getInteger("tenantry_id"):null;
	}
	public void setTenantry_id(Integer tenantry_id) throws RecordsetException {
		this.goods.setValue("tenantry_id", tenantry_id);
	}
	public Double getActual_price() throws Throwable {
		return goods.getRecordCount()>0?goods.getDouble("actual_price"):null;
	}
	public void setActual_price(Double actual_price) throws RecordsetException {
		this.goods.setValue("actual_price", actual_price);
	}
	public Integer getGoods_num() throws Throwable {
		return goods.getRecordCount()>0?goods.getInteger("goods_num"):null;
	}
	public void setGoods_num(Integer goods_num) throws RecordsetException {
		this.goods.setValue("goods_num", goods_num);
	}
	public String getGoods_param() throws Throwable {
		return goods.getRecordCount()>0?goods.getString("goods_param"):null;
	}
	public void setGoods_param(String goods_param) throws RecordsetException {
		this.goods.setValue("goods_param", (null == goods_param? "" : goods_param));
	}
	public String getOrder_date() throws Throwable {
		return goods.getRecordCount()>0?goods.getString("order_date"):null;
	}
	public void setOrder_date(String order_date) throws RecordsetException {
		this.goods.setValue("order_date", (null == order_date? "" : order_date));
	}
	public String getOrder_time() throws Throwable {
		return goods.getRecordCount()>0?goods.getString("order_time"):null;
	}
	public void setOrder_time(String order_time) throws RecordsetException {
		this.goods.setValue("order_time", (null == order_time? "" : order_time));
	}
}
