SELECT 
	t2.price*t2.quantity AS price,
	g.goods_id,
	g.plat_rate,
	g.person_rate 
FROM ws_sale_order_detail t2 
INNER JOIN ws_goods g ON t2.goods_id = g.goods_id 
WHERE t2.sale_order_id = ${fld:sale_order_id} 
