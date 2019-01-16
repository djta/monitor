update
ws_account
set
account_balance=account_balance
+(
		select 
		case when sum(t3.actual_price*t2.quantity) is null then 0 else  sum(actual_price) end 
		FROM ws_trade_order t 
		INNER JOIN ws_sale_order t1 ON t1.trade_order_id = t.tuid 
		join ws_sale_order_detail t2
		on t1.sale_order_id=t2.sale_order_id
		join ws_goods t3 on t2.goods_id=t3.goods_id
		WHERE t.trade_order_code='${sale_order_code}'
		and
		commodity_type='9'
)
where
userlogin=(select distinct userlogin from hr_staff_weixin where weixin_userid='${weixin_id}')
