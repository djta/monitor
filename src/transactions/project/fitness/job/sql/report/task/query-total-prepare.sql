SELECT COUNT(1) AS total 
FROM cc_prepare 
WHERE '${fdate}'::date <= created::date AND '${tdate}'::date >= created::date 
AND createdby = ${fld:userlogin} 
AND org_id = ${fld:org_id} 
AND status != 0 
