update
ws_sale_order
set
state='30',updated = {ts '${def:timestamp}'},createdby='Payment'
where 
trade_order_id = (
	SELECT tuid FROM ws_trade_order WHERE trade_order_code = '${sale_order_code}'
)


