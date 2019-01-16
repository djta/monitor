SELECT 
	c.org_id 
FROM ws_channeL c 
INNER JOIN ws_goods_position gp ON c.channel_id = gp.channel_id 
INNER JOIN ws_goods g ON gp.goods_id = g.goods_id 
INNER JOIN ws_mall m ON c.mall_id = m.mall_id 
WHERE g.goods_id = '${goods_id}'
AND m.tenantry_id = ${tenantry_id}
