SELECT
	t2.appid,
	t3.mchid AS mch_id,
	(SELECT weixin_lastlogin FROM hr_staff sw WHERE sw.userlogin = t1.userlogin LIMIT 1) AS openid,
	notify_url,
	'JSAPI' AS trade_type,
	t3.partner_key AS partnerkey,
	(
		SELECT SUM(normalmoney) 
		FROM cc_siteusedetail t 
		WHERE t.code = '${out_trade_no}'
	) AS total_fee,
	(
		SELECT (select sitename from cc_sitedef where code = t.sitecode) 
		FROM cc_siteusedetail t 
		WHERE t.code = '${out_trade_no}'
	) AS body,
	'${out_trade_no}' AS out_trade_code
FROM hr_staff t1
JOIN wx_service t2 ON t1.weixin_service_id = t2.tuid
JOIN wx_pay t3 ON t2.pay_id = t3.tuid
WHERE t1.userlogin = '${userlogin}'
