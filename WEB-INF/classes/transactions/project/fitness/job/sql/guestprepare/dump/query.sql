SELECT 
	code, org_id 
FROM cc_guest_prepare 
WHERE preparedate < '${def:date}'::date 
AND status = 1
