select 
t1.appid
,t2.partner_id
,t2.mchid
,t2.notify_url
,t2.partner_key
,(select total_price from ws_trade_order where tuid=${out_trade_no}) amount
,(
SELECT 
	ARRAY_AGG(g.goods_name) AS out_trade_name
FROM ws_trade_order t
INNER JOIN ws_sale_order s ON t.tuid = s.trade_order_id 
INNER JOIN ws_sale_order_detail sd ON sd.sale_order_id = s.sale_order_id 
INNER JOIN ws_goods g ON sd.goods_id = g.goods_id 
WHERE t.tuid = ${out_trade_no}::integer
) as body,
(select trade_order_code from ws_trade_order where tuid=${out_trade_no}) as trade_order_code
 From wx_service t1 
 join wx_pay t2 on t1.pay_id=t2.tuid