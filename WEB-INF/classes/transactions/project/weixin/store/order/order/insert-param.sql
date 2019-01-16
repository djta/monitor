INSERT INTO ws_sale_order_param(
	tuid,
	sale_order_id,
	goods_id,
	goods_param_id,
	template_id,
	category_id,
	category_name,
	param_name,
	param_type,
	param_value_type,
	from_param_value,
	from_compare_type,
	to_param_value,
	to_compare_type,
	param_value,
	order_detail_id
) 
SELECT 
	${seq:nextval@seq_ws_sale_order_param},
	'${sale_order_id}',
	'${goods_id}',
	p.tuid,
	p.template_id,
	p.category_id,
	c.category_name,
	p.param_name,
	p.param_type,
	p.param_value_type,
	p.from_param_value,
	p.from_compare_type,
	p.to_param_value,
	p.to_compare_type,
	'${param_value}',
	${seq:currval@seq_ws_sale_order_detail}
FROM ws_goods_param p 
LEFT JOIN ws_param_template_category c ON p.category_id = c.tuid 
WHERE p.tuid = ${param_id}
