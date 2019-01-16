INSERT 
INTO 
ws_account_list
VALUES (
	nextval('seq_ws_account_list'), 
	(select account_id from ws_account t1  where t1.userlogin=(select distinct userlogin from hr_staff_weixin sw where sw.weixin_userid = '${weixin_id}')), 
	(select distinct userlogin from hr_staff_weixin sw where sw.weixin_userid = '${weixin_id}'), 
	'${goods_id}', 
	'${amount_fee}', 
	'10', 
	(select account_balance from ws_account t1 join hr_staff_weixin t2 on t1.userlogin=t2.userlogin and t2.weixin_userid = '${weixin_id}'), 
	'1', 
	now(),
	(select distinct userlogin from hr_staff_weixin sw where sw.weixin_userid = '${weixin_id}'), 
	null, 
	null, 
	null,
	'${sale_order_code}'
);