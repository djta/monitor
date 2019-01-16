SELECT COUNT(1) AS total 
FROM cc_ptlog g 
WHERE '${fdate}'::date <= g.created::date AND '${tdate}'::date >= g.created::date 
AND g.createdby = ${fld:userlogin} 
AND g.org_id = ${fld:org_id} 
AND g.status != 0 
