select
ws_trade_order.state,
ws_trade_order.createdby
FROM
ws_trade_order
WHERE
tuid=${trade_order_id}