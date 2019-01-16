SELECT COUNT(1) AS total 
FROM cc_guest_visit 
WHERE visitdate >= '${fdate}'::date AND visitdate <= '${tdate}'::date 
AND mc = ${fld:userlogin} 
AND org_id = ${fld:org_id} 
AND status != 0 
