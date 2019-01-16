SELECT SUM(CASE WHEN salemember1 IS NOT NULL AND salemember1 != '' THEN normalmoney/2.00 ELSE normalmoney END) AS total 
FROM cc_contract 
WHERE createdate >= '${fdate}'::date AND createdate <= '${tdate}'::date 
AND (salemember = ${fld:userlogin} OR salemember1 = ${fld:userlogin}) 
AND contracttype != 3 AND type IN (0,5,7,9,11,2) 
AND org_id = ${fld:org_id} 
AND status >= 2
