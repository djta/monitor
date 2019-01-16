INSERT INTO ws_trade_order(
	tuid,
	created,
	createdby,
	state,
	total_price,
	trade_order_code
) VALUES(
	${fld:trade_order_id}
	,{ts '${def:timestamp}'}
	,${fld:userlogin}
	,0
	,${fld:total_price}
	,${fld:trade_order_code}
)