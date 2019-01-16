select
goods_name
,goods_id as tuid
,0 as transport_fee
,actual_price*100 as product_fee
from
ws_goods
where goods_id='${tuid}'