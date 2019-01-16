select
stock_pile_id
,storehouse_id
,quantity
from ws_stock_pile
where
goods_id = ${fld:goods_id}
order by quantity desc