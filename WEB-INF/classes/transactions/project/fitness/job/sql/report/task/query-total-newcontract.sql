SELECT COUNT(1) AS total 
FROM cc_contract createdate
WHERE createdate >= '${fdate}'::date AND createdate <= '${tdate}'::date 
AND (salemember = ${fld:userlogin} OR salemember1 = ${fld:userlogin}) 
AND contracttype != 3 AND type IN (0,5,7,9,11,2) 
AND org_id = ${fld:org_id} 
AND status >= 2
