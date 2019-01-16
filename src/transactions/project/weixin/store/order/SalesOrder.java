package transactions.project.weixin.store.order;

import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dinamica.AbstractModule;
import dinamica.Config;
import dinamica.Recordset;
import dinamica.RecordsetException;

public class SalesOrder extends AbstractModule{
	
	private List<OrderGoods> goodsList = new ArrayList<OrderGoods>();
	private Recordset order = new Recordset();
	
	public SalesOrder(String sale_order_id, Integer trade_order_id, Integer org_id, String order_date, String order_time, Double total_price, String remark,
			ServletContext ctx, HttpServletRequest req, HttpServletResponse res, Config config, Connection con) throws Throwable {
		super.init(ctx, req, res);
		super.setConfig(config);
		super.setConnection(con);
		order.append("sale_order_id", Types.VARCHAR);
		order.append("trade_order_id", Types.INTEGER);
		order.append("org_id", Types.INTEGER);
		order.append("order_date", Types.VARCHAR);
		order.append("order_time", Types.VARCHAR);
		order.append("total_price", Types.DOUBLE);
		order.append("remark", Types.VARCHAR);
		
		order.addNew();
		order.setValue("sale_order_id", sale_order_id);
		order.setValue("trade_order_id", trade_order_id);
		order.setValue("org_id", org_id);
		order.setValue("order_date", order_date);
		order.setValue("order_time", order_time);
		order.setValue("total_price", total_price);
		order.setValue("remark", remark);
	}
	
	public Recordset getRecordset(){
		return order;
	}
	
	public String getSale_order_id() throws Throwable {
		return order.getRecordCount()>0?order.getString("sale_order_id"):null;
	}
	public void setSale_order_id(String sale_order_id) throws RecordsetException {
		this.order.setValue("sale_order_id", sale_order_id);
	}
	
	public Integer getTrade_order_id() throws Throwable {
		return order.getRecordCount()>0?order.getInteger("trade_order_id"):null;
	}
	public void setTrade_order_id(Integer trade_order_id) throws RecordsetException {
		this.order.setValue("trade_order_id", trade_order_id);
	}

	public String getOrder_date() throws Throwable {
		return order.getRecordCount()>0?order.getString("order_date"):null;
	}
	public void setOrder_date(String order_date) throws RecordsetException {
		this.order.setValue("order_date", (null == order_date? "" : order_date));
	}
	
	public String getOrder_time() throws Throwable {
		return order.getRecordCount()>0?order.getString("order_time"):null;
	}
	public void setOrder_time(String order_time) throws RecordsetException {
		this.order.setValue("order_time", (null == order_time? "" : order_time));
	}
	
	public Double getTotal_price() throws Throwable {
		return order.getRecordCount()>0?order.getDouble("total_price"):null;
	}
	public void setTotal_price(Double total_price) throws RecordsetException {
		this.order.setValue("total_price", total_price);
	}
	
	public List<OrderGoods> getGoodsList() {
		return goodsList;
	}
	public void setGoodsList(List<OrderGoods> goodsList) {
		this.goodsList = goodsList;
	}
	public void addGoods(OrderGoods goods) {
		this.goodsList.add(goods);
	}
}
