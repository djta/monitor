SELECT
	a.tuid,
	a.entity_id,
	s.userlogin,
	s.name,
	o.org_name
FROM
	hr_authority_list a
LEFT JOIN hr_staff s ON a.entity_value = s.userlogin
LEFT JOIN hr_org o ON o.org_id = s.org_id
where 
	a.entity_id = ${fld:entity_id}
${filter}
${orderby}