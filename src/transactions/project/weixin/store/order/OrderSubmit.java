package transactions.project.weixin.store.order;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import transactions.project.util.Tools;
import dinamica.Db;
import dinamica.GenericTransaction;
import dinamica.Recordset;

public class OrderSubmit extends GenericTransaction{
	@SuppressWarnings("deprecation")
	public int service(Recordset inputParams) throws Throwable{
		super.service(inputParams);
		
		Recordset order = new Recordset();
		order.append("trade_order_id", java.sql.Types.VARCHAR);
		order.append("url", java.sql.Types.VARCHAR);
		order.addNew();
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
			Db db= getDb();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			TradeOrder t = (TradeOrder) loader.loadClass("transactions.project.weixin.store.order.TradeOrder").newInstance();
			t.init(this.getContext(), this.getRequest(), this.getResponse());
			t.setConfig(this.getConfig());
			t.setConnection(this.getConnection());
			t.service(inputParams);

			Integer tradeOrderId = t.getTradeOrderId();
			Recordset r = db.get(getSQL("select '${def:user}' as user,'${def:tenantry}' as tenantry from dual",null));
			if(r.next()){
				t.init_(r.getString("user"),r.getString("tenantry"));
				tradeOrderId = t.getTradeOrderId();
				order.setValue("trade_order_id", tradeOrderId);
			}else{
				return 0;
			}
			String queryOrg = getLocalResource("order/query-goods-org.sql");
			String[] goods_id_arr = getRequest().getParameterValues("goods_id");
			String[] goods_num_arr = getRequest().getParameterValues("goods_num");
			String[] tenantry_id_arr = getRequest().getParameterValues("tenantry_id");
			String[] goods_param_arr = getRequest().getParameterValues("goods_param");
			String[] bespeak_value_arr = getRequest().getParameterValues("bespeak_value");
			String[] goods_remark_arr = getRequest().getParameterValues("goods_remark");
			/** 按预约时间将订单分组 */
			List<String> bespeakList = new ArrayList<String>();
			List<String> bespeakIndexList = new ArrayList<String>();
			int goodsLen = goods_id_arr.length;
			for (int i = 0; i < goodsLen; i++) {
				String bespeak_value = "-1";
				if( null != bespeak_value_arr[i] && !"".equals(bespeak_value_arr[i]) ){
					bespeak_value = bespeak_value_arr[i];
				}
				String org_id = this.queryGoodsOrg(db, goods_id_arr[i], tenantry_id_arr[i], queryOrg);
				String bsvalue = bespeak_value + "@" + org_id;
				int idx = -1;
				if( bespeakList.size() > 0 && bespeakList.contains(bsvalue) ){
					idx = bespeakList.indexOf(bsvalue);
					String _value = bespeakIndexList.get(idx);
					_value = _value + "," + String.valueOf(i);
					bespeakIndexList.set(idx, _value);
				}else{
					bespeakList.add(bsvalue);
					bespeakIndexList.add(String.valueOf(i));
				}
			}

			Double total_price = 0d;
			Date curDate = new Date();
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
			int bespeakLen = bespeakList.size();
			List<SalesOrder> saleOrderList = new ArrayList<SalesOrder>();
			for( int k = 0; k < bespeakLen; k++ ){
				String value = bespeakList.get(k);
				String[] tempVal = value.split("@");
				String bespeakValue = tempVal[0];	// 预约时间
				Integer org_id = Integer.parseInt(tempVal[1]);	// 商家编号
				bespeakValue = ( null == bespeakValue || "-1".equals(bespeakValue) ? "" : bespeakValue );
				String orderDate = "";
				String orderTime = "";
				if( StringUtils.isNotBlank(bespeakValue) ){
					String[] temp = bespeakValue.split(" ");
					orderDate = temp[0];
					orderTime = temp[1];
				}else{
					orderDate = sdfDate.format(curDate);
					orderTime = sdfTime.format(curDate);
				}
				
				String remark = "";
				List<OrderGoods> goodsList = new ArrayList<OrderGoods>();
				// index
				String indexStr = bespeakIndexList.get(k);
				String[] indexArr = indexStr.split(",");
				int indexLen = ( null != indexArr ? indexArr.length : 0 );
				Double price = 0d;
				for( int j = 0; j < indexLen; j++ ){
					int idx = Integer.parseInt(indexArr[j]);
					int tenantry_id = Integer.parseInt(tenantry_id_arr[idx]);
					int goods_num = Integer.parseInt(goods_num_arr[idx]);
					String goods_id = goods_id_arr[idx];
					String goods_param = (null != goods_param_arr && null != goods_param_arr[idx] ? goods_param_arr[idx] : "");
					OrderGoods og = new OrderGoods(goods_id, tenantry_id,goods_num, goods_param, orderDate, orderTime,
									getContext(),getRequest(),getResponse(),this.getConfig(),this.getConnection());
					goodsList.add(og);
					price = Tools.addDoubleValue(price, og.getActual_price()*og.getGoods_num());
					if( null != goods_remark_arr && null != goods_remark_arr[idx] && !"".equals(goods_remark_arr[idx]) ){
						remark = ( StringUtils.isNotBlank(remark) ? (remark + "  " + goods_remark_arr[idx]) : goods_remark_arr[idx] );
					}
				}
				total_price = Tools.addDoubleValue(total_price, price);
				String sale_order_id = t.createSaleOrderId();
				SalesOrder saleOrder = new SalesOrder(sale_order_id, tradeOrderId, org_id, orderDate, orderTime, price, remark, 
										getContext(), getRequest(), getResponse(), this.getConfig(), this.getConnection());
				saleOrder.setGoodsList(goodsList);
				saleOrderList.add(saleOrder);
			}
			t.setTotalPrice(total_price);
			t.setSaleOrderList(saleOrderList);
			t.submitOrder(db);
			//清空session中的购物车
			Recordset rs = (Recordset)getSession().getAttribute("OrderGoodsRecordset");
			getSession().setAttribute("OrderGoodsRecordset", rs.copyStructure());
			Recordset queryAppRecordset=db.get(getSQL(getResource("query-service.sql"), inputParams));
			queryAppRecordset.first();
			String appidString=queryAppRecordset.getString("app_id");
			String apiUrl=queryAppRecordset.getString("api_url");
			String isWeixinString=getRequest().getParameter("is_weixin");
			String url=null;
			if("1".equals(isWeixinString)){
				 url="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appidString+"&redirect_uri="+URLEncoder.encode(apiUrl+"/action/project/weixin/pay/crud?out_trade_no="+String.valueOf(tradeOrderId))+"&response_type=code&scope=snsapi_base&state=1#wechat_redirect";
			}else{
				 //url=apiUrl+"/action/project/taobao/crud?out_trade_no="+t.getSaleOrderId();
				url=apiUrl+"/action/project/taobao/crud?out_trade_no="+String.valueOf(tradeOrderId);
			}
			order.setValue("url", url);
			publish("order",order);
		} catch (Throwable e) {
			throw e;
		}
		return 0 ;
	}
	
	private String queryGoodsOrg(Db db, String goods_id, String tenantry_id, String queryOrg) throws Throwable{
		String _queryOrg = queryOrg;
		_queryOrg = StringUtils.replace(_queryOrg, "${goods_id}", goods_id);
		_queryOrg = StringUtils.replace(_queryOrg, "${tenantry_id}", tenantry_id);
		Recordset rs = db.get(_queryOrg);
		if( rs.getRecordCount() <= 0 ){
			throw new Throwable("无法获取商品[" + goods_id + "]的所属商家");
		}
		rs.first();
		Integer org_id = rs.getInteger("org_id");
		return String.valueOf(org_id);
	}
}
