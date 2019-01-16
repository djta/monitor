SELECT COUNT(1) AS total 
FROM cc_return_log 
WHERE '${fdate}'::date <= created::date AND '${tdate}'::date >= created::date 
AND followby = ${fld:userlogin} 
AND org_id = ${fld:org_id} 
AND status != 0 
