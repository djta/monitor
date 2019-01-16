SELECT COUNT(1) AS total 
FROM cc_ptlog g 
INNER JOIN cc_ptdef d ON d.code = g.ptlevelcode AND g.org_id = d.org_id 
WHERE '${fdate}'::date <= g.created::date AND '${tdate}'::date >= g.created::date 
AND g.createdby = ${fld:userlogin} 
AND g.org_id = ${fld:org_id} 
AND d.reatetype = 1 
AND g.status != 0 
