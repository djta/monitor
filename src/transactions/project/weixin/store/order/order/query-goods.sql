select
tenantry_id
--,channel_id
,market_price
,actual_price
from ws_goods
where
goods_id = '${goods_id}'