update ws_stock_pile
set quantity = quantity-${fld:quantity}
,updated = {ts '${def:timestamp}'}
,updatedby = 'system'
where
stock_pile_id = ${fld:stock_pile_id}
and quantity >= ${fld:quantity}

