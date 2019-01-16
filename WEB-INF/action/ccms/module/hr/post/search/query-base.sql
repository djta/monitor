SELECT
	p.post_id as tuid,
	p.post_name,
	p.show_order,
	p.created,
	p.createdby
FROM
	hr_post p
WHERE
	p.tenantry_id = ${def:tenantry}

${filter}
${orderby}   
