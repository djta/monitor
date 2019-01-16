select
actual_price*${fld:goods_num} as bill_sum
from ws_goods
where
goods_id = ${fld:goods_id}