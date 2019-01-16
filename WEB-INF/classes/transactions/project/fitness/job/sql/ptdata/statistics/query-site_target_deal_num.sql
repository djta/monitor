SELECT 
	COUNT(1) as sitetargetdealnum
FROM cc_contract t 
WHERE contracttype = 0 and type = 2 and org_id = ${fld:org_id} and status >= 2
and to_char(createdate::date,'YYYYMM')=to_char('${def:date}'::date - interval '1 month','YYYYMM')
AND (salemember = ${fld:userlogin} OR salemember1 = ${fld:userlogin}) 
