package transactions.project.weixin.store.order;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import transactions.project.weixin.common.WeixinUtil;
import transactions.project.weixin.store.StoreException;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class TradeOrder extends GenericTransaction{
	private Map<String,List<OrderGoods>> order_goods_map = new HashMap<String,List<OrderGoods>>();
	private Map<String,Recordset> stock_leave_map = new HashMap<String,Recordset>();
	private Map<String,Recordset> stock_leave_detail_map = new HashMap<String,Recordset>();
	private static String order_sql_file = "order/insert-order.sql";
	private static String order_detail_sql_file = "order/insert-order_detail.sql";
	private static String param_sql_file = "order/insert-param.sql";
	private static String query_stock_sql_file = "order/query-stock.sql";
	private static String update_stock_sql_file = "order/update-stock.sql";
	private static String leave_stock_sql_file = "order/insert-stock_leave.sql";
	private static String leave_stock_detail_sql_file = "order/insert-stock_leave_detail.sql";
	/**private static String query_price_sql_file = "order/query-price.sql";
	private static String update_account_sql_file = "order/update-account.sql";
	private static String account_detail_sql_file = "order/insert-account_detail.sql";*/
	private static String trade_order_sql_file = "order/insert-trade-order.sql";
	private Integer trade_order_id = null;
	private OrderInfo order_inf = null;
	private Recordset rsSaleOrder = new Recordset();
	private Double total_price = null;
	private List<SalesOrder> saleOrderList = new ArrayList<SalesOrder>();
	@Override
	public int service(Recordset inputParams) throws Throwable{
		return super.service(inputParams);
	}
	public void init_ (String userlogin, String tenantry_id) throws Throwable{
		order_goods_map.clear();
		stock_leave_map.clear();
		stock_leave_detail_map.clear();
		saleOrderList = new ArrayList<SalesOrder>();
		rsSaleOrder = new Recordset();
		trade_order_id = null;
		total_price = null;
		order_inf = new OrderInfo(userlogin,tenantry_id);
	}
	
	public void setSaleOrderList(List<SalesOrder> saleOrderList){
		this.saleOrderList = saleOrderList;
	}
	
	public void addOrderGoods(String sale_order_id, List<OrderGoods> order_goods_list){
		order_goods_map.put(sale_order_id, order_goods_list);
	}
	public void deleteOrderGoods(String goods_id){
		order_goods_map.remove(goods_id);
	}
	
	public boolean submitOrder(Db db) throws Throwable{
		try{
			submitDAO(db);
		}catch(StoreException e){
			throw e;
		}catch(Exception e){
			throw new Throwable("系统错误，提交订单失败");
		}
		return true;
	}
	
	private boolean submitDAO(Db db) throws Throwable{
		try{
			// 新增交易订单
			addTradeOrder(db);
			int orderLen = this.saleOrderList.size();
			for( int i = 0; i < orderLen; i++ ){
				SalesOrder saleOrder = saleOrderList.get(i);
				String sale_order_id = saleOrder.getSale_order_id();	// 销售单编号
				rsSaleOrder = saleOrder.getRecordset();
				// 新增销售订单
				addOrderBatch(db, rsSaleOrder);
				List<OrderGoods> goodsList = saleOrder.getGoodsList();	// 订单商品列表
				// 新增订单商品、商品参数
				addOrderDetailBatch(db, goodsList, sale_order_id);
				addStockUpdateBatch(db, goodsList);
				addStockLeaveAndDetailBatch(db);
			}
			db.beginTrans();
			int [] result = db.exec();
			if(arraySum(result) < result.length){
				db.rollback();
				throw new StoreException("数据错误，提交订单失败");
			}
			db.commit();
		}catch(Throwable e){
			db.rollback();
			e.printStackTrace();
			throw e;
		}
		return true;
	}
	private void addOrderBatch(Db db, Recordset rsSaleOrder) throws Throwable{
		String sql;
		try {
			sql = getLocalResource(order_sql_file);
			String sql_order = getSQL(sql,order_inf.getRecordset());
			sql_order = getSQL(sql_order, rsSaleOrder);
			db.addBatchCommand(sql_order);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	private void addTradeOrder(Db db) throws Throwable{
		try {
			String sql = getLocalResource(trade_order_sql_file);
			sql = getSQL(sql, order_inf.getRecordset());
			sql = StringUtils.replace(sql, "${fld:total_price}", String.valueOf(total_price));
			db.addBatchCommand(sql);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	private void addOrderDetailBatch(Db db, List<OrderGoods> goodsList, String sale_order_id) throws Throwable{
		String sql;
		try {
			order_goods_map.put(sale_order_id, goodsList);
			sql = getLocalResource(order_detail_sql_file);
			int len = ( null != goodsList ? goodsList.size() : 0 );
			for( int i = 0; i < len; i++ ){
				OrderGoods goods = goodsList.get(i);
				String sql_order_detail = getSQL(sql,goods.getRecordset());
				sql_order_detail = StringUtils.replace(sql_order_detail, "${fld:sale_order_id}", "'"+sale_order_id+"'");
				db.addBatchCommand(sql_order_detail);
				
				String goods_id = goods.getGoods_id();
				// 保存订单商品参数
				String goods_param_str = goods.getGoods_param();
				if( StringUtils.isBlank(goods_param_str) ){
					continue;
				}
				String insertParam = getLocalResource(param_sql_file);
				String[] temp = goods_param_str.split("#@#");
				int paramLen = ( null != temp ? temp.length : 0 );
				for( int p = 0; p < paramLen; p++ ){
					if( null == temp[p] || "".equals(temp[p]) ){
						continue;
					}
					String[] param = temp[p].split("&&&");
					if( null == param || param.length < 2 ){
						continue;
					}
					String _insertParam = getSQL(insertParam, null);
					_insertParam = StringUtils.replace(_insertParam, "${sale_order_id}", sale_order_id);
					_insertParam = StringUtils.replace(_insertParam, "${goods_id}", goods_id);
					_insertParam = StringUtils.replace(_insertParam, "${param_id}", param[0]);
					_insertParam = StringUtils.replace(_insertParam, "${param_value}", param[1]);
					db.addBatchCommand(_insertParam);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	private void addStockUpdateBatch(Db db, List<OrderGoods> goodsList) throws Throwable{
		String sql_query;
		String sql_update;
		try {
			sql_query = getLocalResource(query_stock_sql_file);
			sql_update = getLocalResource(update_stock_sql_file);
			int len = ( null != goodsList ? goodsList.size() : 0 );
			for( int i = 0; i < len; i++ ){
				OrderGoods goods = goodsList.get(i);
				Integer goods_num = goods.getGoods_num();
				String sql_stock_query = getSQL(sql_query,goods.getRecordset());
				Recordset r_stock = db.get(sql_stock_query);
				r_stock.top();
				while(r_stock.next()){
					int stock_num = r_stock.getInt("quantity");
					String storehouse_id = r_stock.getString("storehouse_id");
					Recordset r_stock_leave = stock_leave_map.get(storehouse_id);
					if(r_stock_leave==null){
						r_stock_leave = new Recordset();
						r_stock_leave.append("storehouse_id", java.sql.Types.VARCHAR);
						r_stock_leave.append("goods_num_total", java.sql.Types.INTEGER);
						r_stock_leave.addNew();
						r_stock_leave.setValue("storehouse_id", storehouse_id);
					}
					Recordset r_stock_leave_detail = stock_leave_detail_map.get(storehouse_id);
					if(r_stock_leave_detail==null){
						r_stock_leave_detail = new Recordset();
						r_stock_leave_detail.append("storehouse_id", java.sql.Types.VARCHAR);
						r_stock_leave_detail.append("goods_id", java.sql.Types.VARCHAR);
						r_stock_leave_detail.append("goods_num", java.sql.Types.INTEGER);
						r_stock_leave_detail.append("price", java.sql.Types.DOUBLE);
					}
					r_stock_leave_detail.addNew();
					r_stock_leave_detail.setValue("storehouse_id", storehouse_id);
					if(goods_num >= stock_num){
						r_stock_leave.setValue("goods_num_total", r_stock_leave.isNull("goods_num_total")?stock_num:r_stock_leave.getInteger("goods_num_total")+stock_num);
						r_stock_leave_detail.setValue("goods_id",goods.getGoods_id());
						r_stock_leave_detail.setValue("goods_num",stock_num);
						r_stock_leave_detail.setValue("price",goods.getActual_price());
						goods_num -= stock_num;
						String sql_stock_update = getSQL(sql_update,r_stock);
						db.addBatchCommand(sql_stock_update);
					}else{
						r_stock_leave.setValue("goods_num_total", r_stock_leave.isNull("goods_num_total")?goods_num:r_stock_leave.getInteger("goods_num_total")+goods_num);
						r_stock_leave_detail.setValue("goods_id",goods.getGoods_id());
						r_stock_leave_detail.setValue("goods_num",goods_num);
						r_stock_leave_detail.setValue("price",goods.getActual_price());
						r_stock.setValue("quantity", goods_num);
						goods_num = 0;
						String sql_stock_update = getSQL(sql_update,r_stock);
						db.addBatchCommand(sql_stock_update);
					}
					stock_leave_map.put(storehouse_id, r_stock_leave);
					stock_leave_detail_map.put(storehouse_id, r_stock_leave_detail);
					if(goods_num == 0){
						break;
					}
				}
				
			}
		} catch (Throwable e) {
			
			e.printStackTrace();
		}
	}
	private void addStockLeaveAndDetailBatch(Db db) throws Throwable{
		String sql_leave;
		String sql_leave_detail;
		try {
			sql_leave = getLocalResource(leave_stock_sql_file);
			sql_leave_detail = getLocalResource(leave_stock_detail_sql_file);
			Iterator<Entry<String, Recordset>> i = stock_leave_map.entrySet().iterator();
			while(i.hasNext()){
				Recordset r_leave = i.next().getValue();
				String storehouse_id = r_leave.getString("storehouse_id");
				String sql_stock_leave = getSQL(sql_leave,r_leave);
				db.addBatchCommand(sql_stock_leave);
				Recordset r_leave_detail = stock_leave_detail_map.get(storehouse_id);
				r_leave_detail.top();
				while(r_leave_detail.next()){
					String sql_stock_leave_detail = getSQL(sql_leave_detail,r_leave_detail);
					db.addBatchCommand(sql_stock_leave_detail);
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	/**
	private void addAccountUpdateBatch(Db db){
		String sql;
		try {
			sql = getLocalResource(update_account_sql_file);
			if(!order_goods_map.isEmpty()){
				String sql_account = StringUtil.replace(sql, "${change_money}", Double.toString(getBillSum(db)));
				sql_account = getSQL(sql_account,null);
				db.addBatchCommand(sql_account);
			}
			
		} catch (Throwable e) {
			
			e.printStackTrace();
		}
	}
	private void addAccountDetailBatch(Db db){
		String sql;
		try {
			sql = getLocalResource(account_detail_sql_file);
			if(!order_goods_map.isEmpty()){
				String sql_account_detail = StringUtil.replace(sql, "${change_money}", Double.toString(getBillSum(db)));
				sql_account_detail = getSQL(sql_account_detail,null);
				db.addBatchCommand(sql_account_detail);
			}
			
		} catch (Throwable e) {
			
			e.printStackTrace();
		}
	}
	private double getBillSum(Db db){
		String sql;
		Double sum = 0d;
		try {
			sql = getLocalResource(query_price_sql_file);
			Iterator<Entry<String, List<OrderGoods>>> i = order_goods_map.entrySet().iterator();
			while(i.hasNext()){
				List<OrderGoods> list = (List<OrderGoods>)i.next().getValue();
				int len = (null != list ? list.size() : 0);
				for( int t = 0; t < len; t++ ){
					OrderGoods goods = list.get(t);
					String sql_account = getSQL(sql,goods.getRecordset());
					sum = Tools.addDoubleValue(sum, db.getDoubleColValue(sql_account, "bill_sum"));
				}
			}
		} catch (Throwable e) {
			
			e.printStackTrace();
		}
		return sum;
	}*/
	
	/**
	 * 计算整数数组数值和
	 * @param a 数组
	 * @return 和
	 */
	private int arraySum(int[] a){
		if(a == null || a.length == 0){
			return 0;
		}
		int sum = 0;
		for(int i=0; i<a.length; i++){
			sum+=a[i];
		}
		return sum;
	}
	
	public void cancelOrder(String order_id) throws StoreException{
		if(order_id==null || order_id.length()==0){
			throw new StoreException("发生错误");
		}
	}
	
	public String createSaleOrderId() throws Throwable{
		Recordset r = getDb().get(getSQL("select ${seq:nextval@seq_ws_sale_order} as sale_order_id from dual",null));
		if(r.getRecordCount() <= 0){
			throw new Throwable("序列错误");
		}else{
			r.first();
			String sale_order_id = r.getString("sale_order_id");
			return sale_order_id;
		}
	}
	
	public void setTradeOrderId(Integer id){
		trade_order_id = id;
	}
	public Integer getTradeOrderId(){
		return trade_order_id;
	}

	public void setTotalPrice(Double total_price){
		this.total_price = total_price;
	}
	public Double getTotalPrice(){
		return total_price;
	}
	
	class OrderInfo{
		private Recordset order_info = new Recordset();
		public OrderInfo(String userlogin, String tenantry_id) throws Throwable{
			order_info.append("userlogin", java.sql.Types.VARCHAR);
			order_info.append("tenantry_id", java.sql.Types.INTEGER);
			order_info.append("trade_order_id", Types.INTEGER);
			order_info.append("trade_order_code", Types.VARCHAR);
			order_info.addNew();
			order_info.setValue("userlogin", userlogin);
			if("".equals(tenantry_id)){
				order_info.setValue("tenantry_id", null);
			}else{
				order_info.setValue("tenantry_id", Integer.parseInt(tenantry_id));
			}
			Recordset r = getDb().get(getSQL("select ${seq:nextval@seq_ws_trade_order} as trade_order_id from dual",null));
			if(r.getRecordCount() <= 0){
				throw new Throwable("序列错误");
			}else{
				r.first();
				Integer trade_order_id = r.getInteger("trade_order_id");
				String trade_order_code = WeixinUtil.create_timestamp()+WeixinUtil.create_nonce_str().substring(0, 22);
				order_info.setValue("trade_order_id", trade_order_id);
				order_info.setValue("trade_order_code", trade_order_code);
				setTradeOrderId(trade_order_id);
			}
		}
		public Recordset getRecordset(){
			return order_info;
		}
	}
}
