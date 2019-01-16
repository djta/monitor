SELECT 
	r.tuid::varchar AS code,
	t.cabinetcode,
	r.enddate,
	(r.enddate - {d '${def:date}'}) AS extdays,
	r.customercode,
	f.user_id,
	f.userlogin AS custuserlogin,
	c.name AS custname,
	r.createdby AS staffuserlogin,
	f1.name AS staffname 
FROM cc_cabinet_rent r 
INNER JOIN cc_cabinet t ON t.tuid = r.cabinetid AND t.org_id = r.org_id 
INNER JOIN cc_customer c ON c.code = r.customercode AND c.org_id = r.org_id 
INNER JOIN hr_staff f ON f.user_id = c.user_id 
LEFT JOIN hr_staff f1 ON f1.userlogin = r.createdby 
WHERE r.enddate <= {d '${def:date}'}+5 
AND r.status = 1 AND t.status = 1 AND t.physics_status = 1 AND r.is_deleted = 0 
AND NOT EXISTS(
	SELECT 1 FROM cc_message m 
	WHERE m.issystem = 3 AND m.pk_value = r.tuid::varchar 
	AND {d '${def:date}'} = m.sendtime::date AND m.org_id = r.org_id 
)
