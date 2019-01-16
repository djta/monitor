select  sum(t3.actual_price*t2.quantity) as amount_fee,t3.goods_id
FROM ws_trade_order t 
INNER JOIN ws_sale_order  t1 ON t.tuid = t1.trade_order_id 
join ws_sale_order_detail t2 on t1.sale_order_id=t2.sale_order_id
join ws_goods t3 on t2.goods_id=t3.goods_id
where t.trade_order_code='${sale_order_code}'
AND
	goods_catogry='1'
group by t3.goods_id
