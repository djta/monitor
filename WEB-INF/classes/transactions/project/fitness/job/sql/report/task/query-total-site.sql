SELECT COUNT(1) AS total 
FROM cc_contract 
WHERE createdate >= '${fdate}'::date AND createdate <= '${tdate}'::date 
AND (salemember = ${fld:userlogin} OR salemember1 = ${fld:userlogin}) 
AND contracttype != 3 AND type = 2 
AND source = '3' 
AND org_id = ${fld:org_id} 
AND status >= 2
