SELECT COUNT(1) AS total 
FROM cc_guest 
WHERE '${fdate}'::date <= created::date AND '${tdate}'::date >= created::date 
AND initmc = ${fld:userlogin}
AND org_id = ${fld:org_id} 
AND status != 0 
