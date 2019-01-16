SELECT count(1) AS total 
FROM cc_captcha c 
INNER JOIN cc_sms s ON c.sms_id = s.tuid::integer 
WHERE c.created >= '${start_time}'::timestamp 
AND c.created < '${end_time}'::timestamp 
AND c.mobile = '${mobile}' AND s.status = '1'
