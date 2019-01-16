SELECT 
	o.sale_order_id,
	o.user_code,
	o.is_service_order,
	o.org_id,
	f.user_id AS person_id,
	f.userlogin AS person_userlogin,
	(SELECT g.org_id FROM hr_org g WHERE g.is_deleted = '0' AND g.pid = 0 ORDER BY org_id) AS plat_org_id 
FROM ws_trade_order t 
INNER JOIN ws_sale_order o ON t.tuid = o.trade_order_id  
INNER JOIN hr_staff f ON o.user_code = f.userlogin 
WHERE t.trade_order_code = '${sale_order_code}' 
